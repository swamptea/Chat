package ru.swamptea.javaChat;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.Mockito;

import java.io.*;
import java.net.Socket;

public class MyConnectionTest {
    @Test
    void sendStringTest() throws IOException {
        String testString = "Test String";
        Socket socket = Mockito.mock(Socket.class);
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        ByteArrayInputStream in = Mockito.mock(ByteArrayInputStream.class);
        Mockito.when(socket.getInputStream()).thenReturn(in);
        Mockito.when(socket.getOutputStream()).thenReturn(out);
        Server server = Mockito.mock(Server.class);
        MyConnection connection = new MyConnection(server, socket);
        connection.sendMessage(testString);
        Assertions.assertEquals(testString + "\n", out.toString());
    }
}
