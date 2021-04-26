package ru.senina.itmo.lab7.commands;

import ru.senina.itmo.lab7.CollectionKeeper;
import ru.senina.itmo.lab7.CollectionParser;
import ru.senina.itmo.lab7.CommandResponse;
import ru.senina.itmo.lab7.parser.Parser;

/**
 * Command saves collection to file
 */
@CommandAnnotation(name = "save", collectionKeeper = true, parser = true, filename = true)
public class SaveCommand extends CommandWithoutArgs {
    private CollectionKeeper collectionKeeper;
    private CollectionParser parser;
    private final String filename;

    public SaveCommand(String filename) {
        super("save", "save collection to file");
        this.filename = filename;
    }

    public void setCollectionKeeper(CollectionKeeper collectionKeeper) {
        this.collectionKeeper = collectionKeeper;
    }

    public void setParser(CollectionParser parser) {
        this.parser = parser;
    }

    @Override
    protected CommandResponse doRun() {
        Parser.writeStringToFile(filename, parser.fromObjectToString(collectionKeeper));
        return new CommandResponse(1, getName(), "Collection was successfully saved to " + filename + " file.");
    }
}
