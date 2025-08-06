package com.example.bankcards.repository;

import com.example.bankcards.entity.Card;
import com.example.bankcards.entity.CardStatus;
import com.example.bankcards.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CardRepository extends JpaRepository<Card, Long> {

    Optional<Card> findByIdAndUser(Long id, User user);

    Page<Card> findAllByUser(User user, Pageable pageable);

    boolean existsByEncryptedNumber(String encryptedNumber);
    Page<Card> findAllByUserAndStatus(User user, CardStatus status, Pageable pageable);

    Page<Card> findAll(Pageable pageable);


}
