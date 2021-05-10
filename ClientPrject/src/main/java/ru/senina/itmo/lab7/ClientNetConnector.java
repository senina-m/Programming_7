package ru.senina.itmo.lab7;


import java.io.*;
import java.net.ConnectException;
import java.net.InetSocketAddress;
import java.net.SocketException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;
import java.util.*;


public class ClientNetConnector {
    private SocketChannel socketChannel;
    private Selector selector;
    private final boolean debug = ClientMain.DEBUG;

    public void startConnection(String host, int serverPort) throws RefusedConnectionException{
        try {
            selector = Selector.open();
            if (debug) {
                System.out.println("DEBUG: Selector is created!");
            }
            socketChannel = SocketChannel.open();
            socketChannel.configureBlocking(false);
            socketChannel.register(selector, SelectionKey.OP_CONNECT | SelectionKey.OP_WRITE | SelectionKey.OP_READ);
            socketChannel.connect(new InetSocketAddress(host, serverPort));
            if (debug) {
                System.out.println("DEBUG: Starting to connect!");
            }
            while (true) {
                selector.select();
                Iterator<SelectionKey> keys = selector.selectedKeys().iterator();
                while (keys.hasNext()) {
                    SelectionKey key = keys.next();
                    keys.remove();
                    if (key.isValid() && key.isConnectable()) {
                        SocketChannel channel = (SocketChannel) key.channel();
                        if (channel.isConnectionPending()) {
                            channel.finishConnect();
                            if (debug) {
                                System.out.println("DEBUG: Finished to connect! Client is connected to server!");
                            }
                            return;
                        }
                        break;
                    }
                }
            }
        } catch (ConnectException e) {
            if (debug) {
                System.out.println("DEBUG: (Connect)EXCEPTION Server is not available! " + e);
            }
            throw new RefusedConnectionException("Server is not available! " + e);


        } catch (IOException e) {
            if (debug) {
                System.out.println("DEBUG: (IO)EXCEPTION in connecting! " + e);
            }
            throw new RefusedConnectionException("IOException while connecting. " + e);
        }
    }


    public void sendMessage(String msg) throws RefusedConnectionException{
        if (debug) {
            System.out.println("DEBUG: Sending of a message started!");
        }
        byte[] bytes = (msg + "\n").getBytes(StandardCharsets.UTF_8);
        try {
            ByteBuffer outBuffer = ByteBuffer.wrap(bytes);
            while (true) {
                selector.select();
                Iterator<SelectionKey> keys = selector.selectedKeys().iterator();
                while (keys.hasNext()) {
                    SelectionKey key = keys.next();
                    keys.remove();
                    if (key.isValid() && key.isWritable()) {
                        if (debug) {
                            System.out.println("DEBUG: There is a key to write!");
                        }
                        SocketChannel channel = (SocketChannel) key.channel();
                        channel.write(outBuffer);
                        if (outBuffer.remaining() < 1) {
                            if (debug) {
                                System.out.println("DEBUG: Writing is finished! Message:'" + msg + "' was sent!");
                            }
                            return;
                        }
                    }
                }
            }
        } catch (SocketException e){
            throw new RefusedConnectionException("Server has disconnected! " + e);
        } catch (IOException e) {
            if (debug) {
                System.out.println("DEBUG: Exception in sending a message " + e.getLocalizedMessage());
            }
        }
    }

    /**
     * @return NULLABLE if there was no answer
     */
    public String receiveMessage() throws RefusedConnectionException{

        if (debug) {
            System.out.println("DEBUG: Reading started!");
        }
        try {
            //TODO: sleep for while-true
            while (true) {
//                if (debug) {
//                    System.out.println("DEBUG: select");
//                }
                selector.select();
                ByteBuffer buffer = ByteBuffer.allocate(1024);
                Set<SelectionKey> selectedKeys = selector.selectedKeys();
                Iterator<SelectionKey> iterator = selectedKeys.iterator();
                while (iterator.hasNext()) {
                    SelectionKey selectionKey = iterator.next();
//                    if (debug) {
//                        System.out.println("DEBUG: Key: " + selectionKey.readyOps());
//                    }
                    if (selectionKey.isReadable()) {
                        if (debug) {
                            System.out.println("DEBUG: Reading there was a key to read!");
                        }
                        SocketChannel channel = (SocketChannel) selectionKey.channel();

                        List<Byte> list = new LinkedList<>();
                        while (channel.read(buffer) != -1 || buffer.position() > 0) {
                            buffer.flip();
//                            if(debug){
//                                System.out.println("DEBUG: Buffer contents" + new String(buffer.array(), 0, buffer.position(), StandardCharsets.UTF_8));
//                                System.out.println("DEBUG: Buffer position: " + buffer.position() + "    Buffer limit: " + buffer.limit());
//                            }
                            if(buffer.remaining() > 0) {
                                list.add(buffer.get());
                            }
                            buffer.compact();
                        }
                        String message = byteListToString(list).trim();

                        if (debug) {
                            System.out.println("DEBUG: Reading is finished! Received message: '" + message.toString().trim() + "'.");
                        }
                        return message;
                    }
                    iterator.remove();
                }
            }
        } catch (SocketException e){
            throw new RefusedConnectionException("Server has disconnected" + e);
        } catch (IOException e) {
            if (debug) {
                System.out.println("DEBUG: EXCEPTION in receiving message " + e.toString());
            }
            throw new RuntimeException(e); //todo: check what such syntax do
        } catch (NumberFormatException e) {
            if (debug) {
                System.out.println("DEBUG: EXCEPTION in received message number of bytes in message was incorrect!");
            }
            throw new InvalidArgumentsException("Number of bytes in received message was incorrect");
        }
    }

    public void stopConnection() {
        try {
            socketChannel.close();
            if (debug) {
                System.out.println("DEBUG: Connection is closed!");
            }
        } catch (IOException e) {
            if (debug) {
                System.out.println("DEBUG: Exception in closing connection " + e.getLocalizedMessage());
            }
        }
    }

    private static String byteListToString(List<Byte> list) {
        if (list == null) {
            return "";
        }
        byte[] array = new byte[list.size()];
        int i = 0;
        for (Byte current : list) {
            array[i] = current;
            i++;
        }
        return new String(array, StandardCharsets.UTF_8);
//        return new String(array);
    }
}