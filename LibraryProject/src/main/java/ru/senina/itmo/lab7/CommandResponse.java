package ru.senina.itmo.lab7;

public class CommandResponse {
    private  int commandNumber;
    private  String response;
    private  String commandName;
    private  int exceptionCode = 0;


    public CommandResponse() {
    }

    public CommandResponse(int commandNumber, String commandName, String response) {
        this.commandName = commandName;
        this.commandNumber = commandNumber;
        this.response = response;
    }

    public void setCommandNumber(int commandNumber) {
        this.commandNumber = commandNumber;
    }

    public void setResponse(String response) {
        this.response = response;
    }

    public String getCommandName() {
        return commandName;
    }

    public void setCommandName(String commandName) {
        this.commandName = commandName;
    }

    public int getExceptionCode() {
        return exceptionCode;
    }

    public void setExceptionCode(int exceptionCode) {
        this.exceptionCode = exceptionCode;
    }

    public String getResponse() {
        return response;
    }

    public int getCommandNumber() {
        return commandNumber;
    }
}