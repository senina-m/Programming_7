package ru.senina.itmo.lab7.commands;

import ru.senina.itmo.lab7.CollectionKeeper;
import ru.senina.itmo.lab7.CollectionKeeperParser;
import ru.senina.itmo.lab7.CommandResponse;
import ru.senina.itmo.lab7.parser.ParsingException;

/**
 * Command shows all collection elements
 */
@CommandAnnotation(name = "show", collectionKeeper = true, parser = true)
public class ShowCommand extends CommandWithoutArgs{

    private CollectionKeeper collectionKeeper;
    private CollectionKeeperParser parser;

    public ShowCommand() {
        super("show", "print to standard output all elements of the collection in string representation");
    }

    public void setCollectionKeeper(CollectionKeeper collectionKeeper) {
        this.collectionKeeper = collectionKeeper;
    }

    public void setParser(CollectionKeeperParser parser) {
        this.parser = parser;
    }

    @Override
    protected CommandResponse doRun(){
        try {
            if(collectionKeeper.getAmountOfElements()!= 0) {
                collectionKeeper.sort();
                return new CommandResponse(1, getName(), parser.fromCollectionToStringElements(collectionKeeper));
            } else {
                return new CommandResponse(2, getName(), "No elements in collection.");
            }
        }
        catch (ParsingException e){
            return new CommandResponse(3, getName(), "Parsing was failed. " + e.getMessage());
        }
    }
}
