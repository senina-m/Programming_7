package ru.senina.itmo.lab7.testClasses;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;



@Getter @Setter
public class StudentCoordinates implements Serializable {
    private static final long serialVersionUID = 187894578254L;
    private Student id;
    private int x;
    private int y;

    public StudentCoordinates(int x, int y) {
        this.x = x;
        this.y = y;
    }

    @Override
    public String toString() {
        return "Coordinates{" + "x =" + x + ", y =" + y + '}';
    }
}
