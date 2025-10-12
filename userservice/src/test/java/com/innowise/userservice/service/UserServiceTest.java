package com.innowise.userservice.service;

import com.innowise.userservice.model.UserDto;
import com.innowise.userservice.exception.EmailAlreadyExistException;
import com.innowise.userservice.exception.UserNotFoundException;
import com.innowise.userservice.mapper.UserMapper;
import com.innowise.userservice.model.entity.User;
import com.innowise.userservice.repository.UserRepository;
import com.innowise.userservice.service.impl.UserServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

/**
 * @author Evgeniy Zaleshchenok
 */
@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    private Long userId;

    private User userEntity;

    private UserDto userDto;

    @Mock
    private UserRepository userRepository;

    @Mock
    private CacheManager cacheManager;

    @Mock
    private Cache userCache;

    @Mock
    private UserMapper userMapper;

    @InjectMocks
    private UserServiceImpl userService;

    @BeforeEach
    void setUp() {
        userId = 1L;

        userEntity = new User();
        userEntity.setId(userId);
        userEntity.setName("John");
        userEntity.setSurname("Doe");
        userEntity.setEmail("john.doe@example.com");
        userEntity.setBirthDate(LocalDate.parse("2005-04-03"));

        userDto = new UserDto();
        userDto.setName("John");
        userDto.setSurname("Doe");
        userDto.setEmail("john.doe@example.com");
        userDto.setBirthDate(LocalDate.parse("2005-04-03"));
    }

    @Test
    void createUser_withValidData_returnsUser() {
        Mockito.when(userRepository.save(userEntity)).thenReturn(userEntity);
        Mockito.when(userMapper.toResponseDto(userEntity)).thenReturn(userDto);
        Mockito.when(userMapper.toEntity(userDto)).thenReturn(userEntity);
        UserDto result = userService.createUser(userDto);

        assertNotNull(result);
        assertEquals(userDto.getId(), result.getId());
        assertEquals(userDto.getName(), result.getName());
        assertEquals(userDto.getSurname(), result.getSurname());
        assertEquals(userDto.getEmail(), result.getEmail());
        assertEquals(userDto.getBirthDate(), result.getBirthDate());

        Mockito.verify(userRepository, Mockito.times(1)).save(userEntity);
        Mockito.verify(userMapper, Mockito.times(1)).toResponseDto(userEntity);
    }

    @Test
    void createUser_withAlreadyExistEmail_returnsError() {
        Mockito.when(userRepository.findByEmail(userEntity.getEmail())).thenReturn(Optional.ofNullable(userEntity));
        assertThrows(EmailAlreadyExistException.class, () -> userService.createUser(userDto));
    }

    @Test
    void getUserById_withValidId_returnsUser() {
        Mockito.when(userRepository.findById(userId)).thenReturn(Optional.of(userEntity));
        Mockito.when(userMapper.toResponseDto(userEntity)).thenReturn(userDto);

        UserDto result = userService.getUserById(userId);

        assertNotNull(result);
        assertEquals(userDto.getId(), result.getId());
        assertEquals(userDto.getName(), result.getName());
        assertEquals(userDto.getSurname(), result.getSurname());
        assertEquals(userDto.getEmail(), result.getEmail());
        assertEquals(userDto.getBirthDate(), result.getBirthDate());
        Mockito.verify(userRepository, Mockito.times(1)).findById(userId);
        Mockito.verify(userMapper, Mockito.times(1)).toResponseDto(userEntity);
    }

    @Test
    void getUserById_withNonExistingUser_returnsError() {
        Mockito.when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> userService.getUserById(userId));
    }

    @Test
    void getAllUsers_whenUsersExist_shouldReturnUserDtoList() {
        User user2 = new User();
        user2.setId(2L);
        user2.setName("Jane");
        user2.setSurname("Doe");
        user2.setEmail("jane.doe@example.com");
        user2.setBirthDate(LocalDate.parse("2005-04-03"));

        UserDto userDto2 = new UserDto();
        userDto2.setName("Jane");
        userDto2.setSurname("Doe");
        userDto2.setEmail("jane.doe@example.com");
        userDto2.setBirthDate(LocalDate.parse("2005-04-03"));

        List<User> userList = List.of(user2, userEntity);
        Mockito.when(userRepository.findAll()).thenReturn(userList);
        Mockito.when(userMapper.toResponseDto(user2)).thenReturn(userDto2);
        Mockito.when(userMapper.toResponseDto(userEntity)).thenReturn(userDto);

        List<UserDto> result = userService.getAllUsers();

        assertNotNull(result);
        assertEquals(result.size(), 2);
        assertThat(result).containsExactlyInAnyOrder(userDto, userDto2);
        Mockito.verify(userRepository, Mockito.times(1)).findAll();
        Mockito.verify(userMapper, Mockito.times(1)).toResponseDto(user2);
    }

    @Test
    void getAllUsers_NoUsers_shouldReturnEmptyList() {
        Mockito.when(userRepository.findAll()).thenReturn(new ArrayList<>());

        List<UserDto> result = userService.getAllUsers();

        assertNotNull(result);
        assertTrue(result.isEmpty());
        Mockito.verify(userRepository, Mockito.times(1)).findAll();
        Mockito.verify(userMapper, Mockito.never()).toResponseDto(userEntity);
    }

    @Test
    void getUsersByIds_AllUsersInCache_returnUserList(){
        UserDto userDto2 = new UserDto();
        userDto2.setId(2L);
        userDto2.setName("Jane");
        Mockito.when(cacheManager.getCache("users")).thenReturn(userCache);
        Mockito.when(userCache.get(2L, UserDto.class)).thenReturn(userDto2);
        Mockito.when(userCache.get(1L, UserDto.class)).thenReturn(userDto);

        List<UserDto> result = userService.getUsersByIds(List.of(1L, 2L));

        assertNotNull(result);
        assertThat(result).containsExactlyInAnyOrder(userDto, userDto2);

        Mockito.verify(userRepository, Mockito.never()).findUsersByIds(Mockito.anyList());
        Mockito.verify(userMapper, Mockito.never()).toResponseDto(Mockito.any());
    }

    @Test
    void getUsersByIds_NoUsersInCache_returnUserList(){
        User user2 = new User();
        user2.setId(2L);
        user2.setName("Jane");
        UserDto userDto2 = new UserDto();
        userDto2.setId(2L);
        userDto2.setName("Jane");
        Mockito.when(cacheManager.getCache("users")).thenReturn(userCache);
        Mockito.when(userCache.get(2L, UserDto.class)).thenReturn(null);
        Mockito.when(userCache.get(1L, UserDto.class)).thenReturn(null);
        Mockito.when(userRepository.findUsersByIds(List.of(userId, 2L))).thenReturn(List.of(userEntity, user2));
        Mockito.when(userMapper.toResponseDto(userEntity)).thenReturn(userDto);
        Mockito.when(userMapper.toResponseDto(user2)).thenReturn(userDto2);

        List<UserDto> result = userService.getUsersByIds(List.of(userId, 2L));

        assertNotNull(result);
        assertThat(result).containsExactlyInAnyOrder(userDto, userDto2);
        Mockito.verify(userRepository, Mockito.times(1)).findUsersByIds(Mockito.anyList());
        Mockito.verify(userMapper, Mockito.times(2)).toResponseDto(Mockito.any());
    }

    @Test
    void getUsersByIds_withPartialCacheHit_shouldFetchMissingFromDbAndCombine() {
        Long missingUserId = 2L;
        User userFromDb = new User();
        userFromDb.setId(missingUserId);
        userFromDb.setName("Jane");

        UserDto dtoFromDb = new UserDto();
        dtoFromDb.setId(missingUserId);
        dtoFromDb.setName("Jane");

        Mockito.when(cacheManager.getCache("users")).thenReturn(userCache);
        Mockito.when(userCache.get(userId, UserDto.class)).thenReturn(userDto);
        Mockito.when(userCache.get(missingUserId, UserDto.class)).thenReturn(null);

        List<Long> idsToFetchFromDb = List.of(missingUserId);
        Mockito.when(userRepository.findUsersByIds(idsToFetchFromDb)).thenReturn(List.of(userFromDb));
        Mockito.when(userMapper.toResponseDto(userFromDb)).thenReturn(dtoFromDb);

        List<Long> requestedIds = List.of(userId, missingUserId);
        List<UserDto> result = userService.getUsersByIds(requestedIds);

        assertNotNull(result);
        assertThat(result).hasSize(2);
        assertThat(result).containsExactlyInAnyOrder(userDto, dtoFromDb);

        Mockito.verify(userRepository, Mockito.times(1)).findUsersByIds(idsToFetchFromDb);
        Mockito.verify(userMapper, Mockito.times(1)).toResponseDto(userFromDb);
        Mockito.verify(userMapper, Mockito.never()).toResponseDto(userEntity);
        Mockito.verify(userCache, Mockito.times(1)).put(missingUserId, dtoFromDb);
        Mockito.verify(userCache, Mockito.never()).put(userId, userDto);
    }

    @Test
    void getUserByEmail_whenUserExists_returnsUserDto() {
        String email = userEntity.getEmail();
        Mockito.when(userRepository.findByEmail(email)).thenReturn(Optional.of(userEntity));
        Mockito.when(userMapper.toResponseDto(userEntity)).thenReturn(userDto);

        UserDto result = userService.getUserByEmail(email);

        assertNotNull(result);
        assertEquals(userDto.getName(), result.getName());
        Mockito.verify(userRepository).findByEmail(email);
        Mockito.verify(userMapper).toResponseDto(userEntity);
    }

    @Test
    void getUserByEmail_whenUserNotFound_throwsUserNotFoundException() {
        String nonExistentEmail = "ghost@example.com";
        Mockito.when(userRepository.findByEmail(nonExistentEmail)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> userService.getUserByEmail(nonExistentEmail));
        Mockito.verify(userMapper, Mockito.never()).toResponseDto(Mockito.any());
    }

    @Test
    void updateUser_whenUserExists_returnsUpdatedUserDto() {
        UserDto updateRequest = new UserDto();
        updateRequest.setName("Updated John");
        updateRequest.setEmail("updated.john@example.com");

        UserDto expectedDto = new UserDto();
        expectedDto.setName("Updated John");
        expectedDto.setEmail("updated.john@example.com");
        expectedDto.setSurname(userEntity.getSurname());

        Mockito.when(userRepository.findById(userId)).thenReturn(Optional.of(userEntity));
        Mockito.when(userRepository.save(Mockito.any(User.class))).thenReturn(userEntity);
        Mockito.when(userMapper.toResponseDto(userEntity)).thenReturn(expectedDto);

        UserDto result = userService.updateUser(userId, updateRequest);

        assertNotNull(result);
        assertEquals("Updated John", result.getName());
        assertEquals("updated.john@example.com", result.getEmail());
        assertEquals("Doe", result.getSurname());

        Mockito.verify(userRepository).findById(userId);
        Mockito.verify(userRepository).save(userEntity);
    }

    @Test
    void updateUser_whenUserNotFound_throwsUserNotFoundException() {
        Long nonExistentId = 999L;
        UserDto updateRequest = new UserDto();
        updateRequest.setName("Ghost");

        Mockito.when(userRepository.findById(nonExistentId)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> userService.updateUser(nonExistentId, updateRequest));
        Mockito.verify(userRepository, Mockito.never()).save(Mockito.any());
    }

    @Test
    void deleteUser_whenUserExists_deletesUser() {
        Mockito.when(userRepository.existsById(userId)).thenReturn(true);
        Mockito.doNothing().when(userRepository).deleteById(userId);

        assertDoesNotThrow(() -> userService.deleteUser(userId));

        Mockito.verify(userRepository).existsById(userId);
        Mockito.verify(userRepository).deleteById(userId);
    }

    @Test
    void deleteUser_whenUserNotFound_throwsUserNotFoundException() {
        Long nonExistentId = 999L;
        Mockito.when(userRepository.existsById(nonExistentId)).thenReturn(false);

        assertThrows(UserNotFoundException.class, () -> userService.deleteUser(nonExistentId));

        Mockito.verify(userRepository).existsById(nonExistentId);
        Mockito.verify(userRepository, Mockito.never()).deleteById(Mockito.anyLong());
    }
}