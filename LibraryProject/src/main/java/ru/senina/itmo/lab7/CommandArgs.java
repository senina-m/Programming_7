package ru.senina.itmo.lab7;

import lombok.Getter;
import lombok.Setter;
import ru.senina.itmo.lab7.labwork.LabWork;

@Getter @Setter
public class CommandArgs {
    private String commandName;
    private String[] args;
    private LabWork element;
    private String token;
    private String login;

    public CommandArgs(String commandName, String[] args) {
        this.commandName = commandName;
        this.args = args;
    }

    public CommandArgs() {
    }

}
