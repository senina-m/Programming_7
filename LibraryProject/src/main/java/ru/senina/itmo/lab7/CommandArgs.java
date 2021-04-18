package ru.senina.itmo.lab7;

import ru.senina.itmo.lab7.labwork.LabWork;

public class CommandArgs {
    private String commandName;
    private String[] args;
    private LabWork element;
    private String token;

    public CommandArgs(String commandName, String[] args) {
        this.commandName = commandName;
        this.args = args;
    }

    public CommandArgs() {
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getCommandName() {
        return commandName;
    }

    public void setCommandName(String commandName) {
        this.commandName = commandName;
    }

    public String[] getArgs() {
        return args;
    }

    public void setArgs(String[] args) {
        this.args = args;
    }

    public LabWork getElement() {
        return element;
    }

    public void setElement(LabWork element) {
        this.element = element;
    }
}
