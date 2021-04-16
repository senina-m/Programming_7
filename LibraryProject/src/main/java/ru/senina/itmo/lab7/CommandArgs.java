package ru.senina.itmo.lab7;

import ru.senina.itmo.lab7.labwork.LabWork;

public class CommandArgs {
    private String commandName;
    private int number;
    private String[] args;
    private LabWork element;

    public CommandArgs(String commandName, String[] args) {
        this.commandName = commandName;
        this.args = args;
    }

    public CommandArgs() {
    }

    public String getCommandName() {
        return commandName;
    }

    public void setCommandName(String commandName) {
        this.commandName = commandName;
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
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
