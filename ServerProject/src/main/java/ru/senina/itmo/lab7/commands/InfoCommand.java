package ru.senina.itmo.lab7.commands;

import ru.senina.itmo.lab7.CollectionKeeper;

/**
 * Command prints information about the collection (type, initialization date, number of elements, etc.) to the standard output stream
 */
@CommandAnnotation(name = "info", collectionKeeper = true)
public class InfoCommand extends CommandWithoutArgs {
    CollectionKeeper collectionKeeper;

    public InfoCommand() {
        super("info", "print information about the collection (type, initialization date, number of elements, etc.) to the standard output stream");
    }

    public void setCollectionKeeper(CollectionKeeper collectionKeeper){
        this.collectionKeeper = collectionKeeper;
    }

    @Override
    protected String doRun() {
        return "You have entered info command.\n" +
                "This collection was created: " + collectionKeeper.getTime() + "\n"
                + "Collection type: " + collectionKeeper.getType() + "\n"
                + "Amount of collection's elements: " + collectionKeeper.getAmountOfElements() + "\n";
    }
}
