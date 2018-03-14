package juja.sqlcmd.view;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Scanner;

public class Console implements View {

    private Scanner scanner;
    private OutputStream outputStream;

    public Console(OutputStream printStream, InputStream inputStream) {
        this.outputStream = printStream;
        this.scanner = new Scanner(inputStream);
    }

    @Override
    public void write(String message) {
        try {
            outputStream.write((message + System.lineSeparator()).getBytes());
        } catch (IOException e) {
            System.err.println("Error. " + e.getMessage());
        }
    }

    @Override
    public String read() {
        return scanner.nextLine();
    }
}