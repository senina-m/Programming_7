package ru.senina.itmo.lab7;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class CommandResponse {
    //todo: do i need an arguments field here? may be later
    private  Status code;
    private  String response;
    private  String commandName;

    public CommandResponse() {
    }

    public CommandResponse(Status code, String commandName, String response) {
        this.commandName = commandName;
        this.code = code;
        this.response = response;
    }
}