package ru.senina.itmo.lab7;

import org.apache.commons.codec.digest.DigestUtils;
import org.hibernate.engine.jdbc.spi.SqlExceptionHelper;
import ru.senina.itmo.lab7.labwork.LabWork;

import javax.persistence.*;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;
import java.util.Properties;
import java.util.Random;
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

    public static void addElement(LabWork labWork, String token) {
        //todo: no such elements before (or i can add more?)
        assert entityManagerFactory != null; //Прикольная штука надо про неё прочитать и научиться пользоваться
        EntityManager manager = entityManagerFactory.createEntityManager();
        EntityTransaction transaction = null;
        try {
            transaction = manager.getTransaction();
            transaction.begin();

            labWork.setOwner(manager.createQuery("SELECT owner FROM Owner owner WHERE owner.token =" + token, Owner.class).getSingleResult());
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

    public static void removeById(long id, String token) {
        assert entityManagerFactory != null;
        EntityManager manager = entityManagerFactory.createEntityManager();
        EntityTransaction transaction = null;

        try {
            transaction = manager.getTransaction();
            transaction.begin();
            LabWork element = manager.find(LabWork.class, id);
            if (element.getOwner().getToken().equals(token)) {
                manager.remove(element);
            }
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

    public static void updateById(LabWork labWork, long id, String token) {
        assert entityManagerFactory != null;
        EntityManager manager = entityManagerFactory.createEntityManager();
        EntityTransaction transaction = null;

        try {
            transaction = manager.getTransaction();
            transaction.begin();

            LabWork element = manager.find(LabWork.class, id);
            if (element.getOwner().getToken().equals(token)) {
                element.copyElement(labWork); //TODO: check that Coordinates and Discipline copied correctly.
                manager.persist(element);
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

    public static List<LabWork> getSortedList(){
        List<LabWork> elements = null;
        assert entityManagerFactory != null;
        EntityManager manager = entityManagerFactory.createEntityManager();
        EntityTransaction transaction = null;

        try {
            transaction = manager.getTransaction();
            transaction.begin();
            elements = manager.createQuery("SELECT labwork FROM LabWork labwork ORDER BY labwork.id", LabWork.class).getResultList();
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

    public static void clear(String token) {
        assert entityManagerFactory != null;
        EntityManager manager = entityManagerFactory.createEntityManager();
        EntityTransaction transaction = null;

        try {
            transaction = manager.getTransaction();
            transaction.begin();

            Owner owner = manager.createQuery("SELECT owner FROM Owner owner WHERE owner.token =" + token, Owner.class).getSingleResult();
            owner.getLabWork().clear();

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

    public static void removeGreater(LabWork labWork, String token){
        assert entityManagerFactory != null;
        EntityManager manager = entityManagerFactory.createEntityManager();
        EntityTransaction transaction = null;

        try {
            transaction = manager.getTransaction();
            transaction.begin();
            //TODO: Дорогая, пахнет SQL иньекцией :)
            Owner owner = manager.createQuery("SELECT owner FROM Owner owner WHERE owner.token =" + token, Owner.class).getSingleResult();
            for(LabWork element : owner.getLabWork()){
                if (element.compareById(labWork) < 0) {
                    manager.remove(element);
                }
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
            num =  manager.createQuery("SELECT COUNT(id) FROM LabWork").getFirstResult();
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

    public static void removeAtIndex(int index, String token){
        //TODO: test to this command
        assert entityManagerFactory != null;
        EntityManager manager = entityManagerFactory.createEntityManager();
        EntityTransaction transaction = null;

        try {
            transaction = manager.getTransaction();
            transaction.begin();
            LabWork element = manager.createQuery("SELECT labwork FROM LabWork labwork LIMIT 1 OFFSET " + index, LabWork.class).getSingleResult();
            if(element.getOwner().getToken().equals(token)){
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

    /**
     * Get Login and Password create new User in DB.
     * Generate and return token
     */
    public static String register(String login, String password) throws UserAlreadyExistsException{
        assert entityManagerFactory != null;
        EntityManager manager = entityManagerFactory.createEntityManager();
        EntityTransaction transaction = null;
        String token;

        try {
            transaction = manager.getTransaction();
            transaction.begin();

            Owner user = new Owner();
            user.setLogin(login);
            user.setPassword(encryptPassword(password));
            token = generateToken();
            user.setToken(token);
            manager.persist(user);

            transaction.commit();
        } catch (EntityExistsException | RollbackException ex) {
            //fixme read more about RollbackException
            throw new UserAlreadyExistsException();
        } catch (Exception ex) {
//            System.out.println(ex.getClass().toString());
            ex.printStackTrace();
            if (transaction != null) {
                transaction.rollback();
            }
        } finally {
            manager.close();
        }
        return null;
    }


    /**
     * Check if user with such token exist
     * Return true if he exist
     */
    public static boolean checkLogin(String token) throws UnLoginUserException {
        assert entityManagerFactory != null;
        EntityManager manager = entityManagerFactory.createEntityManager();
        EntityTransaction transaction = null;
        boolean result = false;
        try {
            transaction = manager.getTransaction();
            transaction.begin();
            Owner user = manager.createQuery("SELECT owner FROM Owner owner WHERE owner.token =" + token, Owner.class).getSingleResult();
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


    /**
     * Get Login and Password check if such exist.
     * Generate and return new token.
     */
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
            if(user.getPassword().equals(encryptPassword(password))){
                token = generateToken();
                user.setToken(token);
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
        return token;
    }

    private static String generateToken(){

        byte[] array = new byte[50];
        new Random().nextBytes(array);
        return new String(array, StandardCharsets.UTF_8);
    }

    private static String encryptPassword(String password){
        String pepper = "kjh34kjhg*()&$2";
        return DigestUtils.md5Hex(password + pepper);
    }

}
