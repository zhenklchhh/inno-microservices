package com.innowise.innomicroservices.service;

import com.innowise.innomicroservices.dto.CardDTO;
import com.innowise.innomicroservices.exception.CardNotFoundException;
import com.innowise.innomicroservices.exception.UserNotFoundException;
import com.innowise.innomicroservices.mapper.CardMapper;
import com.innowise.innomicroservices.model.Card;
import com.innowise.innomicroservices.model.User;
import com.innowise.innomicroservices.repository.CardRepository;
import com.innowise.innomicroservices.repository.UserRepository;
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
    private final UserRepository userRepository;

    @Autowired
    public CardService(CardRepository cardRepository, CardMapper cardMapper, UserRepository userRepository) {
        this.cardRepository = cardRepository;
        this.cardMapper = cardMapper;
        this.userRepository = userRepository;
    }

    @Transactional
    public CardDTO createCard(CardDTO createCardRequestDto) {
        User user = userRepository.findById(createCardRequestDto.getUserId())
                .orElseThrow(() -> new UserNotFoundException("User with id " + createCardRequestDto.getUserId() + " not found"));
        Card card = cardMapper.toEntity(createCardRequestDto);
        card.setUser(user);
        Card savedCard = cardRepository.save(card);
        return cardMapper.toResponseDto(savedCard);
    }

    @Transactional(readOnly = true)
    public CardDTO getCard(Long cardId) {
        Card card = cardRepository.findById(cardId)
                .orElseThrow(() -> new CardNotFoundException("Card not found with id " + cardId));
        return cardMapper.toResponseDto(card);
    }

    @Transactional
    public List<CardDTO> getAllCards() {
        List<Card> cards = cardRepository.findAll();
        return cards.stream()
                .map(cardMapper::toResponseDto)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<CardDTO> getCardsByIds(List<Long> cardIds) {
        return cardRepository.findCardsByIds(cardIds).stream()
                .map(cardMapper::toResponseDto)
                .toList();
    }

    @Transactional
    public CardDTO updateCard(Long cardId, CardDTO updateCardRequestDto) {
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
        if (!cardRepository.existsById(cardId)) {
            throw new CardNotFoundException("Card not found with id " + cardId);
        }
        cardRepository.deleteById(cardId);
    }
}
