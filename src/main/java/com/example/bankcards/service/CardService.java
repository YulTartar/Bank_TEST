package com.example.bankcards.service;

import com.example.bankcards.dto.CardDto;
import com.example.bankcards.dto.CreateCardRequest;
import com.example.bankcards.dto.TransferRequest;
import com.example.bankcards.entity.CardStatus;
import com.example.bankcards.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface CardService {

    CardDto createCard(CreateCardRequest request, User user);

    Page<CardDto> getUserCards(User user, Pageable pageable);

    void transferBetweenCards(TransferRequest request, User user);

    void blockCard(Long cardId, User user);

    void activateCard(Long cardId, User user);
    void deleteCard(Long cardId);

    Page<CardDto> getUserCards(User user, Pageable pageable, CardStatus status);

    Page<CardDto> getAllCards(Pageable pageable);
}
