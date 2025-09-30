package com.innowise.innomicroservices.service;

import com.innowise.innomicroservices.dto.UserDTO;
import com.innowise.innomicroservices.exception.UserNotFoundException;
import com.innowise.innomicroservices.mapper.UserMapper;
import com.innowise.innomicroservices.model.User;
import com.innowise.innomicroservices.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @author Evgeniy Zaleshchenok
 */
@Service
public class UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final CacheManager cacheManager;

    @Autowired
    public UserService(UserRepository userRepository, UserMapper userMapper, CacheManager cacheManager) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
        this.cacheManager = cacheManager;
    }

    @Transactional
    public UserDTO createUser(UserDTO createUserRequestDto) {
        User user = userMapper.toEntity(createUserRequestDto);
        User savedUser = userRepository.save(user);
        return userMapper.toResponseDto(savedUser);
    }

    @Transactional(readOnly = true)
    @Cacheable(value = "users", key = "#id")
    public UserDTO getUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("User not found with id: " + id));
        return userMapper.toResponseDto(user);
    }

    @Transactional
    public List<UserDTO> getAllUsers() {
        List<User> users = userRepository.findAll();
        return users.stream()
                .map(userMapper::toResponseDto)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<UserDTO> getUsersByIds(List<Long> ids) {
        return userRepository.findUsersByIds(ids).stream()
                .map(userMapper::toResponseDto)
                .toList();
    }

    @Transactional(readOnly = true)
    @Cacheable(value = "users", key = "#email")
    public UserDTO getUserByEmail(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("User not found with email: " + email));
        return userMapper.toResponseDto(user);
    }

    @Transactional
    @CachePut(value = "users", key = "#id")
    public UserDTO updateUser(Long id, UserDTO updateUserRequestDto) {
        User userToUpdate = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("User not found with id: " + id));

        if (updateUserRequestDto.getName() != null) {
            userToUpdate.setName(updateUserRequestDto.getName());
        }
        if (updateUserRequestDto.getSurname() != null) {
            userToUpdate.setSurname(updateUserRequestDto.getSurname());
        }
        if (updateUserRequestDto.getEmail() != null) {
            userToUpdate.setEmail(updateUserRequestDto.getEmail());
        }
        if (updateUserRequestDto.getBirthDate() != null) {
            userToUpdate.setBirthDate(updateUserRequestDto.getBirthDate());
        }
        userRepository.save(userToUpdate);
        return userMapper.toResponseDto(userToUpdate);
    }

    @Transactional
    @CacheEvict(value = "users", key = "#id")
    public void deleteUser(Long id) {
        if (!userRepository.existsById(id)) {
            throw new RuntimeException("User not found with id: " + id);
        }
        userRepository.deleteById(id);
    }
}
