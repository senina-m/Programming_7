package ru.senina.itmo.lab7.commands;

import ru.senina.itmo.lab7.CollectionKeeper;
import ru.senina.itmo.lab7.CommandResponse;
import ru.senina.itmo.lab7.InvalidArgumentsException;
import ru.senina.itmo.lab7.labwork.LabWork;

/**
 * Command updates the element with given ID in collection
 */
@CommandAnnotation(name = "update", element = true, collectionKeeper = true, id = true)
public class UpdateCommand extends Command{
    private CollectionKeeper collectionKeeper;
    private LabWork element;
    private long id;

    public UpdateCommand() {
        super("update id {element}", "update the value of the collection element whose id is equal to the given");
    }

    public void setCollectionKeeper(CollectionKeeper collectionKeeper) {
        this.collectionKeeper = collectionKeeper;
    }

    @Override
    protected CommandResponse doRun() {
        return new CommandResponse(1, getName(), collectionKeeper.updateID(id, element, getToken()));
    }

    @Override
    public void validateArguments() {
        String[] args = getArgs();
        if(args.length == 2){
            try{
                this.id = Long.parseLong(args[1]);
            }
            catch (NumberFormatException e){
                throw new InvalidArgumentsException("Update command argument has to be long.");
            }
        }else {
            throw new InvalidArgumentsException("Update command has to have an argument - id of the element.");
        }
    }

    public LabWork getElement() {
        return element;
    }

    @Override
    public void setElement(LabWork element) {
        this.element = element;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }
}
