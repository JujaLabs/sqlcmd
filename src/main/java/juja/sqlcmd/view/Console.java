package juja.sqlcmd.view;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;

public class Console implements View {
    private InputStream inputStream;
    private OutputStream outputStream;

    public Console() {
        this(System.in, System.out);
    }

    public Console(InputStream inputStream, OutputStream outputStream) {
        this.outputStream = outputStream;
        this.inputStream = inputStream;
    }

    @Override
    public void write(String message) {
        try {
            message += "\n";
            outputStream.write(message.getBytes());
        } catch (IOException e) {
            throw new RuntimeException("Something goes wrong with write method... Reason:" + e.getMessage());
        }
    }

    @Override
    public String read() {
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        try {
            return reader.readLine();
        } catch (IOException e) {
            throw new RuntimeException("Something goes wrong with read method... Reason:" + e.getMessage());
        }
    }
}