package ru.swamptea.javaChat;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class MyConnection implements Runnable {

    private final Server server;
    private final Socket clientSocket;
    private final PrintWriter out;
    private final BufferedReader in;

    public MyConnection(Server server, Socket socket) throws IOException {
        this.server = server;
        this.clientSocket = socket;
        out = new PrintWriter(socket.getOutputStream());
        in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
    }

    @Override
    public void run() {
        try {
            while (!Thread.currentThread().isInterrupted()) {
                // Если от клиента пришло сообщение, отправляем его всем клиентам
                String msg = in.readLine();
                if (msg != null && !msg.equals("")) {
                    server.onReceiveString(this, msg);
                }
            }
        }
        catch (IOException e) {
            server.onException(this, e);
        } finally {
            disconnect();
        }
    }

    public synchronized void sendMessage(String msg) {
        //отправить сообщение
        try {
            out.println(msg);
            out.flush();
        } catch (Exception e) {
            server.onException(this, e);
        }
    }

    public synchronized void disconnect() {
        Thread.currentThread().interrupt();
        server.onDisconnect(this);
        try {
            in.close();
            out.close();
            clientSocket.close();
        } catch (IOException e) {
            server.onException(this, e);
        }
    }

    @Override
    public String toString() {
        return "Connection: " + clientSocket.getInetAddress() + ": " + clientSocket.getPort();
    }
}