package com.fundoo.notes.service;

import com.fundoo.notes.dto.ForgotPasswordDTO;
import com.fundoo.notes.dto.LoginDTO;
import com.fundoo.notes.dto.LoginResponseDTO;
import com.fundoo.notes.dto.RegistrationDTO;
import com.fundoo.notes.dto.ResetPasswordDTO;
import com.fundoo.notes.dto.UserResponseDTO;

public interface UserService {

    UserResponseDTO registerUser(RegistrationDTO registrationDTO);

    LoginResponseDTO loginUser(LoginDTO loginDTO);

    void forgotPassword(ForgotPasswordDTO forgotPasswordDTO);

    void resetPassword(String token, ResetPasswordDTO resetPasswordDTO);
}
