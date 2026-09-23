package com.fundoo.notes.service.impl;

import com.fundoo.notes.dto.ForgotPasswordDTO;
import com.fundoo.notes.dto.LoginDTO;
import com.fundoo.notes.dto.LoginResponseDTO;
import com.fundoo.notes.dto.RegistrationDTO;
import com.fundoo.notes.dto.ResetPasswordDTO;
import com.fundoo.notes.dto.UserResponseDTO;
import com.fundoo.notes.entity.User;
import com.fundoo.notes.exception.InvalidCredentialsException;
import com.fundoo.notes.exception.UserAlreadyExistsException;
import com.fundoo.notes.exception.UserNotFoundException;
import com.fundoo.notes.repository.UserRepository;
import com.fundoo.notes.service.EmailService;
import com.fundoo.notes.service.UserService;
import com.fundoo.notes.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private static final String INVALID_CREDENTIALS_MESSAGE = "Invalid email or password";

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final EmailService emailService;

    @Value("${app.frontend.reset-password-url:http://localhost:8081/reset-password}")
    private String resetPasswordUrl = "http://localhost:8081/reset-password";

    @Override
    public UserResponseDTO registerUser(RegistrationDTO registrationDTO) {
        if (userRepository.existsByEmail(registrationDTO.getEmail())) {
            throw new UserAlreadyExistsException("User already exists with email: " + registrationDTO.getEmail());
        }

        User user = mapRegisterDTOWithUser(registrationDTO);
        User savedUser = userRepository.save(user);

        return mapUserToUserResponseDTO(savedUser);
    }

    @Override
    public LoginResponseDTO loginUser(LoginDTO loginDTO) {
        User user = userRepository.findByEmail(loginDTO.getEmail())
                .orElseThrow(() -> new InvalidCredentialsException(INVALID_CREDENTIALS_MESSAGE));

        if (!passwordEncoder.matches(loginDTO.getPassword(), user.getPassword())) {
            throw new InvalidCredentialsException(INVALID_CREDENTIALS_MESSAGE);
        }

        String token = jwtUtil.generateToken(user.getEmail());

        return new LoginResponseDTO(
                token,
                user.getUserId(),
                user.getFirstName(),
                user.getLastName(),
                user.getEmail());
    }

    @Override
    public void forgotPassword(ForgotPasswordDTO forgotPasswordDTO) {
        userRepository.findByEmail(forgotPasswordDTO.getEmail()).ifPresent(user -> {
            String resetToken = jwtUtil.generateResetToken(user.getUserId(), user.getEmail());
            String resetLink = resetPasswordUrl + "?token=" + resetToken;
            emailService.sendPasswordResetEmail(user.getEmail(), resetLink);
        });
    }

    @Override
    public void resetPassword(String token, ResetPasswordDTO resetPasswordDTO) {
        Long userId = jwtUtil.extractUserIdFromToken(token);
        if (userId == null) {
            throw new UserNotFoundException("Invalid token: user ID not found");
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found with id: " + userId));

        user.setPassword(passwordEncoder.encode(resetPasswordDTO.getPassword()));
        userRepository.save(user);
    }

    private User mapRegisterDTOWithUser(RegistrationDTO registrationDTO) {
        return User.builder()
                .firstName(registrationDTO.getFirstName())
                .lastName(registrationDTO.getLastName())
                .email(registrationDTO.getEmail())
                .password(passwordEncoder.encode(registrationDTO.getPassword()))
                .build();
    }

    private UserResponseDTO mapUserToUserResponseDTO(User user) {
        return new UserResponseDTO(
                user.getUserId(),
                user.getFirstName(),
                user.getLastName(),
                user.getEmail());
    }
}
