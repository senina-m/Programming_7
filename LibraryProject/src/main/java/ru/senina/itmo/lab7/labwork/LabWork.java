package ru.senina.itmo.lab7.labwork;


import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import ru.senina.itmo.lab7.InvalidArgumentsException;
import ru.senina.itmo.lab7.Owner;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Objects;

/**
 * Class an element of collection
 */
@Entity
public class LabWork  implements Serializable {

    @CreationTimestamp @Setter @Getter
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private java.time.LocalDateTime creationDate = java.time.LocalDateTime.now();
    //Поле не может быть null, Значение этого поля должно генерироваться автоматически
    @Id @Getter
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "generator_labwork")
    @SequenceGenerator(name = "generator_labwork", sequenceName = "seq_labwork", allocationSize = 1)
    private final Long id = Math.abs((long) Objects.hash(creationDate)); //Поле не может быть null, Значение поля должно быть больше 0, Значение этого поля должно быть уникальным,
    // Значение этого поля должно генерироваться автоматически


    @Getter @Setter
    @Column(name="name", nullable = false)
    private String name; //Поле не может быть null, Строка не может быть пустой

    @Setter @Getter
    @OneToOne(mappedBy = "labWork", cascade = CascadeType.ALL)
    @PrimaryKeyJoinColumn
    @JoinColumn(name = "coordinates", nullable = false)
    private Coordinates coordinates; //Поле не может быть null

    @Getter
    @Column(name = "minimalPoint", nullable = false)
    private float minimalPoint; //Значение поля должно быть больше 0

    @Getter
    @Column(name = "description", nullable = false)
    private String description; //Поле не может быть null

    @Getter
    @Column(name = "averagePoint", nullable = false)
    private Integer averagePoint; //Поле не может быть null, Значение поля должно быть больше 0

    @Transient
    private Difficulty difficulty; //Поле может быть null
    @Basic @Getter
    @Column(name = "difficultyIntValue", nullable = false)
    private int difficultyIntValue;

    @Getter @Setter
    @OneToOne
    private Discipline discipline; //Поле не может быть null

    @ManyToOne()
    @Getter @Setter
    private Owner owner;

    public LabWork() {
    }

    public LabWork(String name, Coordinates coordinates, float minimalPoint, String description, Integer averagePoint, Difficulty difficulty, Discipline discipline) {
        this.name = name;
        this.coordinates = coordinates;
        this.minimalPoint = minimalPoint;
        this.description = description;
        this.averagePoint = averagePoint;
        this.difficulty = difficulty;
        this.discipline = discipline;
    }


    @PostLoad
    void fillTransient() {
        if (difficultyIntValue > 0) {
            this.difficulty = Difficulty.of(difficultyIntValue);
        }
    }

    @PrePersist
    void fillPersistent() {
        if (difficulty != null) {
            this.difficultyIntValue = difficulty.getValue();
        }
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

    public void setMinimalPoint(float minimalPoint) throws InvalidArgumentsException {
        if(minimalPoint > 0){
            this.minimalPoint = minimalPoint;
        }else {
            throw new InvalidArgumentsException("Minimal point can't be less then 0.");
        }
    }

    public void setDescription(String description) throws InvalidArgumentsException {
        if (description != null) {
            this.description = description;
        } else {
            throw new InvalidArgumentsException("Description can't be null.");
        }
    }

    public void setAveragePoint(Integer averagePoint) throws InvalidArgumentsException{
        if (averagePoint != null && averagePoint > 0) {
            this.averagePoint = averagePoint;
        } else {
            throw new InvalidArgumentsException("Average point can't be null or less then 0.");
        }
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

    public void setDifficulty(Difficulty difficulty) {
        this.difficulty = difficulty;
    }

    public void copyElement(LabWork labWork){
        this.name = labWork.name;
        this.coordinates = labWork.coordinates;
        this.averagePoint = labWork.averagePoint;
        this.minimalPoint = labWork.minimalPoint;
        this.description = labWork.description;
        this.difficulty = labWork.difficulty;
        this.discipline = labWork.discipline;
    }

    public long compareById(LabWork labWork) {
        return (this.getId() - labWork.getId());
    }
}