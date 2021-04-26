package ru.senina.itmo.lab7.testClasses;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Table(name = "address") @Getter @Setter
public class Address {

    @Id
    @Column(name = "user_id")
    private Long id;

    @OneToOne
    @MapsId
    @JoinColumn(name = "user_id")
    private TestUser testUser;

    @Column(name = "user_street")
    private String street;

    @Column(name = "user_house")
    private int house;
}
