package ru.senina.itmo.lab7.testClasses;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "testuser") @Getter @Setter
public class TestUser implements Serializable {

    @Id @Getter
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "generator_testuser")
    @SequenceGenerator(name = "generator_testuser", sequenceName = "seq_testuser", allocationSize = 1)
    private Long id;

    @OneToOne(mappedBy = "testUser", cascade = CascadeType.ALL)
    @PrimaryKeyJoinColumn
    private Address address;

    @Column(name = "name")
    private String name;

    @Column(name = "gender")
    @Enumerated(EnumType.STRING)
    private Gender gender;

}