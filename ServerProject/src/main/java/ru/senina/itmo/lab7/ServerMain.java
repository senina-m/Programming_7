package ru.senina.itmo.lab7;

import java.util.logging.Level;

/**
 * @author Senina Mariya
 * Main class of programm to start app.
 */
public class ServerMain {

    public static void main(String[] args) {
        try {
            ServerKeeper serverKeeper = new ServerKeeper();
            serverKeeper.start(Integer.parseInt(args[0]));
        } catch (NumberFormatException e) {
            Logging.log(Level.WARNING, "Incorrect port given to start! It has to be int number!\nSet port in arguments line!");
            System.exit(0);
        } catch (IndexOutOfBoundsException e) {
            Logging.log(Level.WARNING, "No port given to start!!! Set port in arguments line!");
            System.exit(0);
        }
    }
}
