package ru.senina.itmo.lab7.labwork;

import ru.senina.itmo.lab7.InvalidArgumentsException;

import java.util.Objects;

/**
 * The class-field in LabWork class
 */
public class Discipline {
    private String name; //Поле не может быть null, Строка не может быть пустой
    private long lectureHours;
    private Integer practiceHours; //Поле может быть null
    private int selfStudyHours;

    public Discipline() {
    }

    /**
     * @param name name of subject
     * @param lectureHours lecture hours of subject
     * @param practiceHours practice hours of subject
     * @param selfStudyHours selfstudy hours of subject
     */
    public Discipline(String name, long lectureHours, Integer practiceHours, int selfStudyHours) {
        this.name = name;
        this.lectureHours = lectureHours;
        this.practiceHours = practiceHours;
        this.selfStudyHours = selfStudyHours;
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

    public String getName() {
        return name;
    }

    public void setName(String name) throws InvalidArgumentsException {
        if (name != null && name.length() != 0) {
            this.name = name;
        } else {
            throw new InvalidArgumentsException("Discipline's name can't be null or empty line.");
        }
    }

    public long getLectureHours() {
        return lectureHours;
    }

    public void setLectureHours(long lectureHours) {
        this.lectureHours = lectureHours;
    }

    public Integer getPracticeHours() {
        return practiceHours;
    }

    public void setPracticeHours(Integer practiceHours) {
        this.practiceHours = practiceHours;
    }

    public int getSelfStudyHours() {
        return selfStudyHours;
    }

    public void setSelfStudyHours(int selfStudyHours) {
        this.selfStudyHours = selfStudyHours;
    }
}