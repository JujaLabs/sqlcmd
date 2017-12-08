package juja.sqlcmd;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.sql.SQLException;

import static org.junit.Assert.assertEquals;

public class ApplicationTest {

    private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
    private final ByteArrayOutputStream errContent = new ByteArrayOutputStream();

    private PrintStream originalOut;
    private PrintStream originalErr;

    @Before
    public void setUpStreams() {
        originalOut = System.out;
        originalErr = System.err;
        System.setOut(new PrintStream(outContent));
        System.setErr(new PrintStream(errContent));
    }

    @After
    public void cleanUpStreams() {
        System.setOut(originalOut);
        System.setErr(originalErr);
    }

    @Test
    public void out() throws SQLException {
        String lineSeparator = System.lineSeparator();
        String expected = "-------- PostgreSQL JDBC Connection  ------------" + lineSeparator +
                "PostgreSQL JDBC Driver Registered!" + lineSeparator +
                "Connection created" + lineSeparator +
                lineSeparator +
                "Existing Tables:" + lineSeparator +
                "db is empty" + lineSeparator +
                lineSeparator +
                "Existing Tables:" + lineSeparator +
                "-user" + lineSeparator +
                lineSeparator +
                "1 | user1 | password1" + lineSeparator +
                "2 | user2 | password2" + lineSeparator +
                "3 | user3 | password3" + lineSeparator +
                lineSeparator +
                "1 | user1 | password1" + lineSeparator +
                "3 | user3 | password3" + lineSeparator +
                "2 | userchange1 | password2" + lineSeparator +
                lineSeparator +
                "1 | user1 | password1" + lineSeparator +
                "2 | userchange1 | password2" + lineSeparator +
                lineSeparator;
        new Application().simpleSQL();
        assertEquals(expected, outContent.toString());
    }
}
