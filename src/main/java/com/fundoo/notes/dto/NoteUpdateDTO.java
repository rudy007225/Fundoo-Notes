package com.fundoo.notes.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class NoteUpdateDTO {

    private Long noteId;
    private String title;
    private String content;
    private String colour;
}
