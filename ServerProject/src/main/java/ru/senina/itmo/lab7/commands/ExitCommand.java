package ru.senina.itmo.lab7.commands;


import ru.senina.itmo.lab7.*;

@CommandAnnotation(name = "exit", collectionKeeper = true, parser = true)
public class ExitCommand extends CommandWithoutArgs{
    private CollectionKeeper collectionKeeper;
    private CollectionParser parser;
    public ExitCommand() {
        super("exit", "end the program (without saving to file)");
    }

    public void setCollectionKeeper(CollectionKeeper collectionKeeper) {
        this.collectionKeeper = collectionKeeper;
    }

    public void setParser(CollectionParser parser) {
        this.parser = parser;
    }

    @Override
    protected CommandResponse doRun() {
        return new CommandResponse(Status.OK, getName(), parser.fromObjectToString(new LabWorkList(collectionKeeper.getSortedList())));
    }
}
