package com.innowise.innomicroservices.service;

import com.innowise.innomicroservices.dto.CardResponseDto;
import com.innowise.innomicroservices.dto.CreateCardRequestDto;
import com.innowise.innomicroservices.dto.UpdateCardRequestDto;
import com.innowise.innomicroservices.exception.CardNotFoundException;
import com.innowise.innomicroservices.mapper.CardMapper;
import com.innowise.innomicroservices.model.Card;
import com.innowise.innomicroservices.repository.CardRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @author Evgeniy Zaleshchenok
 */
@Service
public class CardService {
    private final CardRepository cardRepository;
    private final CardMapper cardMapper;

    @Autowired
    public CardService(CardRepository cardRepository, CardMapper cardMapper) {
        this.cardRepository = cardRepository;
        this.cardMapper = cardMapper;
    }

    @Transactional
    public CardResponseDto createCard(CreateCardRequestDto createCardRequestDto) {
        Card card = cardMapper.toEntity(createCardRequestDto);
        Card savedCard = cardRepository.save(card);
        return cardMapper.toResponseDto(savedCard);
    }

    @Transactional(readOnly = true)
    public CardResponseDto getCard(Long cardId) {
        Card card = cardRepository.findById(cardId)
                .orElseThrow(() -> new CardNotFoundException("Card not found with id " + cardId));
        return cardMapper.toResponseDto(card);
    }

    @Transactional(readOnly = true)
    public List<CardResponseDto> getCardsByIds(List<Long> cardIds) {
        return cardRepository.findCardsByIds(cardIds).stream()
                .map(cardMapper::toResponseDto)
                .toList();
    }

    @Transactional
    public CardResponseDto updateCard(Long cardId, UpdateCardRequestDto updateCardRequestDto) {
        Card cardToUpdate = cardRepository.findById(cardId)
                .orElseThrow(() -> new CardNotFoundException("Card not found with id " + cardId));

        if (updateCardRequestDto.getCardNumber() != null) {
            cardToUpdate.setCardNumber(updateCardRequestDto.getCardNumber());
        }
        if (updateCardRequestDto.getExpiryDate() != null) {
            cardToUpdate.setExpiryDate(updateCardRequestDto.getExpiryDate());
        }
        if (updateCardRequestDto.getHolder() != null) {
            cardToUpdate.setHolder(updateCardRequestDto.getHolder());
        }
        cardRepository.save(cardToUpdate);
        return cardMapper.toResponseDto(cardToUpdate);
    }

    @Transactional
    public void deleteCard(Long cardId) {
        cardRepository.deleteById(cardId);
    }
}
