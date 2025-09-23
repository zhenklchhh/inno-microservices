package com.innowise.innomicroservices.service;

import com.innowise.innomicroservices.dto.CreateUserRequestDto;
import com.innowise.innomicroservices.dto.UpdateUserRequestDto;
import com.innowise.innomicroservices.dto.UserResponseDto;
import com.innowise.innomicroservices.exception.UserNotFoundException;
import com.innowise.innomicroservices.mapper.UserMapper;
import com.innowise.innomicroservices.model.User;
import com.innowise.innomicroservices.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
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

    @Autowired
    public UserService(UserRepository userRepository, UserMapper userMapper) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
    }

    @Transactional
    public UserResponseDto createUser(CreateUserRequestDto createUserRequestDto) {
        User user = userMapper.toEntity(createUserRequestDto);
        User savedUser = userRepository.save(user);
        return userMapper.toResponseDto(savedUser);
    }

    @Transactional(readOnly = true)
    public UserResponseDto getUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("User not found with id: " + id));
        return userMapper.toResponseDto(user);
    }

    @Transactional(readOnly = true)
    public List<UserResponseDto> getUsersByIds(List<Long> ids) {
        return userRepository.findUsersByIds(ids).stream()
                .map(userMapper::toResponseDto)
                .toList();
    }

    @Transactional(readOnly = true)
    public UserResponseDto getUserByEmail(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("User not found with email: " + email));
        return userMapper.toResponseDto(user);
    }

    @Transactional
    public UserResponseDto updateUser(Long id, UpdateUserRequestDto updateUserRequestDto) {
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
    public void deleteUser(Long id) {
        if (!userRepository.existsById(id)) {
            throw new RuntimeException("User not found with id: " + id);
        }
        userRepository.deleteById(id);
    }
}
