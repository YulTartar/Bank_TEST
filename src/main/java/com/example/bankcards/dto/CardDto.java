package com.example.bankcards.dto;

import com.example.bankcards.entity.CardStatus;

import java.math.BigDecimal;
import java.time.LocalDate;

public class CardDto {

    private Long id;
    private String maskedNumber;
    private String owner;
    private LocalDate expirationDate;
    private CardStatus status;
    private BigDecimal balance;

    public CardDto() {}

    public CardDto(Long id, String maskedNumber, String owner, LocalDate expirationDate, CardStatus status, BigDecimal balance) {
        this.id = id;
        this.maskedNumber = maskedNumber;
        this.owner = owner;
        this.expirationDate = expirationDate;
        this.status = status;
        this.balance = balance;
    }


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getMaskedNumber() {
        return maskedNumber;
    }

    public void setMaskedNumber(String maskedNumber) {
        this.maskedNumber = maskedNumber;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public LocalDate getExpirationDate() {
        return expirationDate;
    }

    public void setExpirationDate(LocalDate expirationDate) {
        this.expirationDate = expirationDate;
    }

    public CardStatus getStatus() {
        return status;
    }

    public void setStatus(CardStatus status) {
        this.status = status;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }
}
