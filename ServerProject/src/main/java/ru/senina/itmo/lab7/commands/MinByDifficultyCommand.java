package ru.senina.itmo.lab7.commands;

import ru.senina.itmo.lab7.CollectionKeeper;
import ru.senina.itmo.lab7.CollectionParser;
import ru.senina.itmo.lab7.CommandResponse;
import ru.senina.itmo.lab7.Status;
import ru.senina.itmo.lab7.parser.ParsingException;

/**
 * Command class to find the minimum difficult subject in the collection
 */
@CommandAnnotation(name = "min_by_difficulty", collectionKeeper = true, parser = true)
public class MinByDifficultyCommand extends CommandWithoutArgs {
    private CollectionKeeper collectionKeeper;
    private CollectionParser parser;

    @Override
    public void setParser(CollectionParser parser) {
        this.parser = parser;
    }

    public MinByDifficultyCommand() {
        super("min_by_difficulty", "remove any object from the collection with the minimum difficulty value");
    }

    public void setCollectionKeeper(CollectionKeeper collectionKeeper){
        this.collectionKeeper = collectionKeeper;
    }

    @Override
    protected CommandResponse doRun() {
        try {
            return new CommandResponse(Status.OK, getName(), "The less difficult subject is: \n" + parser.fromElementToString(collectionKeeper.minByDifficulty()));
        } catch (IndexOutOfBoundsException e){
            return new CommandResponse(Status.PROBLEM_PROCESSED, getName(), "Can't do min_by_difficulty command. " + e.getMessage());
        } catch ( ParsingException e){
            return new CommandResponse(Status.PARSER_EXCEPTION, getName(), "Minimal element with such description was incorrect.");
        }
    }
}
