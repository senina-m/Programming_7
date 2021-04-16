package ru.senina.itmo.lab7;


import com.fasterxml.jackson.databind.ObjectMapper;
import ru.senina.itmo.lab7.commands.*;
import ru.senina.itmo.lab7.parser.JsonParser;
import ru.senina.itmo.lab7.parser.Parser;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.concurrent.TimeoutException;
import java.util.logging.Level;

/**
 * Class to deal with input and output and keep CollectionKeeper class instance.
 */
public class ServerKeeper {
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final ServerNetConnector netConnector = new ServerNetConnector();
    private CollectionKeeper collectionKeeper = new CollectionKeeper(new LinkedList<>());
    private final CollectionKeeperParser collectionKeeperParser = new CollectionKeeperParser(objectMapper, CollectionKeeper.class);
    private final Parser<CommandResponse> responseParser = new JsonParser<>(objectMapper, CommandResponse.class);
    private final Parser<CommandArgs> commandJsonParser = new JsonParser<>(objectMapper, CommandArgs.class);

    public ServerKeeper() {
    }

    /**
     * Method to start a new collection and System.in reader
     */
    public void start(int serverPort) {
        Logging.log(Level.INFO, "Keeper was started.");
        while (true) {
            newClient(serverPort);
            collectionKeeper = new CollectionKeeper(new LinkedList<>());
        }
    }

    private Map<String, Command> createCommandMap() {
        //TODO: decide what to do with filename variable
        String filename = "my_file.json";
        Map<String, Command> commandMap = new HashMap<>();
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
        return commandMap;
    }

    private Map<String, String[]> createCommandsArgsMap(Map<String, Command> map) {
        Map<String, String[]> commandsArgsMap = new HashMap<>();
        for (Command command : map.values()) {
            if (command.getClass().isAnnotationPresent(CommandAnnotation.class)) {
                CommandAnnotation annotation = command.getClass().getAnnotation(CommandAnnotation.class);
                if (annotation.element()) {
                    commandsArgsMap.put(annotation.name(), new String[]{"element"});
                } else {
                    commandsArgsMap.put(annotation.name(), new String[]{""});
                }
            }
        }
        return commandsArgsMap;
    }

    private void newClient(int serverPort){

        //TODO: try_with_resources
        try {
            Map<String, Command> commandMap = createCommandMap();
            SetOfCommands setOfCommands = new SetOfCommands(createCommandsArgsMap(commandMap));
            commandMap.put("create_collection", new CreateCollectionCommand());

            netConnector.startConnection(serverPort);
            String commandsSetString = new JsonParser<>(objectMapper, SetOfCommands.class).fromObjectToString(setOfCommands);
            netConnector.sendResponse(commandsSetString);
            Logging.log(Level.INFO, "Initial commands set was sent to client.");
            boolean working = true;
            while (working) {
                try {
                    String strCommand = netConnector.nextCommand(2000);
                    if (strCommand != null) {
                        CommandArgs commandArgs = commandJsonParser.fromStringToObject(strCommand);
                        Logging.log(Level.INFO, "New command " + commandArgs.getCommandName() + " (" + commandArgs.getNumber() + ") was read.");
                        Command command = commandMap.get(commandArgs.getCommandName());
                        command.setArgs(commandArgs);
                        if (command.getClass().isAnnotationPresent(CommandAnnotation.class)) {
                            CommandAnnotation annotation = command.getClass().getAnnotation(CommandAnnotation.class);
                            if (annotation.collectionKeeper()) {
                                command.setCollectionKeeper(collectionKeeper);
                            }
                            if (annotation.parser()) {
                                command.setParser(collectionKeeperParser);
                            }
                            if (annotation.element()) {
                                //TODO: Check that element isn't null (have i do it here?)
                                command.setElement(commandArgs.getElement());
                            }
                        }
                        String commandResult = command.run();
                        Logging.log(Level.INFO, "Command " + command.getName() + " (" + command.getNumber() + ") was executed without errors.");
                        //stop connection and wait for new client if this is answering too long or has disconnected
                        if (netConnector.checkIfConnectionClosed()) {
                            netConnector.stopConnection();
                            working = false;
                        }
                        netConnector.sendResponse(responseParser.fromObjectToString(new CommandResponse(command.getNumber(), command.getName(), commandResult)));
                        Logging.log(Level.INFO, "Response to command " + command.getName() + " (" + command.getNumber() + ") was sent.");
                    }
                } catch (TimeoutException e) {
                    netConnector.stopConnection();
                    working = false;
                }
            }
        } catch (FileAccessException | InvalidArgumentsException e) {
            netConnector.sendResponse(responseParser.fromObjectToString(new CommandResponse(-1, e.getClass().getName(), "Server fail. Sorry, service is no more available.")));
            Logging.log(Level.WARNING, e.getLocalizedMessage());
        } finally {
            netConnector.stopConnection();
        }
        Logging.log(Level.INFO, "Exit from this client, because he disconnected!");

    }
}
