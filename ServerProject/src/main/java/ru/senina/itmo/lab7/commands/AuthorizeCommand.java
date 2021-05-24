package ru.senina.itmo.lab7.commands;

import ru.senina.itmo.lab7.*;

import java.util.Optional;

@CommandAnnotation(name = "authorize", isVisibleInHelp = false)
public class AuthorizeCommand extends Command{
    private String login;
    private String password;

    public AuthorizeCommand() {
        super("authorize", "Let client app send request to authorization.");
    }

    @Override
    protected CommandResponse doRun() {
        try {
            String token = Optional.ofNullable(DBManager.refreshToken(login, password)).orElseThrow(RuntimeException::new);
            return new CommandResponse(Status.OK, getName(), token);
        }catch (Exception e){
            //todo: catch different exceptions
            return new CommandResponse(Status.REGISTRATION_FAIL, getName(), "Something wrong with refreshing token in authorize command!" );
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
            throw new InvalidArgumentsException("Authorize command has to have 2 arguments - login and password.");
        }
    }

    @Override
    protected void checkIfLogin() throws UnLoginUserException {}
}