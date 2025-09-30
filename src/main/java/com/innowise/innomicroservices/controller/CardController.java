package com.innowise.innomicroservices.controller;

import com.innowise.innomicroservices.dto.CardDTO;
import com.innowise.innomicroservices.service.CardService;
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
    public ResponseEntity<CardDTO> createCard(@RequestBody CardDTO createCardRequestDto) {
        CardDTO cardResponseDto = cardService.createCard(createCardRequestDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(cardResponseDto);
    }

    @GetMapping("/{id}")
    public ResponseEntity<CardDTO> getCardById(@PathVariable Long id) {
        CardDTO cardResponseDto = cardService.getCard(id);
        return ResponseEntity.ok(cardResponseDto);
    }

    @GetMapping
    public ResponseEntity<List<CardDTO>> getAllCards() {
        List<CardDTO> cardResponseDto = cardService.getAllCards();
        return ResponseEntity.ok(cardResponseDto);
    }

    @GetMapping("/batch")
    public ResponseEntity<List<CardDTO>> getAllCards(@RequestParam List<Long> ids) {
        List<CardDTO> cardResponseDtos = cardService.getCardsByIds(ids);
        return ResponseEntity.ok(cardResponseDtos);
    }

    @PutMapping("/{id}")
    public ResponseEntity<CardDTO> updateCard(@PathVariable Long id,
                                                      @RequestBody CardDTO updateCardRequestDto) {
        CardDTO cardResponseDto = cardService.updateCard(id, updateCardRequestDto);
        return ResponseEntity.ok(cardResponseDto);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCard(@PathVariable Long id) {
        cardService.deleteCard(id);
        return ResponseEntity.noContent().build();
    }
}
