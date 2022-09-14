package ru.swamptea.javaChat;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mockito;

import java.io.*;
import java.net.Socket;

public class ClientTest {

    @Test
    void sendStringTest() throws IOException {
        String testString = "Test String";
        Socket socket = Mockito.mock(Socket.class);
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        ByteArrayInputStream in = Mockito.mock(ByteArrayInputStream.class);
        Mockito.when(socket.getInputStream()).thenReturn(in);
        Mockito.when(socket.getOutputStream()).thenReturn(out);

        Client client = new Client();
        client.setIn(new BufferedReader(new InputStreamReader(socket.getInputStream())));
        client.setOut(new PrintWriter(socket.getOutputStream()));
        client.sendMessageToServer(testString);
        Assertions.assertEquals(": " + testString + "\n", out.toString());
    }

    @Test
    void sendStringTestInExit() throws IOException {
        Socket socket = Mockito.mock(Socket.class);
        Logger logger = Mockito.mock(Logger.class);
        Mockito.doNothing().when(logger).log(Mockito.anyString());

        ByteArrayInputStream in = Mockito.mock(ByteArrayInputStream.class);
        Mockito.when(socket.getInputStream()).thenReturn(in);

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        Mockito.when(socket.getOutputStream()).thenReturn(out);

        BufferedReader mockReader = Mockito.mock(BufferedReader.class);

        Client client = new Client();
        client.setClientSocket(socket);
        client.setLogger(logger);
        client.setIn(new BufferedReader(new InputStreamReader(socket.getInputStream())));
        client.setOut(new PrintWriter(socket.getOutputStream()));
        client.setSystemIn(mockReader);
        client.sendMessageToServer("/exit");
        Assertions.assertEquals("Client disconnected: \n", out.toString());
    }
}
