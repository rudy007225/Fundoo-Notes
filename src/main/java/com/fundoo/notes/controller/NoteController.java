package com.fundoo.notes.controller;

import com.fundoo.notes.dto.NoteRequestDTO;
import com.fundoo.notes.dto.NoteResponseDTO;
import com.fundoo.notes.dto.NoteUpdateDTO;
import com.fundoo.notes.security.CustomUserDetails;
import com.fundoo.notes.service.NoteService;
import com.fundoo.notes.util.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/notes")
@RequiredArgsConstructor
public class NoteController {

    private final NoteService noteService;

    @PostMapping
    public ResponseEntity<ApiResponse<NoteResponseDTO>> createNote(@RequestBody NoteRequestDTO dto, @AuthenticationPrincipal CustomUserDetails userDetails) {
        Long userId = userDetails.getUserId();
        NoteResponseDTO response = noteService.createNote(dto, userId);
        ApiResponse<NoteResponseDTO> apiResponse = ApiResponse.success("Note created successfully", response);
        return ResponseEntity.status(HttpStatus.CREATED).body(apiResponse);
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<NoteResponseDTO>>> getAllNotes(
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        Long userId = userDetails.getUserId();
        List<NoteResponseDTO> notes = noteService.getAllNotes(userId);
        String message = notes.isEmpty() ? "No notes found" : "Notes retrieved successfully";
        return ResponseEntity.ok(ApiResponse.success(message, notes));
    }

    @GetMapping("/{noteId}")
    public ResponseEntity<ApiResponse<NoteResponseDTO>> getNoteById(
            @PathVariable Long noteId,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        Long userId = userDetails.getUserId();
        NoteResponseDTO response = noteService.getNoteById(noteId, userId);
        return ResponseEntity.ok(ApiResponse.success("Note retrieved successfully", response));
    }
    
    @PatchMapping
    public ResponseEntity<ApiResponse<NoteResponseDTO>> editNote( @RequestBody NoteUpdateDTO note,
    		                              @AuthenticationPrincipal CustomUserDetails userDetails){
    	Long userId = userDetails.getUserId();
    	NoteResponseDTO res = noteService.editNote(note, userId);
    	return ResponseEntity.ok(ApiResponse.success("Note successfully updated", res));
    }
}
