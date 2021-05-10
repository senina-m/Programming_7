package ru.senina.itmo.lab7.labwork;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;
import ru.senina.itmo.lab7.InvalidArgumentsException;

import javax.persistence.*;
import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

/**
 * The class-field in LabWork class
 */
@Entity @Getter @Setter
public class Discipline implements Serializable {

    @Id
    @Column(name = "discipline_name", nullable = false)
    private String name; //Поле не может быть null, Строка не может быть пустой

    @Column(name = "discipline_lectureHours", nullable = false)
    private long lectureHours;

    @Column(name = "discipline_practiceHours", nullable = false)
    private Integer practiceHours; //Поле может быть null

    @Column(name = "discipline_selfStudyHours", nullable = false)
    private int selfStudyHours;

    @JsonIgnore
    @OneToMany(mappedBy = "discipline", cascade = CascadeType.MERGE)
    private List<LabWork> labWork = new LinkedList<>();

    public Discipline() {
    }

    public Discipline(String name, long lectureHours, Integer practiceHours, int selfStudyHours) {
        this.name = name;
        this.lectureHours = lectureHours;
        this.practiceHours = practiceHours;
        this.selfStudyHours = selfStudyHours;
    }

    public void addLabWork(LabWork labWork){
        this.labWork.add(labWork);
    }

    public void setName(String name) throws InvalidArgumentsException {
        if (name != null && name.length() != 0) {
            this.name = name;
        } else {
            throw new InvalidArgumentsException("Discipline's name can't be null or empty line.");
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Discipline that = (Discipline) o;
        return lectureHours == that.lectureHours && selfStudyHours == that.selfStudyHours && name.equals(that.name) && practiceHours.equals(that.practiceHours);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, lectureHours, practiceHours, selfStudyHours);
    }

}