package ru.practicum.shareit.exception;

public class StateConversionFailedException extends IllegalArgumentException {

    public StateConversionFailedException(String message) {
        super("Unknown state: " + message);
    }

}
