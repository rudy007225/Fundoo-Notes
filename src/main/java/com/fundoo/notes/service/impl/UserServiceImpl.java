package com.fundoo.notes.service.impl;

import com.fundoo.notes.dto.RegistrationDTO;
import com.fundoo.notes.dto.UserResponseDTO;
import com.fundoo.notes.entity.User;
import com.fundoo.notes.exception.UserAlreadyExistsException;
import com.fundoo.notes.repository.UserRepository;
import com.fundoo.notes.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public UserResponseDTO registerUser(RegistrationDTO registrationDTO) {
    	
        if (userRepository.existsByEmail(registrationDTO.getEmail())) {
            throw new UserAlreadyExistsException("User already exists with email: " + registrationDTO.getEmail());
        }

        User user = mapRegisterDTOWithUser(registrationDTO);
        User savedUser = userRepository.save(user);
        
        return mapUserToUserResponseDTO(savedUser);
    }
    
    private User mapRegisterDTOWithUser(RegistrationDTO registrationDTO) {
    	
    	   User user = User.builder()
                   .firstName(registrationDTO.getFirstName())
                   .lastName(registrationDTO.getLastName())
                   .email(registrationDTO.getEmail())
                   .password(passwordEncoder
                           .encode(registrationDTO.getPassword()))
                   .build();
    	   return user;
    }
    
    private UserResponseDTO mapUserToUserResponseDTO(User user) {
    	 
    	  return new UserResponseDTO(
                user.getUserId(),
                user.getFirstName(),
                user.getLastName(),
                user.getEmail());
    }
}
