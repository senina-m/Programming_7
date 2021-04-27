package ru.senina.itmo.lab7;


import org.junit.jupiter.api.Test;
import ru.senina.itmo.lab7.testClasses.StudentCoordinates;
import ru.senina.itmo.lab7.testClasses.Student;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;
import java.util.Properties;

public class TestDataBaseManager {
    private static final EntityManagerFactory entityManagerFactory = setEntityManagerFactory();

    public static EntityManagerFactory  setEntityManagerFactory() {
        String path = Optional.ofNullable( System.getenv("DB_properties")).orElseThrow(() -> new IllegalArgumentException("Invalid  DB_properties variable"));
        try (final InputStream jpaFileInput = Files.newInputStream(Paths.get(path))) {
            final Properties properties = new Properties();
            properties.load(jpaFileInput);
            return Persistence.createEntityManagerFactory("MyJPAModel", properties);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Test
    public void basicOperationsToDB() {
        // Create two Students
        create(1, "Alice", 22, new StudentCoordinates(2,4));
        create(2, "Bob", 20,  new StudentCoordinates(5,4));
        create(3, "Charlie", 25, new StudentCoordinates(93,10));

        // Update the age of Bob using the id
        update(2, "Bob", 25,  new StudentCoordinates(100,100));

        // Delete the Alice from database
        delete(1);

        // Print all the Students
        List<Student> students = readAll();
        if (students != null) {
            for (Student stu : students) {
                System.out.println(stu);
            }
        }

        // NEVER FORGET TO CLOSE THE ENTITY_MANAGER_FACTORY
        assert entityManagerFactory != null;
        entityManagerFactory.close();
    }

    public void create(int id, String name, int age, StudentCoordinates address) {
        // Create an EntityManager
        assert entityManagerFactory != null;
        EntityManager manager = entityManagerFactory.createEntityManager();
        EntityTransaction transaction = null;

        try {
            // Get a transaction
            transaction = manager.getTransaction();
            // Begin the transaction
            transaction.begin();

            // Create a new Student object
            Student stu = new Student();
            stu.setId(id);
            stu.setName(name);
            stu.setAge(age);
            stu.setAddress(address);

            // Save the student object
            manager.persist(stu);

            // Commit the transaction
            transaction.commit();
        } catch (Exception ex) {
            // If there are any exceptions, roll back the changes
            if (transaction != null) {
                transaction.rollback();
            }
            // Print the Exception
            ex.printStackTrace();
        } finally {
            // Close the EntityManager
            manager.close();
        }
    }

    public List<Student> readAll() {

        List<Student> students = null;

        // Create an EntityManager
        assert entityManagerFactory != null;
        EntityManager manager = entityManagerFactory.createEntityManager();
        EntityTransaction transaction = null;

        try {
            // Get a transaction
            transaction = manager.getTransaction();
            // Begin the transaction
            transaction.begin();

            // Get a List of Students
            students = manager.createQuery("SELECT s FROM Student s", Student.class).getResultList();

            // Commit the transaction
            transaction.commit();
        } catch (Exception ex) {
            // If there are any exceptions, roll back the changes
            if (transaction != null) {
                transaction.rollback();
            }
            // Print the Exception
            ex.printStackTrace();
        } finally {
            // Close the EntityManager
            manager.close();
        }
        return students;
    }

    public void delete(int id) {
        // Create an EntityManager
        assert entityManagerFactory != null;
        EntityManager manager = entityManagerFactory.createEntityManager();
        EntityTransaction transaction = null;

        try {
            // Get a transaction
            transaction = manager.getTransaction();
            // Begin the transaction
            transaction.begin();

            // Get the Student object
            Student stu = manager.find(Student.class, id);

            // Delete the student
            manager.remove(stu);

            // Commit the transaction
            transaction.commit();
        } catch (Exception ex) {
            // If there are any exceptions, roll back the changes
            if (transaction != null) {
                transaction.rollback();
            }
            // Print the Exception
            ex.printStackTrace();
        } finally {
            // Close the EntityManager
            manager.close();
        }
    }

    public void update(int id, String name, int age, StudentCoordinates address) {
        // Create an EntityManager
        assert entityManagerFactory != null;
        EntityManager manager = entityManagerFactory.createEntityManager();
        EntityTransaction transaction = null;

        try {
            // Get a transaction
            transaction = manager.getTransaction();
            // Begin the transaction
            transaction.begin();

            // Get the Student object
            Student stu = manager.find(Student.class, id);

            // Change the values
            stu.setName(name);
            stu.setAge(age);
            stu.setAddress(address);

            // Update the student
            manager.persist(stu);

            // Commit the transaction
            transaction.commit();
        } catch (Exception ex) {
            // If there are any exceptions, roll back the changes
            if (transaction != null) {
                transaction.rollback();
            }
            // Print the Exception
            ex.printStackTrace();
        } finally {
            // Close the EntityManager
            manager.close();
        }
    }
}
