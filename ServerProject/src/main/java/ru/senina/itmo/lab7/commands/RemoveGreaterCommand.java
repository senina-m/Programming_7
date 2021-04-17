package ru.senina.itmo.lab7.commands;

import ru.senina.itmo.lab7.CollectionKeeper;
import ru.senina.itmo.lab7.CommandResponse;
import ru.senina.itmo.lab7.labwork.LabWork;

/**
 * Command removes all elements greater than given
 */
@CommandAnnotation(name = "remove_greater", element = true, collectionKeeper = true)
public class RemoveGreaterCommand extends CommandWithoutArgs{
    private CollectionKeeper collectionKeeper;
    private LabWork element;

    public RemoveGreaterCommand() {
        super("remove_greater {element}", "remove all items from the collection that are greater than the specified one");
    }

    public void setCollectionKeeper(CollectionKeeper collectionKeeper){
        this.collectionKeeper = collectionKeeper;
    }

    @Override
    protected CommandResponse doRun() {
        return new CommandResponse(1, getName(), collectionKeeper.removeGreater(element));
    }

    public LabWork getElement() {
        return element;
    }

    @Override
    public void setElement(LabWork element) {
        this.element = element;
    }
}
