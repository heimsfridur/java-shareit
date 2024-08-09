package ru.practicum.shareit.exceptions;

public class UnavailableToAddCommentException extends RuntimeException {
    public UnavailableToAddCommentException(String message) {
        super(message);
    }

}
