package ru.senina.itmo.lab7;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Objects;
import java.util.logging.*;

public class ClientLog {
    static Logger logger;
    public Handler fileHandler;

    private ClientLog() throws IOException {
        String path = Objects.requireNonNull(ClientLog.class.getClassLoader()
                .getResource("logging.properties"))
                .getFile();
        System.setProperty("java.util.logging.config.file", path);
        logger = Logger.getLogger(ClientLog.class.getName());

        String logFile = "ClientLogs.txt";
        File f = new File(logFile);
        f.createNewFile();
        PrintWriter writer = new PrintWriter(f);
        writer.print("");
        writer.close();

        fileHandler = new FileHandler(logFile, true);
        fileHandler.setFormatter( new SimpleFormatter());
        logger.addHandler(fileHandler);
    }

    private static Logger getLogger() {
        if (logger == null) {
            try {
                new ClientLog();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return logger;
    }

    public static void log(Level level, String msg) {
        getLogger().log(level, msg);
//        System.out.println(msg);
    }
}