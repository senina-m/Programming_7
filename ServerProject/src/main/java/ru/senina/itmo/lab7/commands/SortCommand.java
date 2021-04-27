package ru.senina.itmo.lab7.commands;

import ru.senina.itmo.lab7.CollectionKeeper;
import ru.senina.itmo.lab7.CollectionParser;
import ru.senina.itmo.lab7.CommandResponse;
import ru.senina.itmo.lab7.LabWorkList;

/**
 * Command sorts collection
 */
@CommandAnnotation(name = "sort", collectionKeeper = true, parser = true)
public class SortCommand extends CommandWithoutArgs {
    private CollectionKeeper collectionKeeper;
    private CollectionParser parser;

    public SortCommand() {
        super("sort", "sort the collection in natural order and return it");
    }

    public void setCollectionKeeper(CollectionKeeper collectionKeeper) {
        this.collectionKeeper = collectionKeeper;
    }
    public void setParser(CollectionParser parser) {
        this.parser = parser;
    }

    @Override
    protected CommandResponse doRun() {
        return new CommandResponse(1, getName(), parser.fromObjectToString(new LabWorkList(collectionKeeper.getSortedList())));
    }
}
