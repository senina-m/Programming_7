package ru.senina.itmo.lab7.JPA;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.junit.After;
import org.junit.Before;
import org.junit.jupiter.api.Test;

import javax.persistence.*;
import java.util.Optional;

public class testJPA {
    @PersistenceContext(type = PersistenceContextType.TRANSACTION)
    private EntityManager entityManager;

    @Before
    public void init() {
        EntityManagerFactory entityManagerFactory = Persistence.createEntityManagerFactory("sample");
        entityManager = Optional.ofNullable(entityManagerFactory.createEntityManager()).orElseThrow(()
                -> new IllegalArgumentException("JPA properties are incorrect. (See login and password)"));
    }

    @After
    public void close() {
        entityManager.getEntityManagerFactory().close();
        entityManager.close();
    }

    @Test
    public void shouldPersistCategory() {
        SessionFactory sessionFactory = new Configuration().configure().buildSessionFactory();
        Session session = sessionFactory.openSession();

//        EntityManagerFactory emf = Persistence.createEntityManagerFactory("sample");
//        em = Optional.ofNullable(emf.createEntityManager()).orElseThrow(()
//                -> new IllegalArgumentException("JPA properties are incorrect. (See login and password)"));
//        Student student = new Student();
//        student.setName("Masha");
//        entityManager.persist(student);
    }
}
