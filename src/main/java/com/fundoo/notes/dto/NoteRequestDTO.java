package com.fundoo.notes.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
public class NoteRequestDTO {

    private String title;
    private String content;
    private String colour;
}
