package ru.swamptea.javaChat;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.CopyOnWriteArrayList;

public class Server {

    ServerSocket serverSocket;
    Socket clientSocket;
    private final int PORT = 8989;

    private CopyOnWriteArrayList<MyConnection> connections = new CopyOnWriteArrayList<>();

    private Logger logger;

    private static final SimpleDateFormat formatter = new SimpleDateFormat("HH:mm:ss");

    public Server() throws IOException {
      // инициализируем поля, ждем сокет клиента
        logger = new Logger("chatServer/log.txt");
        this.serverSocket = new ServerSocket(PORT);
        logger.log("Server running...");
        while (true){
            this.clientSocket = serverSocket.accept();
            MyConnection connection = new MyConnection(this, clientSocket);
            onConnection(connection);
        }
    }

    public static void main(String[] args) throws IOException {
        Server server = new Server();
    }

    private void sendMessageToAllClients(String msg){
        // отправить сообщение всем клиентам
        logger.log(msg);
        for(MyConnection connection : connections){
            connection.sendMessage(msg);
        }
    }

    private void sendMessageToClient(MyConnection connection, String msg){
        // отправить сообщение конкретному клиенту
        logger.log("Server message to " + connection + ": " + msg);
        connection.sendMessage(msg);
    }

    public synchronized void onConnection(MyConnection connection) {
        // добавляем соединение в список, просим ввести ник
        connections.add(connection);
        logger.log("Client connected: " + connection);
        sendMessageToClient(connection, "ENTER YOUR NICKNAME");
        new Thread(connection).start();
    }

    public synchronized void onReceiveString(MyConnection connection, String msg) {
        // собрать строку, отправить ее всем клиентам
        sendMessageToAllClients(formatter.format(new Date()) + " " + msg);
        logger.log(connection + " " + msg);
    }

    public synchronized void onDisconnect(MyConnection connection){
        // удаляем соединение из списка
        logger.log("Client disconnected: " + connection);
        connections.remove(connection);
        sendMessageToAllClients("Client disconnected: " + connection);
    }

    public synchronized void onException(MyConnection connection, Exception e) {
        logger.log("TCPConnectionException " + e);
    }
}