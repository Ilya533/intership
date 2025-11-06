package com.innowise.user_service.service;

import com.innowise.user_service.exception.UserNotFoundException;
import com.innowise.user_service.mapper.UserMapper;
import com.innowise.user_service.model.domain.UserEntity;
import com.innowise.user_service.model.dto.UserDTO;
import com.innowise.user_service.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @Cacheable(value = "users", key = "#id")
    public UserDTO getUserById(Long id) {
        return userRepository.findById(id)
                .map(userMapper::toDto)
                .orElseThrow(() -> new UserNotFoundException(id));
    }

    @Cacheable(value = "usersByEmail", key = "#email")
    public UserDTO getUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .map(userMapper::toDto)
                .orElseThrow(() -> new UserNotFoundException(email));
    }

    public List<UserDTO> findBySurnameJPQL(String surname) {
        return userRepository.findBySurnameJPQL(surname)
                .stream()
                .map(userMapper::toDto)
                .collect(Collectors.toList());
    }

    public List<UserDTO> findByNameNative(String name) {
        return userRepository.findByNameNative(name)
                .stream()
                .map(userMapper::toDto)
                .collect(Collectors.toList());
    }

    @Cacheable(value = "usersWithCards", key = "#id")
    public UserDTO getUserWithCards(Long id) {
        return userRepository.findByIdWithCards(id)
                .map(userMapper::toDto)
                .orElseThrow(() -> new UserNotFoundException(id));
    }

    public Page<UserDTO> getAllUsers(Pageable pageable) {
        return userRepository.findAll(pageable).map(userMapper::toDto);
    }

    // Каскадное создание пользователя с картами
    public UserDTO createUser(UserDTO dto) {
        UserEntity entity = userMapper.toEntity(dto);

        // Каскадное сохранение карт
        if (entity.getCards() != null) {
            entity.getCards().forEach(card -> card.setUser(entity));
        }

        UserEntity saved = userRepository.save(entity);
        return userMapper.toDto(saved);
    }

    @Caching(
            put = @CachePut(value = "users", key = "#id"),
            evict = {
                    @CacheEvict(value = "usersByEmail", allEntries = true),
                    @CacheEvict(value = "usersWithCards", key = "#id")
            }
    )
    public UserDTO updateUser(Long id, UserDTO dto) {
        UserEntity entity = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException(id));

        entity.setName(dto.getName());
        entity.setSurname(dto.getSurname());
        entity.setEmail(dto.getEmail());
        entity.setBirthDate(dto.getBirthDate());

        if (dto.getCards() != null && !dto.getCards().isEmpty()) {
            entity.getCards().clear();
            dto.getCards().forEach(cardDTO -> {
            });
        }

        return userMapper.toDto(entity);
    }

    @Caching(evict = {
            @CacheEvict(value = "users", key = "#id"),
            @CacheEvict(value = "usersByEmail", allEntries = true),
            @CacheEvict(value = "usersWithCards", key = "#id")
    })
    public void deleteUser(Long id) {
        UserEntity user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException(id));
        userRepository.delete(user);
    }
}