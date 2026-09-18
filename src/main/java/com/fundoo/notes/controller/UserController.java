package com.fundoo.notes.controller;

import com.fundoo.notes.dto.RegistrationDTO;
import com.fundoo.notes.dto.UserResponseDTO;
import com.fundoo.notes.service.UserService;
import com.fundoo.notes.util.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<UserResponseDTO>> register( @Valid @RequestBody RegistrationDTO registrationDTO) {
        UserResponseDTO responseDTO = userService.registerUser(registrationDTO);
        ApiResponse<UserResponseDTO> apiResponse = ApiResponse.success("User registered successfully", responseDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(apiResponse);
    }
}
