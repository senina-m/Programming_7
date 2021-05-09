package ru.senina.itmo.lab7.commands;

import ru.senina.itmo.lab7.CommandResponse;
import ru.senina.itmo.lab7.Status;

import java.util.Map;

/**
 * Command displays help for available ru.senina.itmo.lab6.commands
 */

@CommandAnnotation(name = "help")
public class HelpCommand extends CommandWithoutArgs {
    private Map<String, Command> commands;

    public HelpCommand(Map<String, Command> commands) {
        super("help", "displays help for available commands");
        this.commands = commands;
    }

    @Override
    public CommandResponse doRun() {
        StringBuilder string = new StringBuilder();
        string.append("The full list of Commands is here: \n");
        for(Command command: commands.values()){
            string.append(command.getName()).append(" : ").append(command.getDescription()).append("\n");
        }
        return new CommandResponse(Status.OK, getName(), string.toString());
    }

    public Map<String, Command> getCommands() {
        return commands;
    }

    public void setCommands(Map<String, Command> commands) {
        this.commands = commands;
    }
}