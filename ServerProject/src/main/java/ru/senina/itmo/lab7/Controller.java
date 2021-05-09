package ru.senina.itmo.lab7;

import com.fasterxml.jackson.databind.ObjectMapper;

import ru.senina.itmo.lab7.parser.JsonParser;

import java.util.logging.Level;

public class Controller {
    private final Model model;
    private static final ObjectMapper objectMapper = new ObjectMapper();
    private static final JsonParser<CommandArgs> commandArgsParser = new JsonParser<>(objectMapper, CommandArgs.class);
    private static final JsonParser<CommandResponse> commandResponseParser = new JsonParser<>(objectMapper, CommandResponse.class);



    public Controller(Model model) {
        this.model = model;
    }

    public String processCommand(String strCommand) {
        //Todo: обработка команды
        if (strCommand != null) {
            CommandArgs commandArgs = commandArgsParser.fromStringToObject(strCommand);
            Logging.log(Level.INFO, "New command " + commandArgs.getCommandName() + " was read.");
            CommandResponse commandResponse = model.run(commandArgs);
            Logging.log(Level.INFO, "Command " + commandResponse.getCommandName() + " was executed.");
            return commandResponseParser.fromObjectToString(commandResponse);
        }else {
            return commandResponseParser.fromObjectToString(new CommandResponse(Status.NULLABLE_COMMAND, "none", "Command came to server invalid!"));
        }
    }
}
