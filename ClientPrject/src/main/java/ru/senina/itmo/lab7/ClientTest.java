package ru.senina.itmo.lab7;

public class ClientTest {
    public static void main(String[] args){
        ClientNetConnector net = new ClientNetConnector();
        net.startConnection("localhost", 8181);
        net.sendMessage("abcdefghijkmnlop");
        net.receiveMessage();
        net.stopConnection();
    }
}
