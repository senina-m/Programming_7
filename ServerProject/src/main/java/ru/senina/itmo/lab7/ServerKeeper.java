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
//        ServerLog.log(Level.INFO, "Keeper was started.");
//        ServerLog.log(Level.INFO, " Log test INFO");
//        ServerLog.log(Level.SEVERE, " Log test SEVERE");
//        ServerLog.log(Level.WARNING, " Log test WARNING");
        ExecutorService readThreads = Executors.newCachedThreadPool();
        ExecutorService processThreads = Executors.newCachedThreadPool();
        Controller controller = new Controller(new Model());
        while (!isStopped) { // TODO: HOW TO STOP?
            final ServerNetConnector net = new ServerNetConnector();
            SendingTask sendingTask = new SendingTask(net);
            ProcessingTask processingTask = new ProcessingTask(controller, sendingTask);
            ReadingTask readingTask = new ReadingTask(net, processThreads, processingTask);
            while (true) { //пока не появится готовый клиент  - проверяем
                if(net.startConnection(serverPort)){
                    break;
                }
                //fixme нужно ли тут усыплять поток на какое-то время, чтобы не грузить процессор?
            }
            readThreads.execute(readingTask);
            ServerLog.log(Level.INFO, "readThreads was executed for readingTask");
        }
        try {
            readThreads.shutdown();
            readThreads.awaitTermination(3, TimeUnit.SECONDS);
            processThreads.shutdown();
            processThreads.awaitTermination(3, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            ServerLog.log(Level.WARNING, "Termination error " + e.toString());
        }
    }

    public void stop() {
        isStopped = true;
    }
}
