package com.innowise.userservice.controller;

import com.innowise.userservice.model.CardDto;
import com.innowise.userservice.service.impl.CardServiceImpl;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author Evgeniy Zaleshchenok
 */
@RestController
@RequestMapping("/cards")
public class CardController {
    private final CardServiceImpl cardService;

    @Autowired
    public CardController(CardServiceImpl cardService) {
        this.cardService = cardService;
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<CardDto> createCard(@Valid @RequestBody CardDto createCardRequestDto) {
        CardDto cardResponseDto = cardService.createCard(createCardRequestDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(cardResponseDto);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    public ResponseEntity<CardDto> getCardById(@PathVariable Long id) {
        CardDto cardResponseDto = cardService.getCard(id);
        return ResponseEntity.ok(cardResponseDto);
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<CardDto>> getAllCards() {
        List<CardDto> cardResponseDto = cardService.getAllCards();
        return ResponseEntity.ok(cardResponseDto);
    }

    @GetMapping(params = "ids")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<CardDto>> getAllCards(@RequestParam List<Long> ids) {
        List<CardDto> cardResponseDtos = cardService.getCardsByIds(ids);
        return ResponseEntity.ok(cardResponseDtos);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<CardDto> updateCard(@PathVariable Long id,
                                              @Valid @RequestBody CardDto updateCardRequestDto) {
        CardDto cardResponseDto = cardService.updateCard(id, updateCardRequestDto);
        return ResponseEntity.ok(cardResponseDto);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteCard(@PathVariable Long id) {
        cardService.deleteCard(id);
        return ResponseEntity.noContent().build();
    }
}
