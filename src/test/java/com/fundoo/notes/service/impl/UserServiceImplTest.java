package com.fundoo.notes.service.impl;

import com.fundoo.notes.dto.RegistrationDTO;
import com.fundoo.notes.dto.UserResponseDTO;
import com.fundoo.notes.entity.User;
import com.fundoo.notes.exception.UserAlreadyExistsException;
import com.fundoo.notes.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserServiceImpl userService;

    private RegistrationDTO registrationDTO;

    @BeforeEach
    void setUp() {
        registrationDTO = new RegistrationDTO();
        registrationDTO.setFirstName("John");
        registrationDTO.setLastName("Doe");
        registrationDTO.setEmail("john.doe@example.com");
        registrationDTO.setPassword("plainPassword123");
    }

    @Test
    void registerUser_ShouldThrowUserAlreadyExistsException_WhenEmailExists() {
        when(userRepository.existsByEmail(registrationDTO.getEmail())).thenReturn(true);

        UserAlreadyExistsException exception = assertThrows(
                UserAlreadyExistsException.class,
                () -> userService.registerUser(registrationDTO));

        assertTrue(exception.getMessage().contains("john.doe@example.com"));
        verify(userRepository, times(1)).existsByEmail(registrationDTO.getEmail());
        verifyNoInteractions(passwordEncoder);
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void registerUser_ShouldRegisterSuccessfully_WhenEmailDoesNotExist() {
        when(userRepository.existsByEmail(registrationDTO.getEmail())).thenReturn(false);
        when(passwordEncoder.encode("plainPassword123")).thenReturn("hashedPassword123");

        User savedUser = User.builder()
                .userId(1L)
                .firstName("John")
                .lastName("Doe")
                .email("john.doe@example.com")
                .password("hashedPassword123")
                .build();

        when(userRepository.save(any(User.class))).thenReturn(savedUser);

        UserResponseDTO response = userService.registerUser(registrationDTO);

        assertNotNull(response);
        assertEquals(1L, response.getUserId());
        assertEquals("John", response.getFirstName());
        assertEquals("Doe", response.getLastName());
        assertEquals("john.doe@example.com", response.getEmail());

        verify(userRepository, times(1)).existsByEmail(registrationDTO.getEmail());
        verify(passwordEncoder, times(1)).encode("plainPassword123");
        verify(userRepository, times(1)).save(any(User.class));
    }
}
