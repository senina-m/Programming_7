package ru.senina.itmo.lab7;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.junit.jupiter.api.Test;
import ru.senina.itmo.lab7.labwork.Coordinates;
import ru.senina.itmo.lab7.labwork.Difficulty;
import ru.senina.itmo.lab7.labwork.Discipline;
import ru.senina.itmo.lab7.labwork.LabWork;

import java.util.LinkedList;
import java.util.List;

public class JacksonTest {

    public static ObjectMapper objectMapper = new ObjectMapper();

    static {
        objectMapper.configure(SerializationFeature.INDENT_OUTPUT, true);
        objectMapper.configure(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY, true);
    }

    @Test
    public void objectSerialization() {
        LabWork labWork = createElement();
        List<LabWork> list = new LinkedList<>();
        list.add(labWork);
        list.add(labWork);
        LabWorkList labWorkList = new LabWorkList(list);


        CollectionParser parser = new CollectionParser(objectMapper, LabWorkList.class);
        String json = parser.fromObjectToString(labWorkList);
        System.out.println(json);
        LabWorkList newObject = parser.fromStringToObject(json);
        System.out.println("Test finished");
    }

    @Test
    public void labWorkSerialization(){
        LabWork labWork = createElement();
        CollectionParser parser = new CollectionParser(objectMapper, LabWorkList.class);
        String json = parser.fromElementToString(labWork);
        System.out.println(json);
        LabWork newLabWork = parser.fromStringToElement(json);
        System.out.println("LabWork test finished");
    }

    private LabWork createElement(){
        String name = "7th lab";
        Coordinates coordinates = new Coordinates(2, 3);
        float minimalPoint = 80;
        String description = "I love my rat - Hory, but because of lab I have no time for her!";
        Integer averagePoint = 60;
        Difficulty difficulty = Difficulty.HOPELESS;
        Discipline discipline = new Discipline("Programming", 35, 65, 1000000);
       return new LabWork(name, coordinates, minimalPoint, description, averagePoint, difficulty, discipline);
    }

}
