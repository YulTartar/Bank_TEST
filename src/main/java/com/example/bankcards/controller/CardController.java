package com.example.bankcards.controller;

import com.example.bankcards.dto.CardDto;
import com.example.bankcards.dto.CreateCardRequest;
import com.example.bankcards.dto.TransferRequest;
import com.example.bankcards.entity.CardStatus;
import com.example.bankcards.entity.Role;
import com.example.bankcards.entity.User;
import com.example.bankcards.security.CustomUserDetails;
import com.example.bankcards.service.CardService;
import com.example.bankcards.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/cards")
public class CardController {

    private final CardService cardService;

    @Autowired
    public CardController(CardService cardService) {
        this.cardService = cardService;
    }


    @PostMapping
    public ResponseEntity<CardDto> createCard(@RequestBody @Valid CreateCardRequest request,
                                              @AuthenticationPrincipal CustomUserDetails userDetails) {
        CardDto dto = cardService.createCard(request, userDetails.getUserEntity());
        return ResponseEntity.ok(dto);
    }





    @PostMapping("/transfer")
    public ResponseEntity<Void> transfer(@RequestBody @Valid TransferRequest request,
                                         @AuthenticationPrincipal CustomUserDetails userDetails) {
        cardService.transferBetweenCards(request, userDetails.getUserEntity());
        return ResponseEntity.ok().build();
    }


    @PostMapping("/{id}/block")
    public ResponseEntity<Void> block(@PathVariable Long id,
                                      @AuthenticationPrincipal User user) {
        cardService.blockCard(id, user);
        return ResponseEntity.ok().build();
    }


    @PostMapping("/{id}/activate")
    public ResponseEntity<Void> activate(@PathVariable Long id,
                                         @AuthenticationPrincipal User user) {
        cardService.activateCard(id, user);
        return ResponseEntity.ok().build();
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCard(@PathVariable Long id,
                                           @AuthenticationPrincipal CustomUserDetails userDetails) {
        if (userDetails.getUserEntity().getRole() != Role.ADMIN) {
            return ResponseEntity.status(403).build();
        }
        cardService.deleteCard(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping
    public ResponseEntity<Page<CardDto>> getMyCards(@AuthenticationPrincipal CustomUserDetails userDetails,
                                                    @RequestParam(defaultValue = "0") int page,
                                                    @RequestParam(defaultValue = "10") int size,
                                                    @RequestParam(required = false) CardStatus status) {
        Page<CardDto> cards = cardService.getUserCards(
                userDetails.getUserEntity(),
                PageRequest.of(page, size),
                status
        );
        return ResponseEntity.ok(cards);
    }

    @GetMapping("/admin/all")
    public ResponseEntity<Page<CardDto>> getAllCards(@AuthenticationPrincipal CustomUserDetails userDetails,
                                                     @RequestParam(defaultValue = "0") int page,
                                                     @RequestParam(defaultValue = "10") int size) {
        if (userDetails.getUserEntity().getRole() != Role.ADMIN) {
            return ResponseEntity.status(403).build();
        }

        Page<CardDto> cards = cardService.getAllCards(PageRequest.of(page, size));
        return ResponseEntity.ok(cards);
    }




}
