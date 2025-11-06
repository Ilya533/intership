package com.innowise.user_service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.innowise.user_service.controller.UserController;
import com.innowise.user_service.model.dto.UserDTO;
import com.innowise.user_service.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserController.class)
@ActiveProfiles("test")
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private UserService userService;

    @Test
    void createUser_ShouldReturnCreated() throws Exception {
        UserDTO userDTO = createValidUserDTO();
        userDTO.setId(null);

        UserDTO responseDTO = createValidUserDTO();
        responseDTO.setId(1L);

        when(userService.createUser(any(UserDTO.class))).thenReturn(responseDTO);

        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("Test"))
                .andExpect(jsonPath("$.email").value("test@test.com"));
    }

    @Test
    void getAllUsers_ShouldReturnPage() throws Exception {
        UserDTO userDTO = createValidUserDTO();
        // Создаем Page с помощью конструктора PageImpl
        Page<UserDTO> page = new PageImpl<>(
                List.of(userDTO),
                PageRequest.of(0, 10),
                1
        );

        when(userService.getAllUsers(any())).thenReturn(page);

        mockMvc.perform(get("/api/users")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].name").value("Test"))
                .andExpect(jsonPath("$.content[0].email").value("test@test.com"))
                .andExpect(jsonPath("$.totalElements").value(1))
                .andExpect(jsonPath("$.totalPages").value(1));
    }

    @Test
    void getUserById_ShouldReturnUser() throws Exception {
        UserDTO userDTO = createValidUserDTO();

        when(userService.getUserById(1L)).thenReturn(userDTO);

        mockMvc.perform(get("/api/users/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("Test"));
    }

    private UserDTO createValidUserDTO() {
        UserDTO dto = new UserDTO();
        dto.setId(1L);
        dto.setName("Test");
        dto.setSurname("User");
        dto.setEmail("test@test.com");
        dto.setBirthDate(LocalDate.of(1990, 1, 1));
        return dto;
    }
}