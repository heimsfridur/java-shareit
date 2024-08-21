package ru.practicum.shareit.exceptions;

public class EmailIsNotUniqueException extends RuntimeException {
    public EmailIsNotUniqueException(String message) {
        super(message);
    }
}
