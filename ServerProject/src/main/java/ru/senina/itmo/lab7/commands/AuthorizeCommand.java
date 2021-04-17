package ru.senina.itmo.lab7.commands;

import ru.senina.itmo.lab7.CommandResponse;
import ru.senina.itmo.lab7.UnLoginUserException;

//todo: дописать в аннатацию параметры для этой команды
@CommandAnnotation(name = "authorize")
public class AuthorizeCommand extends CommandWithoutArgs{
    public AuthorizeCommand() {
        super("authorize", "Let client app send request to authorization.");
    }

    @Override
    protected CommandResponse doRun() {
        //Todo: запос к базе данных и возврат токена
        return null;
    }

    @Override
    protected void checkIfLogin() throws UnLoginUserException {
        //todo: this command shouldn't check if user is login
    }
}