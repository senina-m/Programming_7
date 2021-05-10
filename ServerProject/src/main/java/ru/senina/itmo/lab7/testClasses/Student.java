package ru.senina.itmo.lab7.testClasses;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;

@Entity @Getter @Setter
public class Student implements Serializable {
    private static final long serialVersionUID = 870974520375L;
    @Id
    @Column(name = "student_id", unique = true)
    private int id;

    @Column(name = "student_name", nullable = false)
    private String name;

    @Column(name = "student_age", nullable = false)
    private int age;

    @Column(name = "student_address", nullable = false)
    private StudentCoordinates address;

    public Student(int id, String name, int age, StudentCoordinates address) {
        this.id = id;
        this.name = name;
        this.age = age;
        this.address = address;
    }

    public Student() {

    }

    @Override
    public String toString() {
        return id + "\t" + name + "\t" + age + "\t" + address;
    }
}