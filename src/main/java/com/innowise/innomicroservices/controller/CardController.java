package com.innowise.innomicroservices.controller;

import com.innowise.innomicroservices.model.CardDto;
import com.innowise.innomicroservices.service.CardService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author Evgeniy Zaleshchenok
 */
@RestController
@RequestMapping("/cards")
public class CardController {
    private final CardService cardService;

    @Autowired
    public CardController(CardService cardService) {
        this.cardService = cardService;
    }

    @PostMapping
    public ResponseEntity<CardDto> createCard(@Valid @RequestBody CardDto createCardRequestDto) {
        CardDto cardResponseDto = cardService.createCard(createCardRequestDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(cardResponseDto);
    }

    @GetMapping("/{id}")
    public ResponseEntity<CardDto> getCardById(@PathVariable Long id) {
        CardDto cardResponseDto = cardService.getCard(id);
        return ResponseEntity.ok(cardResponseDto);
    }

    @GetMapping
    public ResponseEntity<List<CardDto>> getAllCards() {
        List<CardDto> cardResponseDto = cardService.getAllCards();
        return ResponseEntity.ok(cardResponseDto);
    }

    @GetMapping("/batch")
    public ResponseEntity<List<CardDto>> getAllCards(@RequestParam List<Long> ids) {
        List<CardDto> cardResponseDtos = cardService.getCardsByIds(ids);
        return ResponseEntity.ok(cardResponseDtos);
    }

    @PutMapping("/{id}")
    public ResponseEntity<CardDto> updateCard(@PathVariable Long id,
                                              @Valid @RequestBody CardDto updateCardRequestDto) {
        CardDto cardResponseDto = cardService.updateCard(id, updateCardRequestDto);
        return ResponseEntity.ok(cardResponseDto);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCard(@PathVariable Long id) {
        cardService.deleteCard(id);
        return ResponseEntity.noContent().build();
    }
}
