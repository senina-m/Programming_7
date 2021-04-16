package ru.senina.itmo.lab7;

import com.fasterxml.jackson.databind.ObjectMapper;
import ru.senina.itmo.lab7.parser.JsonParser;
import ru.senina.itmo.lab7.parser.Parser;

import java.io.File;
import java.nio.file.Files;
import java.util.LinkedList;
import java.util.Optional;

/**
 * Class to deal with input and output and keep CollectionKeeper class instance.
 */
public class ClientKeeper {
    private final String filename;
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final ClientNetConnector netConnector = new ClientNetConnector();
    private TerminalKeeper terminalKeeper;
    private int numberOfCommands = 0;
    private int recursionLevel = 0;
    private boolean working = true;
    private final JsonParser<CommandResponse> responseParser = new JsonParser<>(objectMapper, CommandResponse.class);
    private final JsonParser<CommandArgs> commandArgsJsonParser = new JsonParser<>(objectMapper, CommandArgs.class);
    /**
     * @param filename the path to file from which we read and to which we write collection data
     */
    public ClientKeeper(String filename) {
        this.filename = filename;
    }

    /**
     * Method to start a new collection and System.in reader
     */
    public void start(int serverPort) {
        CommandArgs createCollectionCommand = null;
        try {
            File f = new File(filename);
            if (f.isDirectory() || !Files.isReadable(f.toPath())) {
                System.out.println("There is no rights for reading file. Change rights and run program again!");
                System.exit(0);
            }
            createCollectionCommand = new CommandArgs("create_collection", new String[]{"create_collection", Parser.fromFileToString(filename)});
        } catch (NullPointerException e) {
            System.out.println("File path is wrong. Run program again with correct filename.");
            System.exit(0);
        }

        netConnector.startConnection("localhost", serverPort);
        terminalKeeper = new TerminalKeeper(filename);
        try {
            String message = Optional.ofNullable(netConnector.receiveMessage()).orElseThrow(InvalidServerAnswer::new);
            SetOfCommands commandsMap = new JsonParser<>(objectMapper, SetOfCommands.class).fromStringToObject(message);
            commandsMap.getCommandsWithArgs().put("execute_script", new String[]{""});
            terminalKeeper.setCommands(commandsMap.getCommandsWithArgs());
            newCommand(createCollectionCommand);
        }catch (InvalidServerAnswer e){
            terminalKeeper.printResponse(new CommandResponse(-1, "Exception in server answer: failed reading initial set of commands", "Sorry, server failed. Service is currently unavailable."));
        }
        while (working) {
            try {
                CommandArgs command = terminalKeeper.readNextCommand();
                newCommand(command);
            }catch (InvalidServerAnswer e){
                terminalKeeper.printResponse(new CommandResponse(-1, "Exception in server answer", "Sorry, server failed to process your command. Please, try to run it again."));
            }
        }
        netConnector.stopConnection();
        System.exit(0);
    }

    public void newCommand(CommandArgs command) throws InvalidServerAnswer{
        switch (command.getCommandName()) {
            case ("execute_script"):
                if (recursionLevel < 10) {
                    recursionLevel++;
                    try {
                        LinkedList<CommandArgs> scriptCommands = terminalKeeper.executeScript(command.getArgs()[1]);
                        for (CommandArgs c : scriptCommands) {
                            newCommand(c);
                        }
                    }catch (FileAccessException e){
                        terminalKeeper.printResponse(new CommandResponse(numberOfCommands++, command.getCommandName(), e.getMessage()));
                    }
                } else {
                    terminalKeeper.printResponse(new CommandResponse(numberOfCommands++, "execute_script",
                            "You have stacked in the recursion! It's not allowed to deep in more then 10 levels. " +
                                    "\n No more recursive scripts would be executed!"));
                    recursionLevel = 0;
                }
                break;
            case ("exit"):
                working = false;
            default:
                command.setNumber(numberOfCommands++);
                String message = commandArgsJsonParser.fromObjectToString(command);
                netConnector.sendMessage(message);
                String response = Optional.ofNullable(netConnector.receiveMessage()).orElseThrow(InvalidServerAnswer::new);
                CommandResponse commandAnswer = responseParser.fromStringToObject(response);
                terminalKeeper.printResponse(commandAnswer);
        }
    }
}

