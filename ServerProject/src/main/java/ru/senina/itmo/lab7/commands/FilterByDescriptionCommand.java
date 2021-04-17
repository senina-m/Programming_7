package ru.senina.itmo.lab7.commands;

import ru.senina.itmo.lab7.CollectionKeeper;
import ru.senina.itmo.lab7.CommandResponse;
import ru.senina.itmo.lab7.InvalidArgumentsException;
import ru.senina.itmo.lab7.labwork.LabWork;
import ru.senina.itmo.lab7.CollectionKeeperParser;
import ru.senina.itmo.lab7.parser.ParsingException;

import java.util.List;

/**
 * Command to find all elements in collection with given description
 */
@CommandAnnotation(name = "filter_by_description", collectionKeeper = true, parser = true)
public class FilterByDescriptionCommand extends Command {
    private CollectionKeeper collectionKeeper;
    private String description;
    private CollectionKeeperParser parser;

    public FilterByDescriptionCommand() {
        super("filter_by_description description", "display elements whose description field value is equal to the given one");
    }

    @Override
    public void setCollectionKeeper(CollectionKeeper collectionKeeper) {
        this.collectionKeeper = collectionKeeper;
    }

    @Override
    public void setParser(CollectionKeeperParser parser) {
        this.parser = parser;
    }

    @Override
    protected CommandResponse doRun() {
        try {
            collectionKeeper.sort();
            List<LabWork> resultElements = collectionKeeper.filterByDescription(description);
            if(resultElements.size() != 0){
                StringBuilder result = new StringBuilder();
                result.append("You entered a command filter_by_description. These are the elements with description \"").append(description).append("\":\n");
                for(int i = 0; i < resultElements.size(); i++){
                    result.append("Element ").append(i + 1).append(": \n").append(parser.fromElementToString(resultElements.get(i))).append("\n");
                }
                return new CommandResponse(1, getName(), result.toString());
            }else{
                return new CommandResponse(2, getName(), "There is now elements with description \"" + description + "\".");
            }
        }catch (ParsingException e){
            return new CommandResponse(3, getName(), "Parsing in filter_by_description was failed. " + e.getMessage());
        }
    }

    @Override
    public void validateArguments() {
        String[] args = getArgs();
        if(args.length >= 2){
            StringBuilder description = new StringBuilder();
            for(int i = 1; i < args.length; i++){
                description.append(args[i]);
                if(i != args.length - 1){
                    description.append(" ");
                }
            }
            this.description = description.toString();
        }else {
            throw new InvalidArgumentsException("Command filter_by_description has to have a String argument.");
        }
    }
}
