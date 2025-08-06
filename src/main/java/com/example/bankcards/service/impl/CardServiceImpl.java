package com.example.bankcards.service.impl;

import com.example.bankcards.dto.CardDto;
import com.example.bankcards.dto.CreateCardRequest;
import com.example.bankcards.dto.TransferRequest;
import com.example.bankcards.entity.Card;
import com.example.bankcards.entity.CardStatus;
import com.example.bankcards.entity.User;
import com.example.bankcards.repository.CardRepository;
import com.example.bankcards.service.CardService;
import com.example.bankcards.util.CardUtil;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class CardServiceImpl implements CardService {

    private final CardRepository cardRepository;

    @Autowired
    public CardServiceImpl(CardRepository cardRepository) {
        this.cardRepository = cardRepository;
    }

    @Override
    public CardDto createCard(CreateCardRequest request, User user) {
        String encrypted = CardUtil.encryptNumber(request.getNumber());
        if (cardRepository.existsByEncryptedNumber(encrypted)) {
            throw new RuntimeException("Карта с таким номером уже существует");
        }

        Card card = new Card();
        card.setEncryptedNumber(encrypted);
        card.setOwner(request.getOwner());
        card.setExpirationDate(request.getExpirationDate());
        card.setStatus(CardStatus.ACTIVE);
        card.setBalance(BigDecimal.ZERO);
        card.setUser(user);

        Card saved = cardRepository.save(card);

        return new CardDto(
                saved.getId(),
                CardUtil.maskNumber(request.getNumber()),
                saved.getOwner(),
                saved.getExpirationDate(),
                saved.getStatus(),
                saved.getBalance()
        );
    }

    @Override
    public Page<CardDto> getUserCards(User user, Pageable pageable) {
        return cardRepository.findAllByUser(user, pageable)
                .map(card -> new CardDto(
                        card.getId(),
                        CardUtil.maskEncrypted(card.getEncryptedNumber()),
                        card.getOwner(),
                        card.getExpirationDate(),
                        card.getStatus(),
                        card.getBalance()
                ));
    }

    @Override
    @Transactional
    public void transferBetweenCards(TransferRequest request, User user) {
        Card from = cardRepository.findByIdAndUser(request.getFromCardId(), user)
                .orElseThrow(() -> new RuntimeException("Исходная карта не найдена"));

        Card to = cardRepository.findByIdAndUser(request.getToCardId(), user)
                .orElseThrow(() -> new RuntimeException("Карта получателя не найдена"));

        if (from.getStatus() != CardStatus.ACTIVE || to.getStatus() != CardStatus.ACTIVE) {
            throw new RuntimeException("Обе карты должны быть активны для перевода");
        }

        if (from.getBalance().compareTo(request.getAmount()) < 0) {
            throw new RuntimeException("Недостаточно средств");
        }

        from.setBalance(from.getBalance().subtract(request.getAmount()));
        to.setBalance(to.getBalance().add(request.getAmount()));
    }

    @Override
    public void blockCard(Long cardId, User user) {
        Card card = cardRepository.findByIdAndUser(cardId, user)
                .orElseThrow(() -> new RuntimeException("Карта не найдена"));
        card.setStatus(CardStatus.BLOCKED);
        cardRepository.save(card);
    }

    @Override
    public void activateCard(Long cardId, User user) {
        Card card = cardRepository.findByIdAndUser(cardId, user)
                .orElseThrow(() -> new RuntimeException("Карта не найдена"));
        card.setStatus(CardStatus.ACTIVE);
        cardRepository.save(card);
    }
    @Override
    public void deleteCard(Long cardId) {
        if (!cardRepository.existsById(cardId)) {
            throw new RuntimeException("Карта не найдена");
        }
        cardRepository.deleteById(cardId);
    }

    @Override
    public Page<CardDto> getUserCards(User user, Pageable pageable, CardStatus status) {
        Page<Card> cards;

        if (status != null) {
            cards = cardRepository.findAllByUserAndStatus(user, status, pageable);
        } else {
            cards = cardRepository.findAllByUser(user, pageable);
        }

        return cards.map(card -> new CardDto(
                card.getId(),
                CardUtil.maskEncrypted(card.getEncryptedNumber()),
                card.getOwner(),
                card.getExpirationDate(),
                card.getStatus(),
                card.getBalance()
        ));
    }

    @Override
    public Page<CardDto> getAllCards(Pageable pageable) {
        return cardRepository.findAll(pageable)
                .map(card -> new CardDto(
                        card.getId(),
                        CardUtil.maskEncrypted(card.getEncryptedNumber()),
                        card.getOwner(),
                        card.getExpirationDate(),
                        card.getStatus(),
                        card.getBalance()
                ));
    }



}
