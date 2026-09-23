package com.fundoo.notes.service;

import com.fundoo.notes.dto.NoteRequestDTO;
import com.fundoo.notes.dto.NoteResponseDTO;

import java.util.List;

public interface NoteService {

    NoteResponseDTO createNote(NoteRequestDTO dto, Long userId);

    List<NoteResponseDTO> getAllNotes(Long userId);
 
    NoteResponseDTO getNoteById(Long noteId, Long userId);
}
