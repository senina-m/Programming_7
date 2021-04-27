package ru.senina.itmo.lab7;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import ru.senina.itmo.lab7.labwork.LabWork;
import ru.senina.itmo.lab7.parser.JsonParser;
import ru.senina.itmo.lab7.parser.ParsingException;

import java.util.List;

public class CollectionParser extends JsonParser<LabWorkList> {

    public CollectionParser(ObjectMapper objectMapper, Class<LabWorkList> classT) {
        super(objectMapper, classT);
    }

    public String fromCollectionToStringElements(LabWorkList labWorkList) throws ParsingException {
        StringBuilder resultString = new StringBuilder();
        List<LabWork> list = labWorkList.getLabWorkList();
        for (int i = 0; i < list.size(); i++) {
            resultString.append("\nElement ").append(i + 1).append(":\n").append(fromElementToString(list.get(i)));
        }
        return resultString.toString();
    }

    public String fromElementToString(LabWork element) throws ParsingException {
        try {
            return CollectionParser.getObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(element);
        } catch (JsonProcessingException e) {
            throw new ParsingException("Something wrong with element id: " + element.getId());
        }
    }

    public LabWork fromStringToElement(String json) throws ParsingException {
        try {
            return CollectionParser.getObjectMapper().readValue(json, LabWork.class);
        } catch (JsonProcessingException e) {
            throw new ParsingException("Something wrong with element string -> object parsing.");
        }
    }

    @Override
    public String fromObjectToString(LabWorkList labWorkList) throws ParsingException {
        try {
            return CollectionParser.getObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(labWorkList);
        } catch (JsonProcessingException e) {
            throw new ParsingException("Something wrong with parsing collection to string.");
        }
    }
}
