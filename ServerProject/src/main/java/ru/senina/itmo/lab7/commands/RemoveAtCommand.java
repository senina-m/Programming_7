package ru.senina.itmo.lab7.commands;

import ru.senina.itmo.lab7.CollectionKeeper;
import ru.senina.itmo.lab7.CommandResponse;
import ru.senina.itmo.lab7.InvalidArgumentsException;
import ru.senina.itmo.lab7.Status;

/** * Command that removes element on given place in collection
 */
@CommandAnnotation(name = "remove_at", collectionKeeper = true, index = true)
public class RemoveAtCommand extends Command{
    private CollectionKeeper collectionKeeper;
    private int index;

    public RemoveAtCommand() {
        super("remove_at index", "remove the element at the given collection position (index)");
    }

    public void setCollectionKeeper(CollectionKeeper collectionKeeper){
        this.collectionKeeper = collectionKeeper;
    }

    @Override
    protected CommandResponse doRun() {
        return new CommandResponse(Status.OK, getName(), collectionKeeper.removeAt(index, getToken()));
    }

    @Override
    public void validateArguments() {
        String[] args = getArgs();
        if (args.length == 2) {
            try {
                this.index = Integer.parseInt(args[1]);
            } catch (NumberFormatException e) {
                throw new InvalidArgumentsException("Remove_at command argument has to be Integer.");
            }
        } else {
            throw new InvalidArgumentsException("Remove_at command has to have an argument - index of the element.");
        }
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }
}
