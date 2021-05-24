package ru.senina.itmo.lab7;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeoutException;
import java.util.logging.Level;

public class ServerNetConnector {
    private ServerSocket serverSocket;

    private Socket clientSocket;

    private PrintWriter out;
    private BufferedReader in;
    public boolean startConnection(int port){
        try {
            serverSocket = new ServerSocket(port);
            clientSocket = serverSocket.accept();
            out = new PrintWriter(clientSocket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            ServerLog.log(Level.INFO, "Connection was accepted.");
            return true;
        } catch (IOException e){
            //TODO: обработать ошибки UnknownHostException отдельно?
//            Logging.log(Level.WARNING, "Exception during starting connection. " + e.getLocalizedMessage());
            return false;
//            throw new RuntimeException(e);
        }
    }

    public String receiveMessage(int attempts) throws TimeoutException{
        String line = null;
        try {
            while(line == null) {
                ServerLog.log(Level.ALL, "!!!!!!!!!!!!!!!!I'm reading!!!!!!!!!!!!!!!!!");
                line = in.readLine();
                ServerLog.log(Level.ALL, "!!!!!!!!!!!!!!!!DONE!!!!!!!!!!!!!!!!!");
                attempts--;
                if(attempts<0){
                    throw new TimeoutException("Reading time is out");
                }
            }
        } catch (IOException e){
            ServerLog.log(Level.WARNING, "Exception during nextCommand. " + e.getLocalizedMessage());
            throw new RuntimeException(e);
        }
        ServerLog.log(Level.INFO, "Received message: '" + line + "'.");
        return line;
    }

    public void sendResponse(String str){
        byte[] bytes = str.getBytes(StandardCharsets.UTF_8);
        out.println(str);
        ServerLog.log(Level.INFO, "Message '" + str + "' was send. Length " + bytes.length);
    }

    public void stopConnection() {
        try {
            in.close();
            out.close();
            clientSocket.close();
            serverSocket.close();
            ServerLog.log(Level.INFO, "Connection was closed.");
        } catch (IOException e){
            ServerLog.log(Level.WARNING, "Failed to stopConnection");
        }
    }

    public boolean checkIfConnectionClosed(){
        return serverSocket.isClosed();
    }

    public Socket getClientSocket() {
        return clientSocket;
    }

    public void setClientSocket(Socket clientSocket) {
        this.clientSocket = clientSocket;
    }
}
