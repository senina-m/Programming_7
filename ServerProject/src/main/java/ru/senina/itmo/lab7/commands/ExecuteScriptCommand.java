package ru.senina.itmo.lab7.commands;

import ru.senina.itmo.lab7.CommandResponse;
import ru.senina.itmo.lab7.InvalidArgumentsException;

@CommandAnnotation(name = "execute_script", filename = true)
public class ExecuteScriptCommand extends Command {
    public ExecuteScriptCommand() {
        super("execute_script file_name", "read and execute the script from the specified file.");
    }

    @Override
    protected CommandResponse doRun() {
        return new CommandResponse(1, getName(), "Execute script ");
    }

    @Override
    public void validateArguments() {
        String[] args = getArgs();
        if (args.length != 2) {
            throw new InvalidArgumentsException("Execute script command has to have an argument - file path.");
        }
    }

}
