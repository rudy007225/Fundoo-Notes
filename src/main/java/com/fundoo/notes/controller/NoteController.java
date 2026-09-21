package com.fundoo.notes.controller;

import com.fundoo.notes.dto.NoteRequestDTO;
import com.fundoo.notes.dto.NoteResponseDTO;
import com.fundoo.notes.security.CustomUserDetails;
import com.fundoo.notes.service.NoteService;
import com.fundoo.notes.util.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/notes")
@RequiredArgsConstructor
public class NoteController {

    private final NoteService noteService;

    @PostMapping
    public ResponseEntity<ApiResponse<NoteResponseDTO>> createNote(
            @RequestBody NoteRequestDTO dto,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        Long userId = userDetails.getUserId();
        NoteResponseDTO response = noteService.createNote(dto, userId);
        ApiResponse<NoteResponseDTO> apiResponse = ApiResponse.success("Note created successfully", response);
        return ResponseEntity.status(HttpStatus.CREATED).body(apiResponse);
    }
}
