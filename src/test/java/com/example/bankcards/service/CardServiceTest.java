package com.example.bankcards.service;

import com.example.bankcards.dto.CreateCardRequest;
import com.example.bankcards.dto.TransferRequest;
import com.example.bankcards.entity.*;
import com.example.bankcards.repository.CardRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class CardServiceTest {

    private CardRepository cardRepository;
    private CardService cardService;
    private User testUser;

    @BeforeEach
    public void setup() {
        cardRepository = mock(CardRepository.class);
        cardService = new com.example.bankcards.service.impl.CardServiceImpl(cardRepository);

        testUser = new User();
        testUser.setId(1L);
        testUser.setUsername("user");
        testUser.setRole(Role.USER);
    }

    @Test
    public void testCreateCard_success() {
        CreateCardRequest request = new CreateCardRequest();
        request.setNumber("1234567890123456");
        request.setOwner("Иван Иванов");
        request.setExpirationDate(LocalDate.now().plusYears(2));

        when(cardRepository.existsByEncryptedNumber(anyString())).thenReturn(false);

        cardService.createCard(request, testUser);

        ArgumentCaptor<Card> captor = ArgumentCaptor.forClass(Card.class);
        verify(cardRepository).save(captor.capture());

        Card saved = captor.getValue();
        assertEquals(CardStatus.ACTIVE, saved.getStatus());
        assertEquals(BigDecimal.ZERO, saved.getBalance());
        assertEquals("Иван Иванов", saved.getOwner());
        assertEquals(testUser, saved.getUser());
    }

    @Test
    public void testTransferBetweenCards_success() {
        Card from = new Card();
        from.setId(1L);
        from.setUser(testUser);
        from.setBalance(new BigDecimal("500"));
        from.setStatus(CardStatus.ACTIVE);

        Card to = new Card();
        to.setId(2L);
        to.setUser(testUser);
        to.setBalance(new BigDecimal("100"));
        to.setStatus(CardStatus.ACTIVE);

        when(cardRepository.findByIdAndUser(1L, testUser)).thenReturn(Optional.of(from));
        when(cardRepository.findByIdAndUser(2L, testUser)).thenReturn(Optional.of(to));

        TransferRequest request = new TransferRequest();
        request.setFromCardId(1L);
        request.setToCardId(2L);
        request.setAmount(new BigDecimal("200"));

        cardService.transferBetweenCards(request, testUser);

        assertEquals(new BigDecimal("300"), from.getBalance());
        assertEquals(new BigDecimal("300"), to.getBalance());
    }

    @Test
    public void testBlockCard_success() {
        Card card = new Card();
        card.setId(1L);
        card.setUser(testUser);
        card.setStatus(CardStatus.ACTIVE);

        when(cardRepository.findByIdAndUser(1L, testUser)).thenReturn(Optional.of(card));

        cardService.blockCard(1L, testUser);

        assertEquals(CardStatus.BLOCKED, card.getStatus());
    }

    @Test
    public void testActivateCard_success() {
        Card card = new Card();
        card.setId(1L);
        card.setUser(testUser);
        card.setStatus(CardStatus.BLOCKED);

        when(cardRepository.findByIdAndUser(1L, testUser)).thenReturn(Optional.of(card));

        cardService.activateCard(1L, testUser);

        assertEquals(CardStatus.ACTIVE, card.getStatus());
    }
}
