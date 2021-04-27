package ru.senina.itmo.lab7.commands;

import ru.senina.itmo.lab7.CollectionKeeper;
import ru.senina.itmo.lab7.CommandResponse;
import ru.senina.itmo.lab7.labwork.LabWork;

/**
 * Command adds new element to collection
 */
@CommandAnnotation(name = "add", element = true, collectionKeeper = true)
public class AddCommand extends CommandWithoutArgs{
    CollectionKeeper collectionKeeper;
    private LabWork element;

    public LabWork getElement() {
        return element;
    }

    @Override
    public void setElement(LabWork element) {
        this.element = element;
    }

    public AddCommand() {
        super("add {element}", "add new element to collection");
    }

    public void setCollectionKeeper(CollectionKeeper collectionKeeper){
        this.collectionKeeper = collectionKeeper;
    }

    @Override
    protected CommandResponse doRun() {
        return new CommandResponse(1, getName(), collectionKeeper.add(element, getToken()));
    }

}
