package com.fundoo.notes.controller;

import com.fundoo.notes.dto.ForgotPasswordDTO;
import com.fundoo.notes.dto.LoginDTO;
import com.fundoo.notes.dto.LoginResponseDTO;
import com.fundoo.notes.dto.RegistrationDTO;
import com.fundoo.notes.dto.ResetPasswordDTO;
import com.fundoo.notes.dto.UserResponseDTO;
import com.fundoo.notes.service.UserService;
import com.fundoo.notes.util.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping
    public ResponseEntity<ApiResponse<UserResponseDTO>> register( @Valid @RequestBody RegistrationDTO registrationDTO) {
        UserResponseDTO responseDTO = userService.registerUser(registrationDTO);
        ApiResponse<UserResponseDTO> apiResponse = ApiResponse.success("User registered successfully", responseDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(apiResponse);
    }
    
    
    @PostMapping("/login")
    public ResponseEntity<ApiResponse<LoginResponseDTO>> login(@Valid @RequestBody LoginDTO login){
    	LoginResponseDTO loginResponse = userService.loginUser(login);
    	ApiResponse<LoginResponseDTO> apiResponseLogin = ApiResponse.success("Authenticated", loginResponse);
    	return ResponseEntity.status(HttpStatus.OK).body(apiResponseLogin);
    }

    @PostMapping("/forgotpassword")
    public ResponseEntity<ApiResponse<Void>> forgotPassword(@Valid @RequestBody ForgotPasswordDTO forgotPasswordDTO) {
        userService.forgotPassword(forgotPasswordDTO);
        ApiResponse<Void> apiResponse = ApiResponse.success(
                "If an account exists with this email, a password reset link has been sent.", null);
        return ResponseEntity.status(HttpStatus.OK).body(apiResponse);
    }

    @PatchMapping
    public ResponseEntity<ApiResponse<Void>> resetPassword(@RequestParam("token") String token,
    														@Valid @RequestBody ResetPasswordDTO resetPasswordDTO) {
        userService.resetPassword(token, resetPasswordDTO);
        ApiResponse<Void> apiResponse = ApiResponse.success("Password reset successfully", null);
        return ResponseEntity.status(HttpStatus.OK).body(apiResponse);
    }
}
