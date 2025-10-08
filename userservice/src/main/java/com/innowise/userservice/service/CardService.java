package com.innowise.userservice.service;

import com.innowise.userservice.model.CardDto;
import java.util.List;

/**
 * Service interface for managing user cards.
 * <p>
 * This interface defines the standard operations for managing bank cards,
 * including creation, retrieval, updates, and deletion.
 * </p>
 * @author Evgeniy Zaleshchenok
 */
public interface CardService {

    /**
     * Creates a new card and associates it with a user.
     *
     * @param createCardRequestDto DTO containing card details and the ID of the user who owns the card.
     * @return DTO of the newly created card.
     * @throws com.innowise.userservice.exception.UserNotFoundException if the user specified in the DTO does not exist.
     */
    CardDto createCard(CardDto createCardRequestDto);

    /**
     * Retrieves a card by its unique ID.
     *
     * @param cardId The unique identifier of the card.
     * @return DTO of the found card.
     * @throws com.innowise.userservice.exception.CardNotFoundException if no card is found with the given ID.
     */
    CardDto getCard(Long cardId);

    /**
     * Retrieves a list of all cards in the system.
     *
     * @return A list containing DTOs of all cards. Returns an empty list if none exist.
     */
    List<CardDto> getAllCards();

    /**
     * Retrieves multiple cards by their IDs.
     * <p>
     * The method attempts to fetch cards from the cache first before querying the database for any missing ones.
     * </p>
     *
     * @param cardIds A list of card IDs to retrieve.
     * @return A list of DTOs for the found cards.
     */
    List<CardDto> getCardsByIds(List<Long> cardIds);

    /**
     * Updates an existing card's information.
     *
     * @param cardId The ID of the card to update.
     * @param updateCardRequestDto DTO containing the fields to update. Null fields are ignored.
     * @return DTO of the updated card.
     * @throws com.innowise.userservice.exception.CardNotFoundException if no card is found with the given ID.
     */
    CardDto updateCard(Long cardId, CardDto updateCardRequestDto);

    /**
     * Deletes a card by its ID.
     *
     * @param cardId The ID of the card to delete.
     * @throws com.innowise.userservice.exception.CardNotFoundException if no card is found with the given ID.
     */
    void deleteCard(Long cardId);
}