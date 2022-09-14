package ru.swamptea.javaChat;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Logger {
    private final SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
    private final String FILE_PATH;
    public Logger(String filePath){
        this.FILE_PATH = filePath;
    }

    public void log(String msg) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(FILE_PATH, true))) {
            writer.println();
            writer.write("[" + formatter.format(new Date()) + "] " + msg);
            writer.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}