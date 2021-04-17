package ru.senina.itmo.lab7;

import java.util.logging.Level;

public class ProcessingTask implements Runnable {
    private final Controller controller;
    private final SendingTask sendingTask;
    private String command;

    public ProcessingTask(Controller controller, SendingTask sendingTask) {
        this.controller = controller;
        this.sendingTask = sendingTask;
    }

    public void setCommand(String command) {
        this.command = command;
    }

    @Override
    public void run() {
        String response = controller.processCommand(command);
        sendingTask.setSendResponse(response);
        new Thread(sendingTask).start();
    }
}
