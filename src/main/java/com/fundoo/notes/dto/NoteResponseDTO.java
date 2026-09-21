package com.fundoo.notes.dto;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class NoteResponseDTO {

    private Long noteId;
    private String title;
    private String content;
    private String colour;
    private boolean isArchived;
    private boolean isTrashed;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
