package ru.senina.itmo.lab7.commands;

import ru.senina.itmo.lab7.*;

import java.util.Optional;
import java.util.logging.Level;

@CommandAnnotation(name = "register", isVisibleInHelp = false)
public class RegisterCommand extends Command{
    private String password;
    private String login;

    public RegisterCommand() {
        super("register", "registers user");
    }

    @Override
    protected CommandResponse doRun() {
        try {
            String token = Optional.of(DBManager.register(login, password)).orElseThrow(DataBaseProcessException::new);
            return new CommandResponse(Status.OK, getName(), token);
        }catch (UserAlreadyExistsException e){
            //todo: write table with exceptions values
            return new CommandResponse(Status.REGISTRATION_FAIL, getName(), "User with such login already exist!" );
        }catch (DataBaseProcessException e){
            return new CommandResponse(Status.DB_EXCEPTION, getName(), "Some problems with processing register command in DB!" );
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
