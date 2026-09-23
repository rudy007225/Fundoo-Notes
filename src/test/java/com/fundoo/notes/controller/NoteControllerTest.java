package com.fundoo.notes.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fundoo.notes.config.JwtAuthFilter;
import com.fundoo.notes.dto.NoteRequestDTO;
import com.fundoo.notes.dto.NoteResponseDTO;
import com.fundoo.notes.entity.User;
import com.fundoo.notes.exception.NoteNotFoundException;
import com.fundoo.notes.security.CustomUserDetails;
import com.fundoo.notes.service.NoteService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(NoteController.class)
@AutoConfigureMockMvc(addFilters = false)
class NoteControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private NoteService noteService;

    @MockitoBean
    private JwtAuthFilter jwtAuthFilter;

    private CustomUserDetails customUserDetails;

    @BeforeEach
    void setUp() {
        User user = User.builder()
                .userId(1L)
                .email("user@example.com")
                .password("encodedPass")
                .firstName("Test")
                .lastName("User")
                .build();
        customUserDetails = new CustomUserDetails(user);

        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(customUserDetails, null, customUserDetails.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void getAllNotes_WhenNotesPresent_ShouldReturn200AndNotesList() throws Exception {
        NoteResponseDTO note1 = new NoteResponseDTO(1L, "Title 1", "Content 1", "white", false, false, LocalDateTime.now(), LocalDateTime.now());
        NoteResponseDTO note2 = new NoteResponseDTO(2L, "Title 2", "Content 2", "#ffffff", false, false, LocalDateTime.now(), LocalDateTime.now());

        when(noteService.getAllNotes(1L)).thenReturn(List.of(note1, note2));

        mockMvc.perform(get("/api/notes"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Notes retrieved successfully"))
                .andExpect(jsonPath("$.data.length()").value(2))
                .andExpect(jsonPath("$.data[0].title").value("Title 1"))
                .andExpect(jsonPath("$.data[1].title").value("Title 2"));
    }

    @Test
    void getAllNotes_WhenNoNotes_ShouldReturn200AndEmptyMessage() throws Exception {
        when(noteService.getAllNotes(1L)).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/api/notes"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("No notes found"))
                .andExpect(jsonPath("$.data.length()").value(0));
    }

    @Test
    void createNote_WhenValid_ShouldReturn201() throws Exception {
        NoteRequestDTO request = new NoteRequestDTO();
        request.setTitle("Title");
        request.setContent("Content");
        request.setColour("white");
        NoteResponseDTO responseDTO = new NoteResponseDTO(1L, "Title", "Content", "white", false, false, LocalDateTime.now(), LocalDateTime.now());

        when(noteService.createNote(any(NoteRequestDTO.class), eq(1L))).thenReturn(responseDTO);

        mockMvc.perform(post("/api/notes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Note created successfully"))
                .andExpect(jsonPath("$.data.noteId").value(1L));
    }

    @Test
    void getNoteById_WhenNoteExists_ShouldReturn200AndNote() throws Exception {
        NoteResponseDTO responseDTO = new NoteResponseDTO(101L, "Sample Note", "Sample Content", "white", false, false, LocalDateTime.now(), LocalDateTime.now());
        when(noteService.getNoteById(101L, 1L)).thenReturn(responseDTO);

        mockMvc.perform(get("/api/notes/101"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Note retrieved successfully"))
                .andExpect(jsonPath("$.data.noteId").value(101L))
                .andExpect(jsonPath("$.data.title").value("Sample Note"));
    }

    @Test
    void getNoteById_WhenNoteNotFound_ShouldReturn404() throws Exception {
        when(noteService.getNoteById(999L, 1L))
                .thenThrow(new NoteNotFoundException("Note not found with id: 999"));

        mockMvc.perform(get("/api/notes/999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("Note not found with id: 999"));
    }
}
