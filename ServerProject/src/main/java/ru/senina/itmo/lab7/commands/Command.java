package ru.senina.itmo.lab7.commands;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import ru.senina.itmo.lab7.*;
import ru.senina.itmo.lab7.labwork.LabWork;

/**
 * Parent of all commands classes
 */

@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, include = JsonTypeInfo.As.PROPERTY, property = "@class")
public abstract class Command {
    private String[] args;
    private final String name;
    private final String description;
    private int number;

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    protected Command(String name, String description) {
        this.name = name;
        this.description = description;
    }

    public void setArgs(CommandArgs args) {
        this.args = args.getArgs();
        this.number = args.getNumber();
    }

    public String[] getArgs() {
        return args;
    }

    public CommandResponse run() {
        validateArguments();
        checkIfLogin();
        return doRun();
    }

    /**
     * Command execute method
     *
     * @return value to print in output like the result of command execute
     */
    protected abstract CommandResponse doRun();

    /**
     * Arguments validation method
     */
    public abstract void validateArguments();

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public void setElement(LabWork element) {
    }

    public void setCollectionKeeper(CollectionKeeper collectionKeeper) {
    }

    public void setParser(CollectionKeeperParser parser) {
    }

    protected void checkIfLogin() throws UnLoginUserException {
        //todo: check if token is correct
    }
}
