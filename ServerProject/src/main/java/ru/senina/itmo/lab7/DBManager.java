package ru.senina.itmo.lab7;

import ru.senina.itmo.lab7.labwork.LabWork;

import javax.persistence.*;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;
import java.util.Properties;
import java.util.logging.Level;

public class DBManager {
    private static final EntityManagerFactory entityManagerFactory = setEntityManagerFactory();

    public static EntityManagerFactory setEntityManagerFactory() {
        String path = Optional.ofNullable(System.getenv("DB_properties")).orElseThrow(() -> new IllegalArgumentException("Invalid  DB_properties variable"));
        try (final InputStream jpaFileInput = Files.newInputStream(Paths.get(path))) {
            final Properties properties = new Properties();
            properties.load(jpaFileInput);
            return Persistence.createEntityManagerFactory("MyJPAModel", properties);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void finish() {
        assert entityManagerFactory != null;
        entityManagerFactory.close();
    }

    public static void addElement(LabWork labWork, String login) {
        //todo: no such elements before (or i can add more?)
        assert entityManagerFactory != null; //Прикольная штука надо про неё прочитать и научиться пользоваться
        EntityManager manager = entityManagerFactory.createEntityManager();
        EntityTransaction transaction = null;
        try {
            transaction = manager.getTransaction();
            transaction.begin();
            labWork.setOwner(manager.find(Owner.class, login));
            manager.persist(labWork);
            transaction.commit();
        } catch (Exception ex) {
            if (transaction != null) {
                transaction.rollback();
            }
            //TODO: check already exists exception
            Logging.log(Level.WARNING, "There were some exceptions during adding Element. " + ex.getMessage());
        } finally {
            manager.close();
        }
    }

    public static List<LabWork> readAll() {

        List<LabWork> elements = null;

        assert entityManagerFactory != null;
        EntityManager manager = entityManagerFactory.createEntityManager();
        EntityTransaction transaction = null;

        try {
            transaction = manager.getTransaction();
            transaction.begin();
            elements = manager.createQuery("SELECT labwork FROM LabWork labwork", LabWork.class).getResultList();
            transaction.commit();
        } catch (Exception ex) {
            if (transaction != null) {
                transaction.rollback();
            }
            //TODO: check exceptions
            Logging.log(Level.WARNING, "There were some exceptions during readingAllElements Element. " + ex.getMessage());
        } finally {
            manager.close();
        }
        return elements;
    }

    public static void removeById(long id) {
        assert entityManagerFactory != null;
        EntityManager manager = entityManagerFactory.createEntityManager();
        EntityTransaction transaction = null;

        try {
            transaction = manager.getTransaction();
            transaction.begin();
            LabWork element = manager.find(LabWork.class, id);
            manager.remove(element);
            transaction.commit();
        } catch (Exception ex) {
            if (transaction != null) {
                transaction.rollback();
            }
            //TODO: check exceptions (if there was no such element?
            Logging.log(Level.WARNING, "There were some exceptions during removing element by id. " + ex.getMessage());
        } finally {
            manager.close();
        }
    }

    public static void updateById(LabWork labWork, long id) {
        assert entityManagerFactory != null;
        EntityManager manager = entityManagerFactory.createEntityManager();
        EntityTransaction transaction = null;

        try {
            transaction = manager.getTransaction();
            transaction.begin();
            LabWork element = manager.find(LabWork.class, id);
            element.copyElement(labWork); //TODO: check that Coordinates and Discipline copied correctly.
            manager.persist(element);

            transaction.commit();
        } catch (Exception ex) {
            if (transaction != null) {
                transaction.rollback();
            }
            ex.printStackTrace();
        } finally {
            manager.close();
        }
    }

    public static List<LabWork> sort(){
        List<LabWork> elements = null;
        assert entityManagerFactory != null;
        EntityManager manager = entityManagerFactory.createEntityManager();
        EntityTransaction transaction = null;

        try {
            transaction = manager.getTransaction();
            transaction.begin();
            elements = manager.createQuery("SELECT labwork FROM LabWork labwork ORDER BY id", LabWork.class).getResultList();
            transaction.commit();
        } catch (Exception ex) {
            if (transaction != null) {
                transaction.rollback();
            }
            ex.printStackTrace();
        } finally {
            manager.close();
        }
        return elements;
    }

    public static void clear() {
        assert entityManagerFactory != null;
        EntityManager manager = entityManagerFactory.createEntityManager();
        EntityTransaction transaction = null;

        try {
            transaction = manager.getTransaction();
            transaction.begin();
            manager.createQuery("DELETE FROM LabWork" ).executeUpdate();
            transaction.commit();
        } catch (Exception ex) {
            if (transaction != null) {
                transaction.rollback();
            }
            ex.printStackTrace();
        } finally {
            manager.close();
        }
    }

    public static LabWork minByDifficulty(){
        LabWork element = null;
        assert entityManagerFactory != null;
        EntityManager manager = entityManagerFactory.createEntityManager();
        EntityTransaction transaction = null;

        try {
            transaction = manager.getTransaction();
            transaction.begin();
            element = Optional.ofNullable(manager.createQuery("SELECT min(difficultyIntValue) FROM LabWork ", LabWork.class).
                    getSingleResult()).orElseThrow(() -> new IllegalArgumentException("The value of minimal difficulty is null!!!"));
            transaction.commit();
        } catch (Exception ex) {
            if (transaction != null) {
                transaction.rollback();
            }
            ex.printStackTrace();
        } finally {
            manager.close();
        }
        return element;
    }

    public static List<LabWork> filterByDescription(String string){
        List<LabWork> elements = null;
        assert entityManagerFactory != null;
        EntityManager manager = entityManagerFactory.createEntityManager();
        EntityTransaction transaction = null;

        try {
            transaction = manager.getTransaction();
            transaction.begin();
            //TODO: Дорогая, пахнет SQL иньекцией :)
            elements = manager.createQuery("SELECT labwork FROM LabWork labwork WHERE labwork.description=" + string, LabWork.class).getResultList();
            transaction.commit();
        } catch (Exception ex) {
            if (transaction != null) {
                transaction.rollback();
            }
            ex.printStackTrace();
        } finally {
            manager.close();
        }
        return elements;
    }

    public static void removeGreater(LabWork labWork){
        List<LabWork> elements = null;
        int difficulty = labWork.getDifficultyIntValue();
        assert entityManagerFactory != null;
        EntityManager manager = entityManagerFactory.createEntityManager();
        EntityTransaction transaction = null;

        try {
            transaction = manager.getTransaction();
            transaction.begin();
            //TODO: Дорогая, пахнет SQL иньекцией :)
            elements = manager.createQuery("SELECT labwork FROM LabWork labwork WHERE labwork.difficultyIntValue>" + difficulty, LabWork.class).getResultList();
            for(LabWork element : elements){
                manager.remove(element);
            }
            transaction.commit();
        } catch (Exception ex) {
            if (transaction != null) {
                transaction.rollback();
            }
            ex.printStackTrace();
        } finally {
            manager.close();
        }
    }

    public static int countNumOfElements(){
        int num  = 0;
        assert entityManagerFactory != null;
        EntityManager manager = entityManagerFactory.createEntityManager();
        EntityTransaction transaction = null;

        try {
            transaction = manager.getTransaction();
            transaction.begin();
            num =  manager.createQuery("SELECT COUNT(*) FROM LabWork ").getFirstResult();
            transaction.commit();
        } catch (Exception ex) {
            if (transaction != null) {
                transaction.rollback();
            }
            ex.printStackTrace();
        } finally {
            manager.close();
        }
        return num;
    }

    public static void removeATIndex(int index){
        //TODO: test to this command
        assert entityManagerFactory != null;
        EntityManager manager = entityManagerFactory.createEntityManager();
        EntityTransaction transaction = null;

        try {
            transaction = manager.getTransaction();
            transaction.begin();
            manager.createQuery("SELECT labwork FROM LabWork labwork LIMIT 1 OFFSET " + index).getFirstResult();
            transaction.commit();
        } catch (Exception ex) {
            if (transaction != null) {
                transaction.rollback();
            }
            ex.printStackTrace();
        } finally {
            manager.close();
        }
    }

    public static String register(String login, String password) {
        //TODO: catch exception that user exist if no - return null
        assert entityManagerFactory != null;
        EntityManager manager = entityManagerFactory.createEntityManager();
        EntityTransaction transaction = null;
        String token = null;

        try {
            transaction = manager.getTransaction();
            transaction.begin();
            Owner user = new Owner();
            user.setLogin(login);
            user.setPassword(password);
            manager.persist(user);
            transaction.commit();
        } catch (Exception ex) {
            if (transaction != null) {
                transaction.rollback();
            }
            ex.printStackTrace();
        } finally {
            manager.close();
        }
        return null;
    }

    public static boolean checkLogin(String login, String token) throws UnLoginUserException {
        assert entityManagerFactory != null;
        EntityManager manager = entityManagerFactory.createEntityManager();
        EntityTransaction transaction = null;
        boolean result = false;
        try {
            transaction = manager.getTransaction();
            transaction.begin();
            Owner user = manager.find(Owner.class, login);
            result = token.equals(user.getToken());
            transaction.commit();
        } catch (Exception ex) {
            if (transaction != null) {
                transaction.rollback();
            }
            ex.printStackTrace();
        } finally {
            manager.close();
        }
        return result;
    }

    public static String refreshToken(String login, String password){
        //todo: check if no such user
        assert entityManagerFactory != null;
        EntityManager manager = entityManagerFactory.createEntityManager();
        EntityTransaction transaction = null;
        String token = null;

        try {
            transaction = manager.getTransaction();
            transaction.begin();
            Owner user = manager.find(Owner.class, login);
            if(user.getPassword().equals(password)){
                token = null;
            }
            transaction.commit();
        } catch (Exception ex) {
            if (transaction != null) {
                transaction.rollback();
            }
            ex.printStackTrace();
        } finally {
            manager.close();
        }
        return null;
    }

}
