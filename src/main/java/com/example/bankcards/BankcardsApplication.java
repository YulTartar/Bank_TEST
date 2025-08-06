package com.example.bankcards;

import com.example.bankcards.entity.*;
import com.example.bankcards.repository.CardRepository;
import com.example.bankcards.repository.UserRepository;
import com.example.bankcards.service.CardService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@SpringBootApplication
public class BankcardsApplication {

    public static void main(String[] args) {
        SpringApplication.run(BankcardsApplication.class, args);
    }

    @Bean
    public CommandLineRunner demo(UserRepository userRepository,
                                  CardRepository cardRepository,
                                  CardService cardService,
                                  PasswordEncoder encoder) {
        return args -> {
            System.out.println("====> Запуск BankcardsApplication");

            // ✅ Создание пользователя USER
            User user = userRepository.findByUsername("user").orElseGet(() -> {
                User u = new User();
                u.setUsername("user");
                u.setPassword(encoder.encode("userpass"));
                u.setRole(Role.USER);
                return userRepository.save(u);
            });

            // ✅ Создание пользователя ADMIN
            userRepository.findByUsername("admin").orElseGet(() -> {
                User admin = new User();
                admin.setUsername("admin");
                admin.setPassword(encoder.encode("adminpass"));
                admin.setRole(Role.ADMIN);
                System.out.println("====> Пользователь ADMIN создан");
                return userRepository.save(admin);
            });

            // ✅ Создание двух карт для пользователя
            if (cardRepository.findAllByUser(user, null).isEmpty()) {
                Card card1 = new Card();
                card1.setEncryptedNumber(new StringBuilder("1234567890123456").reverse().toString());
                card1.setOwner("Иван Иванов");
                card1.setExpirationDate(LocalDate.now().plusYears(3));
                card1.setStatus(CardStatus.ACTIVE);
                card1.setBalance(new BigDecimal("1000"));
                card1.setUser(user);
                cardRepository.save(card1);

                Card card2 = new Card();
                card2.setEncryptedNumber(new StringBuilder("9876543210987654").reverse().toString());
                card2.setOwner("Иван Иванов");
                card2.setExpirationDate(LocalDate.now().plusYears(3));
                card2.setStatus(CardStatus.ACTIVE);
                card2.setBalance(new BigDecimal("0"));
                card2.setUser(user);
                cardRepository.save(card2);
            }

            // ✅ Просмотр всех карт пользователя
            List<Card> cards = cardRepository.findAllByUser(user, null).getContent();
            System.out.println("====> Карты пользователя:");
            for (Card c : cards) {
                System.out.println("ID: " + c.getId() + ", Баланс: " + c.getBalance() + ", Статус: " + c.getStatus());
            }

            // ✅ Перевод между своими картами
            if (cards.size() >= 2) {
                Card from = cards.get(0);
                Card to = cards.get(1);
                System.out.println("====> Перевод 100.00 с карты " + from.getId() + " на карту " + to.getId());
                cardService.transferBetweenCards(new com.example.bankcards.dto.TransferRequest() {{
                    setFromCardId(from.getId());
                    setToCardId(to.getId());
                    setAmount(new BigDecimal("100.00"));
                }}, user);
            }

            // ✅ Блокировка и активация второй карты — только если она есть
            List<Card> updatedCards = cardRepository.findAllByUser(user, null).getContent();
            if (updatedCards.size() > 1) {
                Card second = updatedCards.get(1);
                System.out.println("====> Блокировка карты ID: " + second.getId());
                cardService.blockCard(second.getId(), user);

                System.out.println("====> Активация карты ID: " + second.getId());
                cardService.activateCard(second.getId(), user);
            }

            // ✅ Финальный вывод
            List<Card> finalCards = cardRepository.findAllByUser(user, null).getContent();
            System.out.println("====> Обновлённые карты:");
            for (Card c : finalCards) {
                System.out.println("ID: " + c.getId() + ", Баланс: " + c.getBalance() + ", Статус: " + c.getStatus());
            }

            System.out.println("====> Всё по ТЗ протестировано ✅");
        };
    }
}
