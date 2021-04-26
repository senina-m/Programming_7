package ru.senina.itmo.lab7.commands;

import ru.senina.itmo.lab7.*;

import java.util.Optional;

@CommandAnnotation(name = "register")
public class RegisterCommand extends Command{
    private String password;
    private String login;

    protected RegisterCommand(String name, String description) {
        super("register", "registers user");
    }

    @Override
    protected CommandResponse doRun() {
        try {
            String token = Optional.ofNullable(DBManager.register(login, password)).orElseThrow(UserAlreadyExistsException::new);
            return new CommandResponse(1, getName(), token);
        }catch (UserAlreadyExistsException e){
            //todo: write table with exceptions values
            return new CommandResponse(5, getName(), "User with such login already exist!" );
        }
    }

    @Override
    public void validateArguments() {
        String[] args = getArgs();
        if (args.length == 3) {
                this.login = Optional.ofNullable(args[1]).orElseThrow(() ->
                        new InvalidArgumentsException("Login in register command can't be null!"));
                this.password = Optional.ofNullable(args[2]).orElseThrow(() ->
                        new InvalidArgumentsException("Password in register command can't be null!"));
        } else {
            throw new InvalidArgumentsException("Register command has to have 2 arguments - login and password.");
        }
    }

    @Override
    protected void checkIfLogin() throws UnLoginUserException {
        //todo: this command shouldn't check if user is login
    }
}
