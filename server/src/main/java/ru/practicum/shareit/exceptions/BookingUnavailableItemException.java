package ru.practicum.shareit.exceptions;

public class BookingUnavailableItemException extends RuntimeException {
    public BookingUnavailableItemException(String message) {
        super(message);
    }

}
