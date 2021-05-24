package ru.senina.itmo.lab7;

import java.util.logging.Level;

/**
 * @author Senina Mariya
 * Main class of programm to start app.
 */
public class ServerMain {

    public static void main(String[] args) {
        try {
            if(System.getenv("DB_properties") != null) {
                final ServerKeeper serverKeeper = new ServerKeeper();
                Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
                    @Override
                    public void run() {
                        serverKeeper.stop();
                        DBManager.finish();
                    }
                }));
                serverKeeper.start(Integer.parseInt(args[0]));
            }else{
                ServerLog.log(Level.WARNING, "There is no DB_properties filepath in environment variables. Set it and run again.");
                System.exit(0);
            }
        } catch (NumberFormatException e) {
            ServerLog.log(Level.WARNING, "Incorrect port given to start! It has to be int number!\nSet port in arguments line!");
            System.exit(0);
        } catch (IndexOutOfBoundsException e) {
            ServerLog.log(Level.WARNING, "No port given to start!!! Set port in arguments line!");
            System.exit(0);
        }
    }
}
