package ru.senina.itmo.lab7;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.ByteBuffer;
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
            Logging.log(Level.INFO, "Connection was accepted.");
            out = new PrintWriter(clientSocket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            return true;
        } catch (IOException e){
            //TODO: обработать ошибки UnknownHostException отдельно?
//            Logging.log(Level.WARNING, "Exception during starting connection. " + e.getLocalizedMessage());
            return false;
//            throw new RuntimeException(e);
        }
    }

    public String nextCommand(int attempts) throws TimeoutException{
        String line = null;
        try {
            while(line == null) {
                line = in.readLine();
                attempts--;
                if(attempts<0){
                    throw new TimeoutException("Reading time is out");
                }
            }
        } catch (IOException e){
            Logging.log(Level.WARNING, "Exception during nextCommand. " + e.getLocalizedMessage());
            throw new RuntimeException(e);
        }
        Logging.log(Level.INFO, "Received message: '" + line + "'.");
        return line;
    }

    public void sendResponse(String str){
        byte[] bytes = str.getBytes();
        //4 bytes because int is 4 bytes
        out.println((bytes.length + 4) + str);
        Logging.log(Level.INFO, "Message '" + str + "' was send. Length " + bytes.length);
    }

    public void stopConnection() {
        try {
            in.close();
            out.close();
            clientSocket.close();
            serverSocket.close();
            Logging.log(Level.INFO, "Connection was closed.");
        } catch (IOException e){
            Logging.log(Level.WARNING, "Failed to stopConnection");
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
