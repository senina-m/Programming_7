package ru.senina.itmo.lab7;

import com.fasterxml.jackson.databind.ObjectMapper;
import ru.senina.itmo.lab7.commands.*;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;

public class Model {
    private static final ObjectMapper objectMapper = new ObjectMapper();
    private static final CollectionParser COLLECTION_PARSER = new CollectionParser(objectMapper, LabWorkList.class);
    private static final Map<String, Command> commandMap = createCommandMap();
    private static final CollectionKeeper collectionKeeper = new CollectionKeeper();

    public CommandResponse run(CommandArgs commandArgs) {
            Logging.log(Level.INFO, "New command " + commandArgs.getCommandName() + " was read.");
            Command command = commandMap.get(commandArgs.getCommandName());
            command.setArgs(commandArgs);
            if (command.getClass().isAnnotationPresent(CommandAnnotation.class)) {
                CommandAnnotation annotation = command.getClass().getAnnotation(CommandAnnotation.class);
                if (annotation.collectionKeeper()) {
                    command.setCollectionKeeper(collectionKeeper);
                }
                if (annotation.parser()) {
                    command.setParser(COLLECTION_PARSER);
                }
            }
            return command.run();
    }

    private static Map<String, Command> createCommandMap() {
        //TODO: new authorization command
        //TODO: decide what to do with filename variable
        String filename = "my_file.json";
        Map<String, Command> commandMap = new HashMap<>();
        commandMap.put("authorize", new AuthorizeCommand());
        commandMap.put("help", new HelpCommand(commandMap));
        commandMap.put("info", new InfoCommand());
        commandMap.put("show", new ShowCommand());
        commandMap.put("add", new AddCommand());
        commandMap.put("update", new UpdateCommand());
        commandMap.put("remove_by_id", new RemoveByIDCommand());
        commandMap.put("clear", new ClearCommand());
        commandMap.put("save", new SaveCommand(filename));
        commandMap.put("remove_at", new RemoveAtCommand());
        commandMap.put("remove_greater", new RemoveGreaterCommand());
        commandMap.put("sort", new SortCommand());
        commandMap.put("min_by_difficulty", new MinByDifficultyCommand());
        commandMap.put("filter_by_description", new FilterByDescriptionCommand());
        commandMap.put("print_descending", new PrintDescendingCommand());
        commandMap.put("execute_script", new ExecuteScriptCommand());
        commandMap.put("exit", new ExitCommand());
        commandMap.put("request_map_of_commands", new RequestCommandsMapCommand(commandMap));
        commandMap.put("register", new RegisterCommand());
        return commandMap;
    }

}

