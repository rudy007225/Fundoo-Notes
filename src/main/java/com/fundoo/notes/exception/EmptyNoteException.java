package com.fundoo.notes.exception;

public class EmptyNoteException extends RuntimeException {

    public EmptyNoteException(String message) {
        super(message);
    }
}
