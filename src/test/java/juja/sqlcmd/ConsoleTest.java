package juja.sqlcmd;

import juja.sqlcmd.view.Console;
import juja.sqlcmd.view.View;
import org.junit.After;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.NoSuchElementException;

import static org.junit.Assert.assertEquals;

public class ConsoleTest {

    private static ByteArrayOutputStream out;
    private static View view;

    @BeforeClass
    public static void setup() {
        out = new ByteArrayOutputStream();
        view = new Console(System.out, System.in);
        System.setOut(new PrintStream(out));
    }

    @After
    public void tail() {
        out.reset();
    }

    @Test
    public void testWriteSomeText() {
        String message = "Test Line";
        view.write(message);
        String actual = new String(out.toByteArray());
        assertEquals(message + System.lineSeparator(), actual);
    }

    @Test
    public void testWriteWhenEmptyLine() {
        String message = "";
        view.write(message);
        String actual = new String(out.toByteArray());
        assertEquals(message + System.lineSeparator(), actual);
    }

    @Test
    public void testWriteWhenEmptyLine2() {
        view.write(null);
        String actual = new String(out.toByteArray());
        assertEquals(null + System.lineSeparator(), actual);
    }

    @Test
    public void testReadSomeText() {
        String message = "Test Line";
        view = new Console(out, new ByteArrayInputStream(message.getBytes()));
        assertEquals(message, view.read());
    }

    @Test(expected = NoSuchElementException.class)
    public void readWhenEmptyLineReturnsEmptyStringWithLineSeparator() {
        String message = "";
        view = new Console(out, new ByteArrayInputStream(message.getBytes()));
        view.read();
    }

    @Test
    public void testReadWriteSomeText() {
        String message = "Test Line";
        view = new Console(out, new ByteArrayInputStream(message.getBytes()));
        view.write(view.read());
        String actual = new String(out.toByteArray());
        assertEquals(message + System.lineSeparator(), actual);
    }

}
