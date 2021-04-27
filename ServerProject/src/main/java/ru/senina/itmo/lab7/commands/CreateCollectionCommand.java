package ru.senina.itmo.lab7.commands;

import ru.senina.itmo.lab7.*;
import ru.senina.itmo.lab7.labwork.LabWork;
import ru.senina.itmo.lab7.parser.ParsingException;

import java.util.LinkedList;
import java.util.logging.Level;

@CommandAnnotation(name = "create_collection", collectionKeeper = true, parser = true, filename = true)
public class CreateCollectionCommand extends Command {
    private CollectionKeeper collectionKeeper;
    private CollectionParser parser;
    private String collectionString;

    public String getCollectionString() {
        return collectionString;
    }

    public void setCollectionString(String filename) {
        this.collectionString = filename;
    }

    public CreateCollectionCommand() {
        super("create_collection", "create collection from elements from given file");
    }

    @Override
    public void setCollectionKeeper(CollectionKeeper collectionKeeper) {
        this.collectionKeeper = collectionKeeper;
    }

    @Override
    public void setParser(CollectionParser parser) {
        this.parser = parser;
    }

    @Override
    protected CommandResponse doRun() throws InvalidArgumentsException {
        try {
            LinkedList<LabWork> list = (LinkedList<LabWork>) parser.fromStringToObject(collectionString).getLabWorkList();
            for (LabWork element: list){
                collectionKeeper.add(element, getToken());
            }
        } catch (ParsingException e) {
            Logging.log(Level.WARNING, "Collection file was incorrect, collection wasn't updated with start values.");
            return new CommandResponse(2, getName(), "File was incorrect, collection will be empty!");
        }
        if (collectionKeeper.getList() != null) {
            return new CommandResponse(1, getName(), "Collection was successfully created");
        } else {
            throw new InvalidArgumentsException("File " + collectionString + " was invalid.");
        }

    }

    @Override
    public void validateArguments() {
        String[] args = this.getArgs();
        if (args.length == 2) {
            this.collectionString = args[1];
        } else {
            throw new InvalidArgumentsException("Create collection command has the only argument - contents of a collection file.");
        }
    }
}
