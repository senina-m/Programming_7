package ru.senina.itmo.lab7.commands;

import ru.senina.itmo.lab7.CollectionKeeper;

/**
 * Command sorts collection
 */
@CommandAnnotation(name = "sort", collectionKeeper = true)
public class SortCommand extends CommandWithoutArgs {
    private CollectionKeeper collectionKeeper;

    public SortCommand() {
        super("sort", "sort the collection in natural order");
    }

    public void setCollectionKeeper(CollectionKeeper collectionKeeper) {
        this.collectionKeeper = collectionKeeper;
    }

    @Override
    protected String doRun() {
        return collectionKeeper.sort();
    }
}
