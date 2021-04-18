package ru.senina.itmo.lab7;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;

public class ServerKeeper {
    private boolean isStopped = false;

    public ServerKeeper() {
    }

    public void start(int serverPort) {
        Logging.log(Level.INFO, "Keeper was started.");
        ExecutorService readThreads = Executors.newCachedThreadPool();
        ExecutorService processThreads = Executors.newCachedThreadPool();
        Controller controller = new Controller(new Model());
        while (!isStopped) { // TODO: HOW TO STOP?
            final ServerNetConnector net = new ServerNetConnector();
            SendingTask sendingTask = new SendingTask(net);
            ProcessingTask processingTask = new ProcessingTask(controller, sendingTask);
            ReadingTask readingTask = new ReadingTask(net, processThreads, processingTask);
            if (net.startConnection(serverPort)) {
                readThreads.execute(readingTask);
            }
            try {
                readThreads.shutdown();
                readThreads.awaitTermination(3, TimeUnit.SECONDS);
                processThreads.shutdown();
                processThreads.awaitTermination(3, TimeUnit.SECONDS);
            } catch (InterruptedException e) {
                Logging.log(Level.WARNING, "Termination error " + e.toString());
            }
        }
    }

    public void stop() {
        isStopped = true;
    }
}
