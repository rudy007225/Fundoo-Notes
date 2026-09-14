package com.fundoo.notes.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.fundoo.notes.entity.Note;
import com.fundoo.notes.entity.User;

@Repository
public interface NoteRepository extends JpaRepository<Note, Long> {

    // UC6: Get all notes belonging to a specific user
    List<Note> findByUser(User user);

    // UC7: Get a specific note by its ID, scoped to a specific user (prevents unauthorized access)
    Optional<Note> findByNoteIdAndUser(Long noteId, User user);
}
