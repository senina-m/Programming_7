package ru.senina.itmo.lab7.labwork;


import com.fasterxml.jackson.annotation.JsonProperty;
import ru.senina.itmo.lab7.InvalidArgumentsException;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Class an element of collection
 */

public class LabWork {

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private final java.time.LocalDateTime creationDate = java.time.LocalDateTime.now();
    //Поле не может быть null, Значение этого поля должно генерироваться автоматически
    private final Long id = Math.abs((long) Objects.hash(creationDate)); //Поле не может быть null, Значение поля должно быть больше 0, Значение этого поля должно быть уникальным,
    // Значение этого поля должно генерироваться автоматически

    private String name; //Поле не может быть null, Строка не может быть пустой
    private Coordinates coordinates; //Поле не может быть null
    private float minimalPoint; //Значение поля должно быть больше 0
    private String description; //Поле не может быть null
    private Integer averagePoint; //Поле не может быть null, Значение поля должно быть больше 0
    private Difficulty difficulty; //Поле может быть null
    private Discipline discipline; //Поле не может быть null

    public LabWork() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    /**
     * @param name Name of lab
     * @param coordinates coordinates
     * @param minimalPoint minimal point you can get on lab
     * @param description description of the lab
     * @param averagePoint average point students get
     * @param difficulty how difficult is lab
     * @param discipline the lab subject
     */
    public LabWork(String name, Coordinates coordinates, float minimalPoint, String description, Integer averagePoint, Difficulty difficulty, Discipline discipline) {
        this.name = name;
        this.coordinates = coordinates;
        this.minimalPoint = minimalPoint;
        this.description = description;
        this.averagePoint = averagePoint;
        this.difficulty = difficulty;
        this.discipline = discipline;
    }

    public Long getId() {
        return id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        LabWork labWork = (LabWork) o;
        return Float.compare(labWork.minimalPoint, minimalPoint) == 0 && id.equals(labWork.id) && name.equals(labWork.name) && coordinates.equals(labWork.coordinates) && description.equals(labWork.description) && averagePoint.equals(labWork.averagePoint) && difficulty == labWork.difficulty && discipline.equals(labWork.discipline);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, coordinates, minimalPoint, description, averagePoint, difficulty, discipline);
    }

    public LocalDateTime getCreationDate() {
        return creationDate;
    }

    public Coordinates getCoordinates() {
        return coordinates;
    }

    public void setCreationDate(LocalDateTime date) {
    }

    public float getMinimalPoint() {
        return minimalPoint;
    }

    public void setMinimalPoint(float minimalPoint) throws InvalidArgumentsException {
        if(minimalPoint > 0){
            this.minimalPoint = minimalPoint;
        }else {
            throw new InvalidArgumentsException("Minimal point can't be less then 0.");
        }
    }

    public void setCoordinates(Coordinates coordinates){
        this.coordinates = coordinates;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) throws InvalidArgumentsException {
        if (description != null) {
            this.description = description;
        } else {
            throw new InvalidArgumentsException("Description can't be null.");
        }
    }

    public Integer getAveragePoint() {
        return averagePoint;
    }

    public void setAveragePoint(Integer averagePoint) throws InvalidArgumentsException{
        if (averagePoint != null && averagePoint > 0) {
            this.description = description;
        } else {
            throw new InvalidArgumentsException("Average point can't be null or less then 0.");
        }
        this.averagePoint = averagePoint;
    }

    public Difficulty getDifficulty() {
        return difficulty;
    }

    public void setDifficulty(Difficulty difficulty) {
        this.difficulty = difficulty;
    }

    public void setDifficulty(String str) throws InvalidArgumentsException{
        boolean rightString = false;
        for(Difficulty difficulty : Difficulty.values()){
            if(str.equals(difficulty.toString())){
                this.difficulty = difficulty;
                rightString = true;
            }
        }
        if(!rightString){
            throw new InvalidArgumentsException("There is now such value for difficulty.");
        }
    }

    public Discipline getDiscipline() {
        return discipline;
    }

    public void setDiscipline(Discipline discipline) {
        this.discipline = discipline;
    }

}