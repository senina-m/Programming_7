package ru.senina.itmo.lab7.commands;

import ru.senina.itmo.lab7.CollectionKeeper;
import ru.senina.itmo.lab7.CommandResponse;
import ru.senina.itmo.lab7.Status;

/**
 * Command clear collection - delete all elements
 */
@CommandAnnotation(name = "clear", collectionKeeper = true)
public class ClearCommand extends CommandWithoutArgs{
    private CollectionKeeper collectionKeeper;
    public ClearCommand() {
        super("clear", "clear collection");
    }

    public void setCollectionKeeper(CollectionKeeper collectionKeeper){
        this.collectionKeeper = collectionKeeper;
    }

    @Override
    protected CommandResponse doRun() {
        return new CommandResponse(Status.OK, getName(), collectionKeeper.clear(getToken()));
    }
}
