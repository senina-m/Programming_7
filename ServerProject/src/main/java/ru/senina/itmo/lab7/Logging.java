package ru.senina.itmo.lab7;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.logging.*;

public class Logging {
    static Logger logger;
    public Handler fileHandler;
    Formatter plainText;

    private Logging() throws IOException {
        logger = Logger.getLogger(Logging.class.getName());
        String logFile = "myLogServer.txt";
        File f = new File(logFile);
        f.createNewFile();
        PrintWriter writer = new PrintWriter(f);
        writer.print("");
        writer.close();
        fileHandler = new FileHandler(logFile, true);
        plainText = new SimpleFormatter();
        fileHandler.setFormatter(plainText);
        logger.addHandler(fileHandler);
    }

    private static Logger getLogger() {
        if (logger == null) {
            try {
                new Logging();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return logger;
    }

    public static void log(Level level, String msg) {
        getLogger().log(level, msg);
        System.out.println(msg);
    }
}