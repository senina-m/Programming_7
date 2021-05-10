package ru.senina.itmo.lab7.testClasses;


import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.senina.itmo.lab7.Owner;
import ru.senina.itmo.lab7.UserAlreadyExistsException;
import ru.senina.itmo.lab7.labwork.Coordinates;
import ru.senina.itmo.lab7.labwork.Difficulty;
import ru.senina.itmo.lab7.labwork.Discipline;
import ru.senina.itmo.lab7.labwork.LabWork;

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
    private static EntityManagerFactory entityManagerFactory;

    @BeforeEach
    public void setEntityManager() {
        String path = Optional.ofNullable(System.getenv("DB_properties")).orElseThrow(() -> new IllegalArgumentException("Invalid  DB_properties variable"));
        try (final InputStream jpaFileInput = Files.newInputStream(Paths.get(path))) {
            final Properties properties = new Properties();
            properties.load(jpaFileInput);
            entityManagerFactory = Persistence.createEntityManagerFactory("MyJPAModel", properties);
            Optional.ofNullable(entityManagerFactory.createEntityManager()).orElseThrow(()
                    -> new IllegalArgumentException("JPA properties are incorrect. (See login and password)"));
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @AfterAll
    public static void close() {
        entityManagerFactory.close();
    }

    @Test
    public void shouldPersistStudent() {
        assert entityManagerFactory != null;
        EntityManager entityManager = entityManagerFactory.createEntityManager();

        EntityTransaction transaction = null;

        try {
            transaction = entityManager.getTransaction();
            transaction.begin();
            Student student = new Student(1,"Masha",18, new StudentCoordinates(1, 2));
            entityManager.persist(student);
            transaction.commit();
            deleteTable("Student", entityManager);
        } catch (Exception ex) {
            if (transaction != null) {
                transaction.rollback();
            }
            ex.printStackTrace();
            deleteTable("Student", entityManager);
        }finally {
            entityManager.close();
        }
    }

    @Test
    public void OneToOneConnectivity(){
        assert entityManagerFactory != null;
        EntityManager entityManager = entityManagerFactory.createEntityManager();

        EntityTransaction transaction = null;
        try {
            transaction = entityManager.getTransaction();
            transaction.begin();

            TestUser user = new TestUser();
            user.setName("Sava");
            user.setGender(Gender.RATTUS);

            Address address = new Address();
            address.setHouse(12);
            address.setStreet("catLand");

            user.setAddress(address);
            address.setTestUser(user);

            entityManager.persist(user);
            address.setStreet("ratland");
//            entityManager.persist(address); It will be created automatically
            transaction.commit();

            assert1to1InsertedData();
        } catch (Exception ex) {
            if (transaction != null) {
                transaction.rollback();
            }
            ex.printStackTrace();
        }finally {
            deleteTable("Address", entityManager);
            deleteTable("TestUser", entityManager);
            entityManager.close();
        }
    }

    private void assert1to1InsertedData() {
        assert entityManagerFactory != null;
        EntityManager entityManager = entityManagerFactory.createEntityManager();

        Query query = entityManager.createQuery("SELECT users FROM TestUser users", TestUser.class);
        @SuppressWarnings("unchecked") List<TestUser> userList = query.getResultList();

        assertNotNull(userList);
        assertEquals(1, userList.size());

        TestUser user = userList.get(0);
        assertEquals("Sava", user.getName());
        assertEquals(Gender.RATTUS, user.getGender());

        Address address = user.getAddress();
        assertNotNull(address);
        assertEquals("ratland", address.getStreet());
        assertEquals(12 , address.getHouse());

        entityManager.close();

    }


    public void deleteTable(String name, EntityManager entityManager) {
        EntityTransaction transaction = entityManager.getTransaction();
        transaction.begin();
        entityManager.createQuery("DELETE FROM " + name).executeUpdate();
        transaction.commit();
    }

    @Transactional
    public void executeDropTable(String tableName, EntityManager entityManager) {
        entityManager.createNativeQuery("DROP TABLE " + tableName).executeUpdate();
    }

    @Test
    public void testOneToOne(){
        assert entityManagerFactory != null;
        EntityManager entityManager = entityManagerFactory.createEntityManager();

        String token = createOwner();
        //Достаём из базы Owner-a
        Owner owner = getOwnerByToken(entityManager, token);
        //Как бы получаем LabWork
        LabWork labWork = new LabWork("new lab", new Coordinates(60, 0), 2, "description", 1, Difficulty.NORMAL, new Discipline("Proga", 16, 32, 1000));
        //Добавляем одно в другое
        labWork.setOwner(owner);
        owner.addLabWork(labWork);
        //Добавляем в координаты Labwork
        labWork.getCoordinates().setLabWork(labWork);

        //Создаём новую discipline если её до этого не было
        Discipline dbDiscipline = createDiscipline(labWork.getDiscipline());
        dbDiscipline.addLabWork(labWork);
        labWork.setDiscipline(dbDiscipline);

        EntityTransaction transaction = null;
        try {
            transaction = entityManager.getTransaction();
            transaction.begin();

            entityManager.persist(labWork);

            transaction.commit();
            //todo: write assert to OneToOne InsertedData
        } catch (Exception ex) {
            if (transaction != null) {
                transaction.rollback();
            }
            ex.printStackTrace();
        }finally {
            deleteTable("Coordinates", entityManager);
            deleteTable("LabWork", entityManager);
            deleteTable("Discipline", entityManager);
            deleteTable("Owner", entityManager);
            entityManager.close();
        }
    }

    private Owner getOwnerByToken(EntityManager manager, String token) {
        Query query = manager.createQuery("SELECT owner FROM Owner owner WHERE owner.token=:token", Owner.class);
        query.setParameter("token", token);
        return (Owner) query.getSingleResult();
    }

    private String createOwner(){
        assert entityManagerFactory != null;
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        EntityTransaction transaction = null;
        String token = "PS*DaGy!3MtlDFH#tkuh1pcTyS31Bi*2w!dulJ5z@16t*RDG5$";

        try {
            transaction = entityManager.getTransaction();
            transaction.begin();

            Owner user = new Owner();
            user.setLogin("masha");
            user.setPassword("khlsfhjlkhjskdfha"); //means nothing here
            user.setToken(token);
            entityManager.merge(user);

            transaction.commit();
        } catch (EntityExistsException | RollbackException ex) {
            //fixme read more about RollbackException
            throw new UserAlreadyExistsException();
        } catch (Exception ex) {
            System.out.println(ex.getClass().toString());
            ex.printStackTrace();
            if (transaction != null) {
                transaction.rollback();
            }
        }finally {
            entityManager.close();
        }
        return token;
    }

    private Discipline createDiscipline(Discipline discipline){
        assert entityManagerFactory != null;
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        EntityTransaction transaction = null;

        try {
            transaction = entityManager.getTransaction();
            transaction.begin();

            try {
                Discipline dbDiscipline = entityManager.find(Discipline.class, discipline.getName());
                if(dbDiscipline == null){
                    entityManager.persist(discipline);
                }
            }catch (IllegalArgumentException e) {
                entityManager.persist(discipline);
            }

            transaction.commit();
        } catch (EntityExistsException | RollbackException ex) {
            //fixme read more about RollbackException
            throw new UserAlreadyExistsException();
        } catch (Exception ex) {
            System.out.println(ex.getClass().toString());
            ex.printStackTrace();
            if (transaction != null) {
                transaction.rollback();
            }
        }finally {
            entityManager.close();
        }

        return discipline;
    }
}