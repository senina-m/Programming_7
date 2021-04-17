package ru.senina.itmo.lab7;

public class SendingTask implements Runnable{
    private String response;
    private final ServerNetConnector net;

    public SendingTask(ServerNetConnector net) {
        this.net = net;
    }

    public void setSendResponse(String response) {
        this.response = response;
    }

    @Override
    public void run() {
            net.sendResponse(response);
    }
}
