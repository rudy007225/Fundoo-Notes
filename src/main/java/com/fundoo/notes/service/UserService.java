package com.fundoo.notes.service;

import com.fundoo.notes.dto.RegistrationDTO;
import com.fundoo.notes.dto.UserResponseDTO;

public interface UserService {

    UserResponseDTO registerUser(RegistrationDTO registrationDTO);
}
