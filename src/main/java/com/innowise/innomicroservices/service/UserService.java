package com.innowise.innomicroservices.service;

import com.innowise.innomicroservices.dto.UserDto;
import com.innowise.innomicroservices.exception.UserNotFoundException;
import com.innowise.innomicroservices.mapper.UserMapper;
import com.innowise.innomicroservices.model.User;
import com.innowise.innomicroservices.repository.UserRepository;
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
    @CachePut(value = "users", key = "#result.id")
    public UserDto createUser(UserDto createUserRequestDto) {
        User user = userMapper.toEntity(createUserRequestDto);
        User savedUser = userRepository.save(user);
        return userMapper.toResponseDto(savedUser);
    }

    @Transactional(readOnly = true)
    @Cacheable(value = "users", key = "#id")
    public UserDto getUserById(Long id) {
        System.out.println("Fetching user: " + id);
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("User not found with id: " + id));
        return userMapper.toResponseDto(user);
    }

    @Transactional
    public List<UserDto> getAllUsers() {
        List<User> users = userRepository.findAll();
        return users.stream()
                .map(userMapper::toResponseDto)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<UserDto> getUsersByIds(List<Long> ids) {
        Cache usersCache = cacheManager.getCache("users");
        List<UserDto> result = new ArrayList<>();
        List<Long> idsToFetchFromDb = new ArrayList<>();
        for(Long id : ids) {
            UserDto userDto = usersCache.get(id, UserDto.class);
            if (userDto == null) {
                idsToFetchFromDb.add(id);
            }
            else {
                result.add(userDto);
            }
        }
        if (!idsToFetchFromDb.isEmpty()) {
            List<User> fetchedUserDTOS = userRepository.findUsersByIds(idsToFetchFromDb);
            for (User user : fetchedUserDTOS){
                UserDto userDto = userMapper.toResponseDto(user);
                result.add(userDto);
                usersCache.put(user.getId(), userDto);
            }
        }
        return result;
    }

    @Transactional(readOnly = true)
    @Cacheable(value = "users", key = "#email")
    public UserDto getUserByEmail(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("User not found with email: " + email));
        return userMapper.toResponseDto(user);
    }

    @Transactional
    @CachePut(value = "users", key = "#id")
    public UserDto updateUser(Long id, UserDto updateUserRequestDto) {
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
