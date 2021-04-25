package ru.senina.itmo.lab7;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import ru.senina.itmo.lab7.labwork.Coordinates;
import ru.senina.itmo.lab7.labwork.Difficulty;
import ru.senina.itmo.lab7.labwork.Discipline;
import ru.senina.itmo.lab7.labwork.LabWork;

import java.util.LinkedList;

public class JacksonTest {

    public static ObjectMapper objectMapper = new ObjectMapper();

    static {
        objectMapper.configure(SerializationFeature.INDENT_OUTPUT, true);
        objectMapper.configure(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY, true);
    }

    @Test
    @Disabled
    public void testFromObjectToJson() {
        String name = "element";
        Coordinates coordinates = new Coordinates(2, 3);
        float minimalPoint = 80;
        String description = "my lovely Hori";
        Integer averagePoint = 9;
        Difficulty difficulty = Difficulty.HOPELESS;
        Discipline discipline = new Discipline("Killing", 35, 65, 26);
        LabWork labWork = new LabWork(name, coordinates, minimalPoint, description, averagePoint, difficulty, discipline);
        CollectionKeeper collectionKeeper = new CollectionKeeper();

        CollectionKeeperParser parser = new CollectionKeeperParser(objectMapper, CollectionKeeper.class);
        String json = parser.fromObjectToString(collectionKeeper);
        System.out.println(json);
        CollectionKeeper newObject = parser.fromStringToObject(json);
        System.out.println("Test finished");
    }

    @Test
    public void labWorkSerialization(){
        String name = "element";
        Coordinates coordinates = new Coordinates(2, 3);
        float minimalPoint = 80;
        String description = "my lovely Hori";
        Integer averagePoint = 9;
        Difficulty difficulty = Difficulty.HOPELESS;
        Discipline discipline = new Discipline("Killing", 35, 65, 26);
        LabWork labWork = new LabWork(name, coordinates, minimalPoint, description, averagePoint, difficulty, discipline);
        CollectionKeeperParser parser = new CollectionKeeperParser(objectMapper, CollectionKeeper.class);
        String json = parser.fromElementToString(labWork);
        System.out.println(json);
        LabWork newLabWork = parser.fromStringToElement(json);
        System.out.println("LabWork test finished");
    }

}
