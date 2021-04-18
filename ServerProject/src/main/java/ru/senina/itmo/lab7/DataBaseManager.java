package ru.senina.itmo.lab7;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

public class DataBaseManager {
    EntityManagerFactory emf = Persistence.createEntityManagerFactory( "MyJPOModule" );
    EntityManager entityManager = emf.createEntityManager();
}
