package ru.senina.itmo.lab7.commands;

import com.fasterxml.jackson.databind.ObjectMapper;
import ru.senina.itmo.lab7.CommandResponse;
import ru.senina.itmo.lab7.SetOfCommands;
import ru.senina.itmo.lab7.Status;
import ru.senina.itmo.lab7.parser.JsonParser;
import ru.senina.itmo.lab7.parser.ParsingException;

import java.util.HashMap;
import java.util.Map;

@CommandAnnotation(name="request_map_of_commands")
public class RequestCommandsMapCommand extends CommandWithoutArgs{
    private final Map<String, Command> map;

    public RequestCommandsMapCommand(Map<String, Command> map) {
        super("request_map_of_commands", "Request list of commands");
        this.map = map;
    }

    @Override
    protected CommandResponse doRun() {
        Map<String, String[]> commandArgsList = createCommandsArgsMap(map);
        try {
            String strMapOfCommands = new JsonParser<>(new ObjectMapper(), SetOfCommands.class).fromObjectToString(new SetOfCommands(commandArgsList));
            return new CommandResponse(Status.OK, getName(), strMapOfCommands);
        } catch (ParsingException e){
            return new CommandResponse(Status.PARSER_EXCEPTION, getName(), "Problems with parsing set of commands");
        }
    }

    private static Map<String, String[]> createCommandsArgsMap(Map<String, Command> map) {
        Map<String, String[]> commandsArgsMap = new HashMap<>();
        map.values().forEach(command -> {
            if (command.getClass().isAnnotationPresent(CommandAnnotation.class)) {
                CommandAnnotation annotation = command.getClass().getAnnotation(CommandAnnotation.class);
                if (annotation.isVisibleInHelp()) {
                    if (annotation.element()) {
                        commandsArgsMap.put(annotation.name(), new String[]{"element"});
                    } else {
                        commandsArgsMap.put(annotation.name(), new String[]{});
                    }
                }
            }
        });
        return commandsArgsMap;
    }
}
