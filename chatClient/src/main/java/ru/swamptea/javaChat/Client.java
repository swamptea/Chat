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
    }

    public void init() {
        //инициализируем поля, принимаем сообщение от сервера, отправляем ответ
        logger = new Logger("chatClient/log.txt");
        try (BufferedReader reader = new BufferedReader(new FileReader("chatClient/settings.txt"))) {
            port = Integer.parseInt(reader.readLine().split(": ")[1]);
            ipAddress = reader.readLine().split(": ")[1];
            reader.close();
            clientSocket = new Socket(ipAddress, port);
            out = new PrintWriter(clientSocket.getOutputStream());
            in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            systemIn = new BufferedReader(new InputStreamReader(System.in));
            printStr("Connection ready... ");
            printStr(in.readLine());
            clientName = systemIn.readLine();
        } catch (IOException e) {
            printStr("TCPConnectionException " + e);
        }
        out.println("Client connected: " + clientName);
        out.flush();
        new Thread(this).start();
    }

    public static void main(String[] args) {
        Client client = new Client();
        client.init();
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
                    if (msg != null && !msg.equals("")) {
                        printStr(msg);
                    }
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
            out.println("Client disconnected: " + clientName);
            out.flush();
            printStr("Connection close");

            in.close();
            out.close();
            systemIn.close();
            clientSocket.close();
        } else {
            out.println(clientName + ": " + msg);
            out.flush();
        }
    }

    public void setOut(PrintWriter out) {
        this.out = out;
    }

    public void setIn(BufferedReader in) {
        this.in = in;
    }

    public void setSystemIn(BufferedReader systemIn) {
        this.systemIn = systemIn;
    }

    public void setLogger(Logger logger) {
        this.logger = logger;
    }

    public void setClientSocket(Socket clientSocket) {
        this.clientSocket = clientSocket;
    }
}