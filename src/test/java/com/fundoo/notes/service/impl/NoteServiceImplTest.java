package com.fundoo.notes.service.impl;

import com.fundoo.notes.dto.NoteRequestDTO;
import com.fundoo.notes.dto.NoteResponseDTO;
import com.fundoo.notes.entity.Note;
import com.fundoo.notes.entity.User;
import com.fundoo.notes.exception.EmptyNoteException;
import com.fundoo.notes.exception.NoteNotFoundException;
import com.fundoo.notes.exception.UserNotFoundException;
import com.fundoo.notes.repository.NoteRepository;
import com.fundoo.notes.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class NoteServiceImplTest {

    @Mock
    private NoteRepository noteRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private NoteServiceImpl noteService;

    private User user;
    private Note note1;
    private Note note2;

    @BeforeEach
    void setUp() {
        user = User.builder()
                .userId(1L)
                .email("test@example.com")
                .firstName("Test")
                .lastName("User")
                .build();

        note1 = Note.builder()
                .noteId(101L)
                .title("Note 1")
                .content("Content 1")
                .colour("white")
                .isArchived(false)
                .isTrashed(false)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .user(user)
                .build();

        note2 = Note.builder()
                .noteId(102L)
                .title("Note 2")
                .content("Content 2")
                .colour("#fffeee")
                .isArchived(false)
                .isTrashed(false)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .user(user)
                .build();
    }

    @Test
    void getAllNotes_WhenNotesExist_ShouldReturnListOfNoteResponseDTO() {
        when(noteRepository.findByUserUserId(1L)).thenReturn(List.of(note1, note2));

        List<NoteResponseDTO> result = noteService.getAllNotes(1L);

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("Note 1", result.get(0).getTitle());
        assertEquals("Content 1", result.get(0).getContent());
        assertEquals(101L, result.get(0).getNoteId());
        assertEquals("Note 2", result.get(1).getTitle());
        verify(noteRepository, times(1)).findByUserUserId(1L);
    }

    @Test
    void getAllNotes_WhenNoNotesExist_ShouldReturnEmptyListWithoutException() {
        when(noteRepository.findByUserUserId(1L)).thenReturn(Collections.emptyList());

        List<NoteResponseDTO> result = noteService.getAllNotes(1L);

        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(noteRepository, times(1)).findByUserUserId(1L);
    }

    @Test
    void createNote_WhenValidRequest_ShouldSaveAndReturnResponseDTO() {
        NoteRequestDTO request = new NoteRequestDTO();
        request.setTitle("Test Title");
        request.setContent("Test Content");
        request.setColour("yellow");
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(noteRepository.save(any(Note.class))).thenReturn(note1);

        NoteResponseDTO response = noteService.createNote(request, 1L);

        assertNotNull(response);
        assertEquals(101L, response.getNoteId());
        verify(noteRepository, times(1)).save(any(Note.class));
    }

    @Test
    void createNote_WhenBothTitleAndContentEmpty_ShouldThrowEmptyNoteException() {
        NoteRequestDTO request = new NoteRequestDTO();
        request.setTitle("   ");
        request.setContent("");
        request.setColour("yellow");

        assertThrows(EmptyNoteException.class, () -> noteService.createNote(request, 1L));
        verify(noteRepository, never()).save(any());
    }

    @Test
    void createNote_WhenUserNotFound_ShouldThrowUserNotFoundException() {
        NoteRequestDTO request = new NoteRequestDTO();
        request.setTitle("Title");
        request.setContent("Content");
        request.setColour("white");
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> noteService.createNote(request, 1L));
        verify(noteRepository, never()).save(any());
    }

    @Test
    void getNoteById_WhenNoteExists_ShouldReturnNoteResponseDTO() {
        when(noteRepository.findByNoteIdAndUserUserId(101L, 1L)).thenReturn(Optional.of(note1));

        NoteResponseDTO response = noteService.getNoteById(101L, 1L);

        assertNotNull(response);
        assertEquals(101L, response.getNoteId());
        assertEquals("Note 1", response.getTitle());
        assertEquals("Content 1", response.getContent());
        verify(noteRepository, times(1)).findByNoteIdAndUserUserId(101L, 1L);
    }

    @Test
    void getNoteById_WhenNoteDoesNotExist_ShouldThrowNoteNotFoundException() {
        when(noteRepository.findByNoteIdAndUserUserId(999L, 1L)).thenReturn(Optional.empty());

        assertThrows(NoteNotFoundException.class, () -> noteService.getNoteById(999L, 1L));
        verify(noteRepository, times(1)).findByNoteIdAndUserUserId(999L, 1L);
    }
}
