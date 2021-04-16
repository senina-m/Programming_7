package ru.senina.itmo.lab7.labwork;

import ru.senina.itmo.lab7.InvalidArgumentsException;

import java.util.Objects;

public class Coordinates {
    private int x; //Максимальное значение поля: 74
    private long y; //Значение поля должно быть больше -47

    public Coordinates(int x, long y) throws InvalidArgumentsException {
        if(x <= 74 && y >= -47){
            this.x = x;
            this.y = y;
        }else {
            throw new InvalidArgumentsException("Coordinates value is wrong.");
        }
    }

    public Coordinates() {
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Coordinates that = (Coordinates) o;
        return x == that.x && y == that.y;
    }

    @Override
    public int hashCode() {
        return Objects.hash(x, y);
    }

    public int getX() {
        return x;
    }

    public void setX(int x) throws InvalidArgumentsException{
        if(x <= 74){
            this.x = x;
        }else {
            throw new InvalidArgumentsException("Coordinates value is wrong.");
        }
    }

    public long getY() {
        return y;
    }

    public void setY(long y) throws InvalidArgumentsException{
        if(y >= -47){
            this.y = y;
        }else {
            throw new InvalidArgumentsException("Coordinates value is wrong.");
        }
    }
}
