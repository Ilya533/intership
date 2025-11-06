package com.innowise.user_service;

import com.innowise.user_service.exception.UserNotFoundException;
import com.innowise.user_service.mapper.UserMapper;
import com.innowise.user_service.model.domain.UserEntity;
import com.innowise.user_service.model.dto.UserDTO;
import com.innowise.user_service.repository.UserRepository;
import com.innowise.user_service.service.UserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserMapper userMapper;

    @InjectMocks
    private UserService userService;

    @Test
    void getUserById_ShouldReturnUser_WhenUserExists() {
        Long userId = 1L;
        UserEntity userEntity = createUserEntity();
        UserDTO userDTO = createUserDTO();

        when(userRepository.findById(userId)).thenReturn(Optional.of(userEntity));
        when(userMapper.toDto(userEntity)).thenReturn(userDTO);

        UserDTO result = userService.getUserById(userId);

        assertNotNull(result);
        assertEquals(userId, result.getId());
        verify(userRepository).findById(userId);
    }

    @Test
    void getUserById_ShouldThrowException_WhenUserNotFound() {
        Long userId = 999L;

        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> userService.getUserById(userId));
    }

    @Test
    void createUser_ShouldSaveAndReturnUser() {
        UserDTO inputDTO = createUserDTO();
        inputDTO.setId(null);

        UserEntity userEntity = createUserEntity();
        UserEntity savedEntity = createUserEntity();
        savedEntity.setId(1L);
        UserDTO expectedDTO = createUserDTO();

        when(userMapper.toEntity(inputDTO)).thenReturn(userEntity);
        when(userRepository.save(userEntity)).thenReturn(savedEntity);
        when(userMapper.toDto(savedEntity)).thenReturn(expectedDTO);

        UserDTO result = userService.createUser(inputDTO);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        verify(userRepository).save(userEntity);
    }

    @Test
    void getAllUsers_ShouldReturnPageOfUsers() {
        Pageable pageable = PageRequest.of(0, 10);
        UserEntity userEntity = createUserEntity();
        Page<UserEntity> userPage = new PageImpl<>(List.of(userEntity), pageable, 1);
        UserDTO userDTO = createUserDTO();

        when(userRepository.findAll(pageable)).thenReturn(userPage);
        when(userMapper.toDto(userEntity)).thenReturn(userDTO);

        Page<UserDTO> result = userService.getAllUsers(pageable);

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        verify(userRepository).findAll(pageable);
    }

    private UserEntity createUserEntity() {
        UserEntity entity = new UserEntity();
        entity.setId(1L);
        entity.setName("John");
        entity.setSurname("Doe");
        entity.setEmail("john@test.com");
        entity.setBirthDate(LocalDate.of(1990, 1, 1));
        return entity;
    }

    private UserDTO createUserDTO() {
        UserDTO dto = new UserDTO();
        dto.setId(1L);
        dto.setName("John");
        dto.setSurname("Doe");
        dto.setEmail("john@test.com");
        dto.setBirthDate(LocalDate.of(1990, 1, 1));
        return dto;
    }
}