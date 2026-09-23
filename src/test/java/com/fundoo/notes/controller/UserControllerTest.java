package com.fundoo.notes.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fundoo.notes.dto.ForgotPasswordDTO;
import com.fundoo.notes.dto.RegistrationDTO;
import com.fundoo.notes.dto.ResetPasswordDTO;
import com.fundoo.notes.dto.UserResponseDTO;
import com.fundoo.notes.exception.UserAlreadyExistsException;
import com.fundoo.notes.exception.UserNotFoundException;
import com.fundoo.notes.service.UserService;
import com.fundoo.notes.config.JwtAuthFilter;
import io.jsonwebtoken.MalformedJwtException;
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
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
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

    @Test
    void forgotPassword_ShouldReturn200_WhenValidEmail() throws Exception {
        ForgotPasswordDTO dto = new ForgotPasswordDTO("john.doe@example.com");

        mockMvc.perform(post("/api/users/forgotpassword")
                .contentType(MediaType.APPLICATION_JSON)
                .content(Objects.requireNonNull(objectMapper.writeValueAsString(dto))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("If an account exists with this email, a password reset link has been sent."));

        verify(userService, times(1)).forgotPassword(any(ForgotPasswordDTO.class));
    }

    @Test
    void forgotPassword_ShouldReturn200_EvenWhenUserDoesNotExist_AntiEnumeration() throws Exception {
        ForgotPasswordDTO dto = new ForgotPasswordDTO("unknown@example.com");
        doNothing().when(userService).forgotPassword(any(ForgotPasswordDTO.class));

        mockMvc.perform(post("/api/users/forgotpassword")
                .contentType(MediaType.APPLICATION_JSON)
                .content(Objects.requireNonNull(objectMapper.writeValueAsString(dto))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("If an account exists with this email, a password reset link has been sent."));

        verify(userService, times(1)).forgotPassword(any(ForgotPasswordDTO.class));
    }

    @Test
    void forgotPassword_ShouldReturn400_WhenEmailIsInvalid() throws Exception {
        ForgotPasswordDTO dto = new ForgotPasswordDTO("not-an-email");

        mockMvc.perform(post("/api/users/forgotpassword")
                .contentType(MediaType.APPLICATION_JSON)
                .content(Objects.requireNonNull(objectMapper.writeValueAsString(dto))))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("Validation failed"))
                .andExpect(jsonPath("$.errors").isArray());

        verify(userService, never()).forgotPassword(any(ForgotPasswordDTO.class));
    }

    @Test
    void resetPassword_ShouldReturn200_WhenTokenAndPayloadValid() throws Exception {
        ResetPasswordDTO dto = new ResetPasswordDTO("newPassword123");

        mockMvc.perform(patch("/api/users")
                .param("token", "valid-reset-token")
                .contentType(MediaType.APPLICATION_JSON)
                .content(Objects.requireNonNull(objectMapper.writeValueAsString(dto))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Password reset successfully"));

        verify(userService, times(1)).resetPassword(eq("valid-reset-token"), any(ResetPasswordDTO.class));
    }

    @Test
    void resetPassword_ShouldReturn400_WhenPasswordTooShort() throws Exception {
        ResetPasswordDTO dto = new ResetPasswordDTO("12");

        mockMvc.perform(patch("/api/users")
                .param("token", "valid-reset-token")
                .contentType(MediaType.APPLICATION_JSON)
                .content(Objects.requireNonNull(objectMapper.writeValueAsString(dto))))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("Validation failed"));

        verify(userService, never()).resetPassword(anyString(), any(ResetPasswordDTO.class));
    }

    @Test
    void resetPassword_ShouldReturn400_WhenTokenIsMalformed() throws Exception {
        ResetPasswordDTO dto = new ResetPasswordDTO("newPassword123");
        doThrow(new MalformedJwtException("Malformed token"))
                .when(userService).resetPassword(eq("malformed-token"), any(ResetPasswordDTO.class));

        mockMvc.perform(patch("/api/users")
                .param("token", "malformed-token")
                .contentType(MediaType.APPLICATION_JSON)
                .content(Objects.requireNonNull(objectMapper.writeValueAsString(dto))))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("Invalid or expired token: Malformed token"));

        verify(userService, times(1)).resetPassword(eq("malformed-token"), any(ResetPasswordDTO.class));
    }

    @Test
    void resetPassword_ShouldReturn404_WhenUserNotFound() throws Exception {
        ResetPasswordDTO dto = new ResetPasswordDTO("newPassword123");
        doThrow(new UserNotFoundException("User not found with id: 999"))
                .when(userService).resetPassword(eq("valid-token-unknown-user"), any(ResetPasswordDTO.class));

        mockMvc.perform(patch("/api/users")
                .param("token", "valid-token-unknown-user")
                .contentType(MediaType.APPLICATION_JSON)
                .content(Objects.requireNonNull(objectMapper.writeValueAsString(dto))))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("User not found with id: 999"));

        verify(userService, times(1)).resetPassword(eq("valid-token-unknown-user"), any(ResetPasswordDTO.class));
    }
}
