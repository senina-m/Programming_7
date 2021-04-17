package ru.senina.itmo.lab7;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeoutException;
import java.util.logging.Level;

public class ReadingTask implements Runnable {
    private final ServerNetConnector net;
    private final ExecutorService processThreads;
    private final ProcessingTask processingTask;

    public ReadingTask(ServerNetConnector net, ExecutorService processThreads, ProcessingTask processingTask) {
        this.net = net;
        this.processThreads = processThreads;
        this.processingTask = processingTask;
    }

    @Override
    public void run() {
        try {
            final String command = net.nextCommand(3);
            processingTask.setCommand(command);
            processThreads.execute(processingTask);
        } catch (TimeoutException e) {
            Logging.log(Level.WARNING, "Amount of attempts to read next command is out.");
        }
    }

}
