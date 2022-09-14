package ru.swamptea.javaChat;

import java.io.*;
import java.net.Socket;

public class Client implements Runnable {

    private static String ipAddress;
    private static int port;
    private PrintWriter out;
    private BufferedReader in;
    private BufferedReader systemIn;
    private Socket clientSocket;
    private Logger logger;
    private String clientName = "";


    public Client() {
        //инициализируем поля, принимаем сообщение от сервера, отправляем ответ
        logger = new Logger("chatClient/log.txt");
        try (BufferedReader reader = new BufferedReader(new FileReader("chatClient/settings.txt"))) {
            port = Integer.parseInt(reader.readLine().split(": ")[1]);
            ipAddress = reader.readLine().split(": ")[1];
            clientSocket = new Socket(ipAddress, port);
            out = new PrintWriter(clientSocket.getOutputStream());
            in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            systemIn = new BufferedReader(new InputStreamReader(System.in));
            printStr("Connection ready... ");
            printStr(in.readLine());
            clientName = systemIn.readLine();
            out.println("Client connected: " + clientName);
            out.flush();
            new Thread(this).start();
        } catch (IOException e) {
            printStr("TCPConnectionException " + e);
        }
    }

    public static void main(String[] args) {
        Client client = new Client();
    }

    private void printStr(String msg) {
        logger.log(msg);
        System.out.println(msg);
    }

    @Override
    public void run() {
        new Thread(() -> {
            try {
                // ожидаем, принимаем и печатаем сообщения
                while (true) {
                    String msg = in.readLine();
                    printStr(msg);
                }
            } catch (Exception e) {
            }
        }).start();

        new Thread(() -> {
            try {
                // отправляем сообщения на сервер
                while (true) {
                    String msg = systemIn.readLine();
                    if (msg != null && !msg.equals("")) {
                        sendMessageToServer(msg);
                    }
                }
            } catch (IOException e) {
                logger.log("TCPConnectionException " + e);
            }

        }).start();
    }

    public void sendMessageToServer(String msg) throws IOException {
        if (msg.equals("/exit")) {
            Thread.currentThread().interrupt();
            printStr("Connection close");
            out.println("Client disconnected: " + clientName);
            out.flush();
            clientSocket.close();
            System.exit(0);
        } else {
            out.println(clientName + ": " + msg);
            out.flush();
        }
    }
}