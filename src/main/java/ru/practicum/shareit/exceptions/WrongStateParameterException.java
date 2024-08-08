package ru.practicum.shareit.exceptions;

public class WrongStateParameterException extends RuntimeException {
    public WrongStateParameterException(String message) {
        super(message);
    }

}
