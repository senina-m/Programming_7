package ru.senina.itmo.lab7;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import ru.senina.itmo.lab7.labwork.LabWork;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.logging.Level;

/**
 * Class to keep collection's elements
 */
public class CollectionKeeper {
    @JsonCreator
    public CollectionKeeper() {
    }

    @JsonIgnore
    private final Date time = new Date();
    //TODO: think how to save creation time for full table (do i actually need it?)

//--------------------------METHODS----------------------------------------------------------------------

    public List<LabWork> getList() {
        return DBManager.readAll();
    }


    @JsonIgnore
    public long getAmountOfElements() {
        return Optional.of(DBManager.countNumOfElements()).orElse((long) 0);
    }

    public String getTime() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy.MM.dd G 'at' HH:mm:ss z");
        return dateFormat.format(time);
    }

    public String updateID(long id, LabWork element, String token) {
        try {
            DBManager.updateById(element, id, token);
            return "Element with id: " + id + " was successfully updated.";
        } catch (Exception e) {
            return "There is no element with id: " + id + " in collection.";
            //todo: possess different exceptions
        }
    }

    public String add(LabWork element, String token) {
        try {
            DBManager.addElement(element, token);
            return "Element with id: " + element.getId() + " was successfully added.";
        } catch (DBProcessException e) {
            return "Element wasn't added to collection due to problems with data base!";
        } catch (Exception e) {
            Logging.log(Level.WARNING, "Something wrong with adding element to collection. (Warning from collectionKeeper)" + e.getMessage());
            //todo: possess different exceptions
            throw new RuntimeException("Something wrong with adding element to collection. (Warning from collectionKeeper) " + e.getMessage());
        }
    }

    public String removeById(long id, String token) {
        try {
            DBManager.removeById(id, token);
            return "Element with id: " + id + " was successfully removed.";
        } catch (Exception e) {
            return "There is no element with id: " + id + " in collection.";
            //todo: possess different exceptions
        }
    }

    public String clear(String token) {
        try {
            DBManager.clear(token);
            return "The collection was successfully cleared.";
        } catch (Exception e) {
            Logging.log(Level.WARNING, "Something wrong with clearing of collection. (Warning from collectionKeeper)" + e.getMessage());
            //todo: possess different exceptions
            throw new RuntimeException("Something wrong with clearing of collection. (Warning from collectionKeeper) " + e.getMessage());
        }
    }

    public String removeAt(int index, String token) {
        try {
            DBManager.removeAtIndex(index, token);
            return "Element with index " + index + " was successfully removed.";
        } catch (Exception e) {
            //todo: possess different exceptions
            return "Removing an element with index " + index + " was failed. No such index in the collection.";
        }
    }

    public String removeGreater(LabWork element, String token) {
        try {
            DBManager.removeGreater(element, token);
            return "All elements greater then entered were successfully removed.";
        } catch (Exception e) {
            Logging.log(Level.WARNING, "Something wrong with removing_greater elements of collection. (Warning from collectionKeeper)" + e.getMessage());
            //todo: possess different exceptions
            throw new RuntimeException("Something wrong with removing_greater elements of collection. (Warning from collectionKeeper) " + e.getMessage());
        }
    }


    public LabWork minByDifficulty() throws IndexOutOfBoundsException {
        try {
            return DBManager.minByDifficulty();
        } catch (Exception e) {
            Logging.log(Level.WARNING, "Something wrong with minByDifficulty elements of collection. (Warning from collectionKeeper)" + e.getMessage());
            //todo: possess different exceptions
            throw new InvalidArgumentsException("No elements in collection. Can't choose the less by Difficulty.");
        }
    }

    public List<LabWork> filterByDescription(String description) {
        try {
            return DBManager.filterByDescription(description);
        } catch (Exception e) {
            Logging.log(Level.WARNING, "Something wrong with filterByDescription of collection. (Warning from collectionKeeper)" + e.getMessage());
            //todo: possess different exceptions
            throw new RuntimeException("Something wrong with filterByDescription of collection. (Warning from collectionKeeper) " + e.getMessage());
        }
    }


    @JsonIgnore
    public List<LabWork> getSortedList() {
        try {
            return DBManager.getSortedList();
        } catch (Exception e) {
            Logging.log(Level.WARNING, "Something wrong with getSortedList of collection. (Warning from collectionKeeper)" + e.getMessage());
            //todo: possess different exceptions
            throw new RuntimeException("Something wrong with getSortedList of collection. (Warning from collectionKeeper) " + e.getMessage());
        }
    }
}
