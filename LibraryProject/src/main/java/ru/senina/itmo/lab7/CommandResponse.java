package ru.senina.itmo.lab7;

public class CommandResponse {
    //todo: do i need an arguments field here? may be later
    private  int code;
    private  String response;
    private  String commandName;
    private  int exceptionCode = 0;


    public CommandResponse() {
    }

    public CommandResponse(int code, String commandName, String response) {
        this.commandName = commandName;
        this.code = code;
        this.response = response;
    }

    public void setCode(int code) {
        this.code = code;
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

    public int getCode() {
        return code;
    }
}