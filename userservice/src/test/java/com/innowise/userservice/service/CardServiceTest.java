package com.innowise.userservice.service;

import com.innowise.userservice.model.CardDto;
import com.innowise.userservice.exception.CardNotFoundException;
import com.innowise.userservice.exception.UserNotFoundException;
import com.innowise.userservice.mapper.CardMapper;
import com.innowise.userservice.model.entity.Card;
import com.innowise.userservice.model.entity.User;
import com.innowise.userservice.repository.CardRepository;
import com.innowise.userservice.repository.UserRepository;
import com.innowise.userservice.service.impl.CardServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class CardServiceTest {

    @Mock
    private CardRepository cardRepository;
    @Mock
    private CardMapper cardMapper;
    @Mock
    private UserRepository userRepository;
    @Mock
    private CacheManager cacheManager;
    @Mock
    private Cache cardsCache;

    @InjectMocks
    private CardServiceImpl cardService;

    private User user;
    private Card card;
    private CardDto cardDto;
    private Long cardId;
    private Long userId;

    @BeforeEach
    void setUp() {
        userId = 1L;
        cardId = 10L;

        user = new User();
        user.setId(userId);

        card = new Card();
        card.setId(cardId);
        card.setCardNumber("1234-5678-9012-3456");
        card.setUser(user);

        cardDto = new CardDto();
        cardDto.setId(cardId);
        cardDto.setCardNumber("1234-5678-9012-3456");
        cardDto.setUserId(userId);
    }

    @Test
    void createCard_withValidData_returnsSavedCardDto() {
        Mockito.when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        Mockito.when(cardMapper.toEntity(cardDto)).thenReturn(card);
        Mockito.when(cardRepository.save(card)).thenReturn(card);
        Mockito.when(cardMapper.toResponseDto(card)).thenReturn(cardDto);

        CardDto result = cardService.createCard(cardDto);

        assertNotNull(result);
        assertEquals(cardDto.getId(), result.getId());
        Mockito.verify(cardRepository).save(card);
    }

    @Test
    void createCard_whenUserNotFound_throwsUserNotFoundException() {
        Mockito.when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> cardService.createCard(cardDto));
        Mockito.verify(cardRepository, Mockito.never()).save(Mockito.any());
    }

    @Test
    void getCard_whenCardExists_returnsCardDto() {
        Mockito.when(cardRepository.findById(cardId)).thenReturn(Optional.of(card));
        Mockito.when(cardMapper.toResponseDto(card)).thenReturn(cardDto);

        CardDto result = cardService.getCard(cardId);

        assertNotNull(result);
        assertEquals(cardId, result.getId());
        Mockito.verify(cardRepository).findById(cardId);
    }

    @Test
    void getCard_whenCardNotFound_throwsCardNotFoundException() {
        Mockito.when(cardRepository.findById(cardId)).thenReturn(Optional.empty());
        assertThrows(CardNotFoundException.class, () -> cardService.getCard(cardId));
    }

    @Test
    void getAllCards_whenCardsExist_returnsCardDtoList() {
        Mockito.when(cardRepository.findAll()).thenReturn(List.of(card));
        Mockito.when(cardMapper.toResponseDto(card)).thenReturn(cardDto);

        List<CardDto> result = cardService.getAllCards();

        assertThat(result).hasSize(1).contains(cardDto);
        Mockito.verify(cardRepository).findAll();
    }

    @Test
    void getAllCards_whenNoCardsExist_returnsEmptyList() {
        Mockito.when(cardRepository.findAll()).thenReturn(Collections.emptyList());

        List<CardDto> result = cardService.getAllCards();

        assertThat(result).isEmpty();
        Mockito.verify(cardMapper, Mockito.never()).toResponseDto(Mockito.any());
    }

    @Test
    void getCardsByIds_withPartialCacheHit_fetchesMissingAndCombines() {
        Long cachedCardId = 10L;
        Long missingCardId = 20L;

        CardDto cachedDto = new CardDto();
        cachedDto.setId(cachedCardId);

        Card missingCard = new Card();
        missingCard.setId(missingCardId);
        CardDto missingDto = new CardDto();
        missingDto.setId(missingCardId);

        Mockito.when(cacheManager.getCache("cards")).thenReturn(cardsCache);
        Mockito.when(cardsCache.get(cachedCardId, CardDto.class)).thenReturn(cachedDto);
        Mockito.when(cardsCache.get(missingCardId, CardDto.class)).thenReturn(null);

        List<Long> idsToFetch = List.of(missingCardId);
        Mockito.when(cardRepository.findCardsByIds(idsToFetch)).thenReturn(List.of(missingCard));
        Mockito.when(cardMapper.toResponseDto(missingCard)).thenReturn(missingDto);

        List<CardDto> result = cardService.getCardsByIds(List.of(cachedCardId, missingCardId));

        assertThat(result).hasSize(2).containsExactlyInAnyOrder(cachedDto, missingDto);
        Mockito.verify(cardRepository).findCardsByIds(idsToFetch);
        Mockito.verify(cardsCache).put(missingCardId, missingDto);
    }

    @Test
    void updateCard_whenCardExists_returnsUpdatedCardDto() {
        CardDto updateRequest = new CardDto();
        updateRequest.setCardNumber("9999-8888-7777-6666");

        Mockito.when(cardRepository.findById(cardId)).thenReturn(Optional.of(card));
        Mockito.when(cardRepository.save(card)).thenReturn(card);
        Mockito.when(cardMapper.toResponseDto(card)).thenReturn(updateRequest);

        CardDto result = cardService.updateCard(cardId, updateRequest);

        assertEquals(updateRequest.getCardNumber(), result.getCardNumber());
        Mockito.verify(cardRepository).save(card);
    }

    @Test
    void updateCard_whenCardNotFound_throwsCardNotFoundException() {
        Mockito.when(cardRepository.findById(cardId)).thenReturn(Optional.empty());

        assertThrows(CardNotFoundException.class, () -> cardService.updateCard(cardId, cardDto));
        Mockito.verify(cardRepository, Mockito.never()).save(Mockito.any());
    }

    @Test
    void deleteCard_whenCardExists_deletesSuccessfully() {
        Mockito.when(cardRepository.existsById(cardId)).thenReturn(true);
        Mockito.doNothing().when(cardRepository).deleteById(cardId);

        assertDoesNotThrow(() -> cardService.deleteCard(cardId));

        Mockito.verify(cardRepository).existsById(cardId);
        Mockito.verify(cardRepository).deleteById(cardId);
    }

    @Test
    void deleteCard_whenCardNotFound_throwsCardNotFoundException() {
        Mockito.when(cardRepository.existsById(cardId)).thenReturn(false);

        assertThrows(CardNotFoundException.class, () -> cardService.deleteCard(cardId));
        Mockito.verify(cardRepository, Mockito.never()).deleteById(Mockito.any());
    }
}