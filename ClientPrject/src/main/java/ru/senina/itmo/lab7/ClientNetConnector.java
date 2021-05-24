package ru.senina.itmo.lab7;

import java.io.*;
import java.net.ConnectException;
import java.net.InetSocketAddress;
import java.net.SocketException;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.logging.Level;


public class ClientNetConnector {
    private SocketChannel socketChannel;
    private Selector selector;

    public void startConnection(String host, int serverPort) throws RefusedConnectionException {
        try {
            selector = Selector.open(); //Создаём новый селектор
            ClientLog.log(Level.INFO, "Selector is created!");
            socketChannel = SocketChannel.open(); // Открываем новый канал
            socketChannel.configureBlocking(false); //Устанавливаем канал в неблокирующий режим
            if (socketChannel.connect(new InetSocketAddress(host, serverPort))) { // Пытаемся установиться соединение с сервером
                System.out.println("DEBUG: Connected to server!");
                socketChannel.register(selector, SelectionKey.OP_WRITE | SelectionKey.OP_READ); //Этот канал мы будем использовать и на запись и на чтение - регистрируем
                return;
            }
            ClientLog.log(Level.INFO, "Starting to connect!");

            //Если сходу соединиться не удалось (соединение у нас происходит в неблокирующем режиме) - регистрируем канал на то, что мы хотим соединяться
            socketChannel.register(selector, SelectionKey.OP_CONNECT);
            while (true) {
                ClientLog.log(Level.INFO, "Before selector!");
                selector.select(); // Блокирующая операция проверки готов ли какой-то из предоставленных каналов с зарегистрированными ключами к чему готов
                //Если никто не готов - селект будет висеть и ждать, пока не появится, что обрабатывать
                ClientLog.log(Level.INFO, "Selector selected!");

                Iterator<SelectionKey> keys = selector.selectedKeys().iterator();//Все полученые ключи кладём в итератор
                while (keys.hasNext()) { //Бежим по ключам и проверяем, какждый
                    SelectionKey key = keys.next();
                    keys.remove();//удаляем из итератора ключ, чтобы если будут работать два обработчика они не путались
                    ClientLog.log(Level.INFO, "Key: " + key.readyOps());

                    //Проверяем есть ли ключ на соединение
                    if (key.isValid() && key.isConnectable()) {
                        // Если канал готов завершить соединение - завершаем соединение
                        // Мы начали процесс соединения .connect(inetSocketAddress) сразу он не завершился,
                        // значить теперь его нужно и можно завершить, раз мы получили ключ на соединение
                        SocketChannel channel = (SocketChannel) key.channel();
                        if (channel.isConnectionPending()) { //проверяем готовы ли мы завершить соединение
                            channel.finishConnect(); //завершаем соединение
                            ClientLog.log(Level.INFO, "Finished to connect! Client is connected to server!");
                            socketChannel.register(selector, SelectionKey.OP_WRITE | SelectionKey.OP_READ); //регистрируем канал на чтение и запись
                            return;
                        }
                        break;
                    }
                }
            }
        } catch (ConnectException e) {
            ClientLog.log(Level.SEVERE, "(Connect)EXCEPTION Server is not available! " + e);
            throw new RefusedConnectionException("Server is not available! " + e);


        } catch (IOException e) {
            ClientLog.log(Level.SEVERE, "(IO)EXCEPTION in connecting! " + e);
            throw new RefusedConnectionException("IOException while connecting. " + e);
        }
    }


    public void sendMessage(String msg) throws RefusedConnectionException {
        msg = msg + "\n";
        ClientLog.log(Level.INFO, "Sending of a message started!");
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
                        ClientLog.log(Level.INFO, "There is a key to write!");
                        SocketChannel channel = (SocketChannel) key.channel();
                        channel.write(outBuffer);
                        if (outBuffer.remaining() < 1) {
                            ClientLog.log(Level.INFO, "Writing is finished! Message:'" + msg + "' was sent!");
                            return;
                        }
                    }
                }
            }
        } catch (SocketException e) {
            ClientLog.log(Level.WARNING, "Server has disconnected! " + e.getLocalizedMessage());
            throw new RefusedConnectionException("Server has disconnected! " + e);
        } catch (IOException e) {
            ClientLog.log(Level.SEVERE, "Exception in sending a message " + e.getLocalizedMessage());
        }
    }

    /**
     * @return NULLABLE if there was no answer
     */
    public String receiveMessage() throws RefusedConnectionException {
//todo: timeout
        ClientLog.log(Level.INFO, "Reading started!");
        try {
            //TODO: sleep for while-true
            while (true) {
//                    System.out.println("DEBUG: select");
                selector.select();
                ByteBuffer buffer = ByteBuffer.allocate(1024);
                Set<SelectionKey> selectedKeys = selector.selectedKeys();
                Iterator<SelectionKey> iterator = selectedKeys.iterator();
                while (iterator.hasNext()) {
                    SelectionKey selectionKey = iterator.next();
//                        System.out.println("DEBUG: Key: " + selectionKey.readyOps());

                    if (selectionKey.isReadable()) {
                        ClientLog.log(Level.INFO, "Reading there was a key to read!");
                        SocketChannel channel = (SocketChannel) selectionKey.channel();

                        List<Byte> list = new LinkedList<>();
                        while (channel.read(buffer) != -1 || buffer.position() > 0) {
                            ((Buffer)buffer).flip();
//                                System.out.println("DEBUG: Buffer contents" + new String(buffer.array(), 0, buffer.position(), StandardCharsets.UTF_8));
//                                System.out.println("DEBUG: Buffer position: " + buffer.position() + "    Buffer limit: " + buffer.limit());
                            if (buffer.remaining() > 0) {
                                list.add(buffer.get());
                            }
                            buffer.compact();
                        }
                        String message = byteListToString(list).trim();

                        ClientLog.log(Level.INFO, "Reading is finished! Received message: '" + message.toString().trim() + "'.");
                        return message;
                    }
                    iterator.remove();
                }
            }
        } catch (SocketException e) {
            throw new RefusedConnectionException("Server has disconnected" + e);
        } catch (IOException e) {
            ClientLog.log(Level.SEVERE, "EXCEPTION in receiving message " + e.toString());
            throw new RuntimeException(e); //todo: check what such syntax do
        } catch (NumberFormatException e) {
            ClientLog.log(Level.SEVERE, "EXCEPTION in received message number of bytes in message was incorrect!");
            throw new InvalidArgumentsException("Number of bytes in received message was incorrect");
        }
    }

    public void stopConnection() {
        try {
            socketChannel.close();
            ClientLog.log(Level.WARNING, "Connection is closed!");
        } catch (IOException e) {
            ClientLog.log(Level.SEVERE, "Exception in closing connection " + e.getLocalizedMessage());

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
    }
}