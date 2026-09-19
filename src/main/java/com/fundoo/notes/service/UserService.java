package com.fundoo.notes.service;

import com.fundoo.notes.dto.LoginDTO;
import com.fundoo.notes.dto.LoginResponseDTO;
import com.fundoo.notes.dto.RegistrationDTO;
import com.fundoo.notes.dto.UserResponseDTO;

public interface UserService {

    UserResponseDTO registerUser(RegistrationDTO registrationDTO);

    LoginResponseDTO loginUser(LoginDTO loginDTO);
}
