package ru.senina.itmo.lab7;

/**
 * Exception to show that some arguments weren't valid
 */
public class InvalidArgumentsException extends RuntimeException {
    public InvalidArgumentsException(String message) {
        super(message);
    }
}