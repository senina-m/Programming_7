package ru.senina.itmo.lab7.JPA;


import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity
@Getter @Setter
public class Student {
//TODO: если поставить id над геттером то JPA будет читать из методов. Оно надо?
    @Id
    @GeneratedValue
    private Long id;

    private String name;

    public Student() {
    }
}
