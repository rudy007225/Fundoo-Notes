package com.fundoo.notes.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class ResetPasswordDTO {

    @NotNull(message = "Password cannot be null")
    @NotBlank(message = "Password cannot be empty")
    @Size(min = 4, message = "Password must be at least 4 characters long")
    private String password;
}
