package com.example.bankcards.controller;

import com.example.bankcards.dto.CardDto;
import com.example.bankcards.dto.CreateCardRequest;
import com.example.bankcards.dto.TransferRequest;
import com.example.bankcards.entity.CardStatus;
import com.example.bankcards.entity.Role;
import com.example.bankcards.entity.User;
import com.example.bankcards.security.CustomUserDetails;
import com.example.bankcards.service.CardService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CardController.class)
public class CardControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CardService cardService;

    @Autowired
    private ObjectMapper objectMapper;

    private CustomUserDetails getTestUserDetails() {
        User user = new User();
        user.setId(1L);
        user.setUsername("user");
        user.setRole(Role.USER);
        return new CustomUserDetails(user);
    }

    @Test
    @WithMockUser(username = "user", roles = {"USER"})
    public void testCreateCard() throws Exception {
        CreateCardRequest request = new CreateCardRequest();
        request.setNumber("1234567812345678");
        request.setOwner("Иван Иванов");
        request.setExpirationDate(LocalDate.now().plusYears(1));

        CardDto dto = new CardDto(1L, "**** **** **** 5678", "Иван Иванов",
                request.getExpirationDate(), CardStatus.ACTIVE, BigDecimal.ZERO);

        Mockito.when(cardService.createCard(any(), any())).thenReturn(dto);

        mockMvc.perform(post("/api/cards")
                        .with(SecurityMockMvcRequestPostProcessors.user(getTestUserDetails()))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.maskedNumber").value("**** **** **** 5678"));

        verify(cardService).createCard(any(), any());
    }

    @Test
    @WithMockUser(username = "user", roles = {"USER"})
    public void testTransferBetweenCards() throws Exception {
        TransferRequest request = new TransferRequest();
        request.setFromCardId(1L);
        request.setToCardId(2L);
        request.setAmount(new BigDecimal("100.00"));

        mockMvc.perform(post("/api/cards/transfer")
                        .with(SecurityMockMvcRequestPostProcessors.user(getTestUserDetails()))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());

        verify(cardService).transferBetweenCards(any(), any());
    }

    @Test
    @WithMockUser(username = "user", roles = {"USER"})
    public void testGetMyCards() throws Exception {
        CardDto card = new CardDto(1L, "**** **** **** 5678", "Иван Иванов",
                LocalDate.now().plusYears(1), CardStatus.ACTIVE, new BigDecimal("1000.00"));

        Mockito.when(cardService.getUserCards(any(), any(), any()))
                .thenReturn(new PageImpl<>(List.of(card), PageRequest.of(0, 10), 1));

        mockMvc.perform(get("/api/cards?status=ACTIVE&page=0&size=10")
                        .with(SecurityMockMvcRequestPostProcessors.user(getTestUserDetails())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].maskedNumber").value("**** **** **** 5678"));
    }
}
