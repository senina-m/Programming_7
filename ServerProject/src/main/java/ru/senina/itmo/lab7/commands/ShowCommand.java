package ru.senina.itmo.lab7.commands;

import ru.senina.itmo.lab7.*;
import ru.senina.itmo.lab7.parser.ParsingException;

/**
 * Command shows all collection elements
 */
@CommandAnnotation(name = "show", collectionKeeper = true, parser = true)
public class ShowCommand extends CommandWithoutArgs{

    private CollectionKeeper collectionKeeper;
    private CollectionParser parser;

    public ShowCommand() {
        super("show", "print to standard output all elements of the collection in string representation");
    }

    public void setCollectionKeeper(CollectionKeeper collectionKeeper) {
        this.collectionKeeper = collectionKeeper;
    }

    public void setParser(CollectionParser parser) {
        this.parser = parser;
    }

    @Override
    protected CommandResponse doRun(){
        try {
            if(collectionKeeper.getAmountOfElements()!= 0) {
                return new CommandResponse(Status.OK, getName(), parser.fromCollectionToStringElements(new LabWorkList(collectionKeeper.getList())));
            } else {
                return new CommandResponse(Status.PROBLEM_PROCESSED, getName(), "No elements in collection.");
            }
        }
        catch (ParsingException e){
            return new CommandResponse(Status.PARSER_EXCEPTION, getName(), "Parsing was failed. " + e.getMessage());
        }
    }
}
