package ru.senina.itmo.lab7;

import com.fasterxml.jackson.databind.ObjectMapper;
import ru.senina.itmo.lab7.parser.JsonParser;
import ru.senina.itmo.lab7.parser.Parser;

import java.io.File;
import java.nio.file.Files;
import java.util.LinkedList;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

/**
 * Class to deal with input and output and keep CollectionKeeper class instance.
 */
public class ClientKeeper {
    private String token;
    private final String filename;
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final ClientNetConnector netConnector = new ClientNetConnector();
    private TerminalKeeper terminalKeeper;
    private int recursionLevel = 0;
    private boolean working = true;
    private String host = "localhost";
    private int serverPort = 2;
    private int attemptsToConnect = 2;
    private int delayToConnect = 2;
    private final boolean debug = ClientMain.DEBUG;
    private final JsonParser<CommandResponse> responseParser = new JsonParser<>(objectMapper, CommandResponse.class);
    private final JsonParser<CommandArgs> commandArgsJsonParser = new JsonParser<>(objectMapper, CommandArgs.class);


    public ClientKeeper(String filename) {
        this.filename = filename;
    }

    public void start(int serverPort) {
        this.serverPort = serverPort;
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
        //todo: проверить, что ответ пришёл именно на нужную команду
        //todo: проверять, что ответ пришёл валидным
        authorize();
        terminalKeeper.setCommands(getCommandsMap());

        while (working) {
            try {
                CommandArgs command = terminalKeeper.readNextCommand();
                newCommand(command);
            } catch (InvalidServerAnswer e) {
                terminalKeeper.printResponse(new CommandResponse(Status.NETWORK_EXCEPTION, "processing server answer",
                        "Sorry, server failed to process your command. Please, try to run it again."));
            } catch (RefusedConnectionException e) {
                terminalKeeper.printResponse(new CommandResponse(Status.NETWORK_EXCEPTION, "disconnect from server",
                        "Sorry, server have disconnected. Try your command again, please!"));

            }
        }
        netConnector.stopConnection();
        System.exit(0);
    }

    private void newCommand(CommandArgs command) throws InvalidServerAnswer, RefusedConnectionException {
        switch (command.getCommandName()) {
            case ("execute_script"):
                if (recursionLevel < 10) {
                    recursionLevel++;
                    try {
                        LinkedList<CommandArgs> scriptCommands = terminalKeeper.executeScript(command.getArgs()[1]);
                        for (CommandArgs c : scriptCommands) {
                            newCommand(c);
                        }
                    } catch (FileAccessException e) {
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
                tryToConnect(host, this.serverPort, attemptsToConnect, delayToConnect);
                netConnector.sendMessage(message);
                String response = Optional.ofNullable(netConnector.receiveMessage()).orElseThrow(InvalidServerAnswer::new);
                netConnector.stopConnection();
                CommandResponse commandAnswer = responseParser.fromStringToObject(response);
                if(commandAnswer.getCommandName().equals("save")){
                    Parser.writeStringToFile(filename, commandAnswer.getResponse());
                    commandAnswer.setResponse("Collection was successfully saved to file '" + filename + "!");
                }
                terminalKeeper.printResponse(commandAnswer);
        }
    }

    private void authorize() {
        CommandArgs authorizationCommand = terminalKeeper.authorizeUser();
        tryToConnect(host, serverPort, attemptsToConnect, delayToConnect);
        netConnector.sendMessage(commandArgsJsonParser.fromObjectToString(authorizationCommand));
        CommandResponse authResponse = responseParser.fromStringToObject(netConnector.receiveMessage());
        netConnector.stopConnection();
        if (authResponse.getCode().equals(Status.REGISTRATION_FAIL)) {
            if (authResponse.getCommandName().equals("register")) {//Code 5 - exception such user already exist
                terminalKeeper.printResponse(new CommandResponse(authResponse.getCode(), authResponse.getCommandName(),
                        "User with such login already exist! Try to register again!"));
                authorize();
            } else {
                terminalKeeper.printResponse(new CommandResponse(authResponse.getCode(), authResponse.getCommandName(),
                        "Your password or login isn't correct! Try to log in again!"));
                authorize();
            }
        } else {
            terminalKeeper.printResponse(new CommandResponse(authResponse.getCode(), authResponse.getCommandName(),
                    "You have successfully logged in! \nNow you can enter commands! (to see full command's list enter 'help')"));
            token = authResponse.getResponse();
        }
    }

    private Map<String, String[]> getCommandsMap() {
        CommandArgs requestCommandsMapCommand = new CommandArgs("request_map_of_commands", new String[]{});
        requestCommandsMapCommand.setToken(token);
        tryToConnect(host, serverPort, attemptsToConnect, delayToConnect);
        netConnector.sendMessage(commandArgsJsonParser.fromObjectToString(requestCommandsMapCommand));
        CommandResponse requestCommandsMapResponse = responseParser.fromStringToObject(netConnector.receiveMessage());
        netConnector.stopConnection();
        String mapOfCommandsString = requestCommandsMapResponse.getResponse();
        SetOfCommands setOfCommands = new JsonParser<>(objectMapper, SetOfCommands.class).fromStringToObject(mapOfCommandsString);
        return setOfCommands.getCommandsWithArgs();
    }

    public void tryToConnect(String host, int serverPort, int attempts, int delay) {
        for (int i = 0; i < attempts; i++) {
            try {
                netConnector.startConnection(host, serverPort);
                return;
            } catch (RefusedConnectionException e) {
                terminalKeeper.printResponse(new CommandResponse(Status.NETWORK_EXCEPTION, "connect to server", "Sorry, now server is not available! We will try to reconnect in " + delay + " seconds!"));
                try {
                    TimeUnit.SECONDS.sleep(delay);
                } catch (InterruptedException ex) {
                    if (debug) {
                        System.out.println("DEBUG: EXCEPTION in tryToConnect " + ex.toString());
                    }
                }
            }
        }
        terminalKeeper.printResponse(new CommandResponse(Status.NETWORK_EXCEPTION, "connect to server", "Can't connect to server! Try again later!"));
        netConnector.stopConnection();
        System.exit(0); //todo: check if it's OK
    }
}

