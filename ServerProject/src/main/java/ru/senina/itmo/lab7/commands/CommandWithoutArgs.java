package ru.senina.itmo.lab7.commands;

import ru.senina.itmo.lab7.InvalidArgumentsException;

/**
 * Command's who don't have string arguments parent
 */
public abstract class CommandWithoutArgs extends Command{

    public CommandWithoutArgs(String name, String description) {
        super(name, description);
    }

    @Override
    public void validateArguments() {
        if(this.getArgs().length > 1){
            throw new InvalidArgumentsException("This command doesn't have any arguments.");
        }
    }
}