package com.innowise.userservice.service.impl;

import com.innowise.userservice.model.CardDto;
import com.innowise.userservice.exception.CardNotFoundException;
import com.innowise.userservice.exception.UserNotFoundException;
import com.innowise.userservice.mapper.CardMapper;
import com.innowise.userservice.model.entity.Card;
import com.innowise.userservice.model.entity.User;
import com.innowise.userservice.repository.CardRepository;
import com.innowise.userservice.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Evgeniy Zaleshchenok
 */
@Service
public class CardService implements com.innowise.userservice.service.CardService {
    private final CardRepository cardRepository;
    private final CardMapper cardMapper;
    private final UserRepository userRepository;
    private final CacheManager cacheManager;

    @Autowired
    public CardService(CardRepository cardRepository, CardMapper cardMapper, UserRepository userRepository, CacheManager cacheManager) {
        this.cardRepository = cardRepository;
        this.cardMapper = cardMapper;
        this.userRepository = userRepository;
        this.cacheManager = cacheManager;
    }

    @Transactional
    @CachePut(value = "cards", key="#result.id")
    public CardDto createCard(CardDto createCardRequestDto) {
        User user = userRepository.findById(createCardRequestDto.getUserId())
                .orElseThrow(() -> new UserNotFoundException("User with id " + createCardRequestDto.getUserId() + " not found"));
        Card card = cardMapper.toEntity(createCardRequestDto);
        card.setUser(user);
        Card savedCard = cardRepository.save(card);
        return cardMapper.toResponseDto(savedCard);
    }

    @Transactional(readOnly = true)
    @Cacheable(value = "cards", key = "#cardId")
    public CardDto getCard(Long cardId) {
        Card card = cardRepository.findById(cardId)
                .orElseThrow(() -> new CardNotFoundException("Card not found with id " + cardId));
        return cardMapper.toResponseDto(card);
    }

    @Transactional
    public List<CardDto> getAllCards() {
        List<Card> cards = cardRepository.findAll();
        return cards.stream()
                .map(cardMapper::toResponseDto)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<CardDto> getCardsByIds(List<Long> cardIds) {
        Cache cardsCache = cacheManager.getCache("cards");
        List<CardDto> result = new ArrayList<>();
        List<Long> idsToFetch = new ArrayList<>();
        for (Long cardId : cardIds) {
            CardDto cardDto = cardsCache.get(cardId, CardDto.class);
            if (cardDto == null) {
                idsToFetch.add(cardId);
            }
            else{
                result.add(cardDto);
            }
        }

        if(!idsToFetch.isEmpty()) {
            List<Card> cardList = cardRepository.findCardsByIds(idsToFetch);
            for (Card card : cardList) {
                CardDto cardDto = cardMapper.toResponseDto(card);
                cardsCache.put(card.getId(), cardDto);
                result.add(cardDto);
            }
        }
        return result;
    }

    @Transactional
    @CachePut(value = "cards", key = "#cardId")
    public CardDto updateCard(Long cardId, CardDto updateCardRequestDto) {
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
    @CacheEvict(value = "cards", key = "#cardId")
    public void deleteCard(Long cardId) {
        if (!cardRepository.existsById(cardId)) {
            throw new CardNotFoundException("Card not found with id " + cardId);
        }
        cardRepository.deleteById(cardId);
    }
}
