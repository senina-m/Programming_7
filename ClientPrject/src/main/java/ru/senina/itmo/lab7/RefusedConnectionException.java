package ru.senina.itmo.lab7;

public class RefusedConnectionException extends RuntimeException{
    RefusedConnectionException(String message){
        super(message);
    }
}
