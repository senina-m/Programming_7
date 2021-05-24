package ru.senina.itmo.lab7.labwork;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;
import ru.senina.itmo.lab7.InvalidArgumentsException;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Objects;

@Entity
public class Coordinates implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "generator_coordinates")
    @SequenceGenerator(name = "generator_coordinates", sequenceName = "seq_coordinates", allocationSize = 1)
    @Column(name = "coordinates_id")
    private long id;

    @Getter
    @Column(name = "coordinates_x")
    private int x; //Максимальное значение поля: 74

    @Getter
    @Column(name = "coordinates_y")
    private long y; //Значение поля должно быть больше -47

    @Setter
    @OneToOne
    @MapsId
    @JoinColumn(name = "coordinates_id")
    @JsonIgnore
    private LabWork labWork;

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

    public void setX(int x) throws InvalidArgumentsException{
        if(x <= 74){
            this.x = x;
        }else {
            throw new InvalidArgumentsException("Coordinates value is wrong.");
        }
    }

    public void setY(long y) throws InvalidArgumentsException{
        if(y >= -47){
            this.y = y;
        }else {
            throw new InvalidArgumentsException("Coordinates value is wrong.");
        }
    }
}
