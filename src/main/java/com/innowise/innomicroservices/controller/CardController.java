package com.innowise.innomicroservices.controller;

import com.innowise.innomicroservices.dto.CardResponseDto;
import com.innowise.innomicroservices.dto.CreateCardRequestDto;
import com.innowise.innomicroservices.dto.UpdateCardRequestDto;
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
    public ResponseEntity<CardResponseDto> createCard(@RequestBody CreateCardRequestDto createCardRequestDto) {
        CardResponseDto cardResponseDto = cardService.createCard(createCardRequestDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(cardResponseDto);
    }

    @GetMapping("/{id}")
    public ResponseEntity<CardResponseDto> getCardById(@PathVariable Long id) {
        CardResponseDto cardResponseDto = cardService.getCard(id);
        return ResponseEntity.ok(cardResponseDto);
    }

    @GetMapping("/batch")
    public ResponseEntity<List<CardResponseDto>> getAllCards(@RequestParam List<Long> ids) {
        List<CardResponseDto> cardResponseDtos = cardService.getCardsByIds(ids);
        return ResponseEntity.ok(cardResponseDtos);
    }

    @PutMapping("/{id}")
    public ResponseEntity<CardResponseDto> updateCard(@PathVariable Long id,
                                                      @RequestBody UpdateCardRequestDto updateCardRequestDto) {
        CardResponseDto cardResponseDto = cardService.updateCard(id, updateCardRequestDto);
        return ResponseEntity.ok(cardResponseDto);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCard(@PathVariable Long id) {
        cardService.deleteCard(id);
        return ResponseEntity.noContent().build();
    }
}
