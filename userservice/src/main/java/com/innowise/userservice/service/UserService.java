package com.innowise.userservice.service;

import com.innowise.userservice.model.UserDto;
import java.util.List;

/**
 * Service interface for managing users.
 * <p>
 * Defines the contract for user-related operations such as creation, retrieval,
 * updating, and deletion. Implementations of this interface will contain the
 * business logic for these operations.
 * </p>
 * @author Evgeniy Zaleshchenok
 */
public interface UserService {

    /**
     * Creates a new user.
     * <p>
     * The method checks if a user with the given email already exists.
     * If not, it saves the new user to the database.
     * </p>
     *
     * @param createUserRequestDto DTO containing the details of the user to be created. Must not be null.
     * @return DTO of the newly created user, including their generated ID.
     * @throws com.innowise.userservice.exception.EmailAlreadyExistException if a user with the specified email already exists.
     */
    UserDto createUser(UserDto createUserRequestDto);

    /**
     * Retrieves a user by their unique ID.
     *
     * @param id The unique identifier of the user. Must not be null.
     * @return DTO of the found user.
     * @throws com.innowise.userservice.exception.UserNotFoundException if no user is found with the given ID.
     */
    UserDto getUserById(Long id);

    /**
     * Retrieves a list of all users.
     *
     * @return A list of DTOs for all users in the system. Returns an empty list if no users exist.
     */
    List<UserDto> getAllUsers();

    /**
     * Retrieves a list of users based on a collection of IDs.
     * <p>
     * This method efficiently fetches multiple users at once.
     * It checks both the cache and the database.
     * </p>
     *
     * @param ids A list of user IDs to retrieve. Must not be null.
     * @return A list of DTOs for the found users. The size may be less than the input list if some users were not found.
     * @throws com.innowise.userservice.exception.UserNotFoundException if any of the requested IDs do not correspond to an existing user.
     */
    List<UserDto> getUsersByIds(List<Long> ids);

    /**
     * Retrieves a user by their email address.
     *
     * @param email The email address to search for. Must not be null or empty.
     * @return DTO of the found user.
     * @throws com.innowise.userservice.exception.UserNotFoundException if no user is found with the given email.
     */
    UserDto getUserByEmail(String email);

    /**
     * Updates an existing user's information.
     *
     * @param id The ID of the user to update.
     * @param updateUserRequestDto DTO containing the new details for the user. Fields that are null will be ignored.
     * @return DTO of the updated user.
     * @throws com.innowise.userservice.exception.UserNotFoundException if no user is found with the given ID.
     */
    UserDto updateUser(Long id, UserDto updateUserRequestDto);

    /**
     * Deletes a user by their ID.
     *
     * @param id The ID of the user to delete.
     * @throws com.innowise.userservice.exception.UserNotFoundException if no user is found with the given ID.
     */
    void deleteUser(Long id);
}