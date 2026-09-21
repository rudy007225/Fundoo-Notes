package com.fundoo.notes.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fundoo.notes.dto.RegistrationDTO;
import com.fundoo.notes.dto.UserResponseDTO;
import com.fundoo.notes.exception.UserAlreadyExistsException;
import com.fundoo.notes.service.UserService;
import com.fundoo.notes.config.JwtAuthFilter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Objects;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserController.class)
@AutoConfigureMockMvc(addFilters = false)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private UserService userService;

    @MockitoBean
    private JwtAuthFilter jwtAuthFilter;

    private RegistrationDTO validRegistrationDTO;

    @BeforeEach
    void setUp() {
        validRegistrationDTO = new RegistrationDTO();
        validRegistrationDTO.setFirstName("John");
        validRegistrationDTO.setLastName("Doe");
        validRegistrationDTO.setEmail("john.doe@example.com");
        validRegistrationDTO.setPassword("plainPassword123");
    }

    @Test
    void register_ShouldReturn201_WhenRegistrationIsSuccessful() throws Exception {
        UserResponseDTO userResponseDTO = new UserResponseDTO(
                1L,
                "John",
                "Doe",
                "john.doe@example.com");

        when(userService.registerUser(any(RegistrationDTO.class))).thenReturn(userResponseDTO);

        mockMvc.perform(post("/api/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(Objects.requireNonNull(objectMapper.writeValueAsString(validRegistrationDTO))))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("User registered successfully"))
                .andExpect(jsonPath("$.data.userId").value(1))
                .andExpect(jsonPath("$.data.firstName").value("John"))
                .andExpect(jsonPath("$.data.lastName").value("Doe"))
                .andExpect(jsonPath("$.data.email").value("john.doe@example.com"));

        verify(userService, times(1)).registerUser(any(RegistrationDTO.class));
    }

    @Test
    void register_ShouldReturn409_WhenEmailAlreadyExists() throws Exception {
        when(userService.registerUser(any(RegistrationDTO.class)))
                .thenThrow(new UserAlreadyExistsException("User already exists with email: john.doe@example.com"));

        mockMvc.perform(post("/api/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(Objects.requireNonNull(objectMapper.writeValueAsString(validRegistrationDTO))))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("User already exists with email: john.doe@example.com"));

        verify(userService, times(1)).registerUser(any(RegistrationDTO.class));
    }

    @Test
    void register_ShouldReturn400_WhenInputIsInvalid() throws Exception {
        RegistrationDTO invalidDTO = new RegistrationDTO();
        invalidDTO.setFirstName("");
        invalidDTO.setLastName("Doe");
        invalidDTO.setEmail("invalid-email");
        invalidDTO.setPassword("123");

        mockMvc.perform(post("/api/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(Objects.requireNonNull(objectMapper.writeValueAsString(invalidDTO))))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("Validation failed"))
                .andExpect(jsonPath("$.errors").isArray())
                .andExpect(jsonPath("$.errors").isNotEmpty());

        verify(userService, never()).registerUser(any(RegistrationDTO.class));
    }
}
