package ru.senina.itmo.lab7;


import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.senina.itmo.lab7.testClasses.*;

import javax.persistence.*;
import javax.transaction.Transactional;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;
import java.util.Properties;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;


public class testJPA {
    //TODO: write tests for labWork

    @PersistenceContext(type = PersistenceContextType.TRANSACTION)
    private EntityManager entityManager;

    @BeforeEach
    public void setEntityManager() {
        String path = Optional.ofNullable(System.getenv("DB_properties")).orElseThrow(() -> new IllegalArgumentException("Invalid  DB_properties variable"));
        try (final InputStream jpaFileInput = Files.newInputStream(Paths.get(path))) {
            final Properties properties = new Properties();
            properties.load(jpaFileInput);
            EntityManagerFactory entityManagerFactory = Persistence.createEntityManagerFactory("MyJPAModel", properties);
            entityManager = Optional.ofNullable(entityManagerFactory.createEntityManager()).orElseThrow(()
                    -> new IllegalArgumentException("JPA properties are incorrect. (See login and password)"));
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @AfterEach
    public void close() {
        entityManager.getEntityManagerFactory().close();
        entityManager.close();
    }

    @Test
    public void shouldPersistStudent() {
        EntityTransaction transaction = null;

        try {
            transaction = entityManager.getTransaction();
            transaction.begin();
            Student student = new Student();
            student.setAddress(new StudentCoordinates(1, 2));
            student.setAge(18);
            student.setName("Masha");
            entityManager.persist(student);
            transaction.commit();
            deleteTable("Student");
        } catch (Exception ex) {
            if (transaction != null) {
                transaction.rollback();
            }
            ex.printStackTrace();
            deleteTable("Student");
        }
    }

    @Test
    public void OneToOneConnectivity(){
        EntityTransaction transaction = null;
        try {
            transaction = entityManager.getTransaction();
            transaction.begin();
            User user = new User();
            user.setName("Sava");
            user.setGender(Gender.RATTUS);
            Address address = new Address();
            address.setHouse(12);
            address.setStreet("ratland");

            user.setAddress(address);
            address.setUser(user);

            entityManager.persist(user);
//            entityManager.persist(address); It will be created automatically
            transaction.commit();

            assert1to1InsertedData();
            deleteTable("Address");
            deleteTable("User");
        } catch (Exception ex) {
            if (transaction != null) {
                transaction.rollback();
            }
            ex.printStackTrace();
            deleteTable("Address");
            deleteTable("User");
        }
    }

    private void assert1to1InsertedData() {
        Query query = entityManager.createQuery("FROM User");
        @SuppressWarnings("unchecked") List<User> userList = query.getResultList();

        assertNotNull(userList);
        assertEquals(1, userList.size());

        User user = userList.get(0);
        assertEquals("Sava", user.getName());
        assertEquals(Gender.RATTUS, user.getGender());

        Address address = user.getAddress();
        assertNotNull(address);
        assertEquals("ratland", address.getStreet());
        assertEquals(12 , address.getHouse());

    }


    public void deleteTable(String name) {
        EntityTransaction transaction = entityManager.getTransaction();
        transaction.begin();
        entityManager.createQuery("DELETE FROM " + name).executeUpdate();
        transaction.commit();
    }

    @Transactional
    public void executeDropTable(String tableName) {
        entityManager.createNativeQuery("DROP TABLE " + tableName).executeUpdate();
    }
}