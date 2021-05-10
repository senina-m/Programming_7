package ru.senina.itmo.lab7;

import lombok.Getter;
import lombok.Setter;
import ru.senina.itmo.lab7.labwork.LabWork;

import javax.persistence.*;
import java.util.List;

@Entity
@Getter
@Setter
public class Owner {
    @Id
    @Column(name = "user_login", nullable = false)
    private String login;

    @Column(name = "user_password", nullable = false)
    private String password;

    @Column(name = "user_token")
    private String token;

    @OneToMany(mappedBy = "owner", cascade = CascadeType.MERGE)
// orphanRemoval = true - если мы хотим убивать все LabWork когда умер user
    private List<LabWork> labWork;

    public void addLabWork(LabWork labWork){
        this.labWork.add(labWork);
    }
}
