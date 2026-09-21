package com.fundoo.notes.service.impl;

import com.fundoo.notes.dto.NoteRequestDTO;
import com.fundoo.notes.dto.NoteResponseDTO;
import com.fundoo.notes.entity.Note;
import com.fundoo.notes.entity.User;
import com.fundoo.notes.exception.EmptyNoteException;
import com.fundoo.notes.exception.UserNotFoundException;
import com.fundoo.notes.repository.NoteRepository;
import com.fundoo.notes.repository.UserRepository;
import com.fundoo.notes.service.NoteService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class NoteServiceImpl implements NoteService {

    private static final String DEFAULT_COLOUR = "white";
    private static final int TITLE_MAX_LENGTH = 50;

    private final NoteRepository noteRepository;
    private final UserRepository userRepository;

    @Override
    public NoteResponseDTO createNote(NoteRequestDTO dto, Long userId) {
        boolean isTitleBlank = dto.getTitle() == null || dto.getTitle().isBlank();
        boolean isContentBlank = dto.getContent() == null || dto.getContent().isBlank();

        // 1. Check if both title and content are blank -> throw EmptyNoteException
        if (isTitleBlank && isContentBlank) {
            throw new EmptyNoteException("Note title and content cannot both be empty");
        }

        // 2. If title is blank but content isn't -> derive the title from content's first line
        String title;
        if (isTitleBlank) {
            String firstLine = dto.getContent().trim()
                    .lines()
                    .filter(line -> !line.isBlank())
                    .findFirst()
                    .orElse("");
            if (firstLine.length() > TITLE_MAX_LENGTH) {
                title = firstLine.substring(0, TITLE_MAX_LENGTH) + "...";
            } else {
                title = firstLine;
            }
        } else {
            title = dto.getTitle().trim();
        }

        // 3. Fetch the actual User entity by userId
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found with id: " + userId));

        // 4. Build the Note entity via builder
        String colour = (dto.getColour() != null && !dto.getColour().isBlank())
                ? dto.getColour()
                : DEFAULT_COLOUR;

        Note note = Note.builder()
                .title(title)
                .content(dto.getContent())
                .colour(colour)
                .user(user)
                .build();

        // 5. Save via NoteRepository
        Note savedNote = noteRepository.save(note);

        // 6. Map the saved Note -> NoteResponseDTO
        return mapNoteToNoteResponseDTO(savedNote);
    }

    private NoteResponseDTO mapNoteToNoteResponseDTO(Note note) {
        return new NoteResponseDTO(
                note.getNoteId(),
                note.getTitle(),
                note.getContent(),
                note.getColour(),
                note.isArchived(),
                note.isTrashed(),
                note.getCreatedAt(),
                note.getUpdatedAt()
        );
    }
}
