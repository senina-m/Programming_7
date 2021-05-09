package ru.senina.itmo.lab7;

import com.fasterxml.jackson.databind.ObjectMapper;
import ru.senina.itmo.lab7.parser.JsonParser;

import java.io.File;
import java.nio.file.Files;
import java.util.LinkedList;
import java.util.Map;
import java.util.Optional;

/**
 * Class to deal with input and output and keep CollectionKeeper class instance.
 */
public class ClientKeeper {
    private String token;
    private final String filename;
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final ClientNetConnector netConnector = new ClientNetConnector();
    private TerminalKeeper terminalKeeper;
    private int numberOfCommands = 0;
    private int recursionLevel = 0;
    private boolean working = true;
    private final boolean debug = ClientMain.DEBUG;
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
        try {
            File f = new File(filename);
            if (f.isDirectory() || !Files.isReadable(f.toPath())) {
                System.out.println("There is no rights for reading file. Change rights and run program again!");
                System.exit(0);
            }
            //Проверяем что файл который пользователь ввёл - валидный, потому что иначе мы не сможем туда сохранить коллекцию
        } catch (NullPointerException e) {
            System.out.println("File path is wrong. Run program again with correct filename.");
            System.exit(0);
        }

        terminalKeeper = new TerminalKeeper(filename);
        try {
            netConnector.startConnection("localhost", serverPort);
        }catch (RefusedConnectionException e){
            throw new RuntimeException(" Server is not available! java.net.ConnectException");
            //todo: catch exception and try again
        }


        //todo: проверить, что ответ пришёл именно на нужную команду
        //todo: проверять, что ответ пришёл валидным
        authorize();
        terminalKeeper.setCommands(getCommandsMap());

        while (working) {
            try {
                CommandArgs command = terminalKeeper.readNextCommand();
                newCommand(command);
            }catch (InvalidServerAnswer e){
                terminalKeeper.printResponse(new CommandResponse(Status.NETWORK_EXCEPTION, "Exception in server answer", "Sorry, server failed to process your command. Please, try to run it again."));
            }
        }
        netConnector.stopConnection();
        System.exit(0);
    }

    private void newCommand(CommandArgs command) throws InvalidServerAnswer{
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
                        terminalKeeper.printResponse(new CommandResponse(Status.ACCESS_EXCEPTION, command.getCommandName(), e.getMessage()));
                    }
                } else {
                    terminalKeeper.printResponse(new CommandResponse(Status.SCRIPT_RECURSION_EXCEPTION, "execute_script",
                            "You have stacked in the recursion! It's not allowed to deep in more then 10 levels. " +
                                    "\n No more recursive scripts would be executed!"));
                    recursionLevel = 0;
                }
                break;
            case ("exit"):
                working = false;
            default:
                command.setToken(token);
                String message = commandArgsJsonParser.fromObjectToString(command);
                netConnector.sendMessage(message);
                String response = Optional.ofNullable(netConnector.receiveMessage()).orElseThrow(InvalidServerAnswer::new);
                CommandResponse commandAnswer = responseParser.fromStringToObject(response);
                terminalKeeper.printResponse(commandAnswer);
        }
    }

    private void authorize(){
        CommandArgs authorizationCommand = terminalKeeper.authorizeUser();
        netConnector.sendMessage(commandArgsJsonParser.fromObjectToString(authorizationCommand));
        CommandResponse authResponse = responseParser.fromStringToObject(netConnector.receiveMessage());
        if(!authResponse.getCode().equals(Status.REGISTRATION_FAIL)){ //Code 5 - exception such user already exist
            terminalKeeper.printResponse(new CommandResponse(authResponse.getCode(), authResponse.getCommandName(),
                    "User with such login already exist! Try to register again!"));
            authorize();
        }else {
            token = authResponse.getResponse();
        }
    }

    private Map<String, String[]> getCommandsMap(){
        CommandArgs requestCommandsMapCommand = new CommandArgs("request_map_of_commands", new String[]{});
        requestCommandsMapCommand.setToken(token);
        netConnector.sendMessage(commandArgsJsonParser.fromObjectToString(requestCommandsMapCommand));
        CommandResponse requestCommandsMapResponse = responseParser.fromStringToObject(netConnector.receiveMessage());
        String mapOfCommandsString = requestCommandsMapResponse.getResponse();
        SetOfCommands setOfCommands = new JsonParser<>(objectMapper, SetOfCommands.class).fromStringToObject(mapOfCommandsString);
        return setOfCommands.getCommandsWithArgs();
    }
}

