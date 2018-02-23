package juja.sqlcmd;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

import static org.junit.Assert.assertEquals;

public class ApplicationTest {
    private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
    private final ByteArrayOutputStream errContent = new ByteArrayOutputStream();

    private final PrintStream originalOut = System.out;
    private final PrintStream originalErr = System.err;

    private static final String LOGIN_FOR_SU = "postgres";
    private static final String PASSWORD_FOR_SU = "postgres";
    private static final String LOGIN_FOR_TEST = "sqlcmd";
    private static final String NAME_DB_TEST = "sqlcmd";
    private static final String TEST_DB_CONNECTION_URL = "jdbc:postgresql://localhost:5432/";
    private static final String LINE_SEPARATOR = System.lineSeparator();

    private static Connection connection;


    @BeforeClass
    public static void createConnection() throws SQLException, ClassNotFoundException {
        Class.forName("org.postgresql.Driver");
        connection = DriverManager.getConnection(TEST_DB_CONNECTION_URL, LOGIN_FOR_SU, PASSWORD_FOR_SU);
        try (Statement statement = connection.createStatement()) {
            statement.execute(String.format("CREATE DATABASE \"%s\" OWNER \"%s\"", NAME_DB_TEST, LOGIN_FOR_TEST));
        }
    }

    @Before
    public void setUpStreams() {
        System.setOut(new PrintStream(outContent));
        System.setErr(new PrintStream(errContent));
    }

    @After
    public void cleanUpStreams() {
        System.setOut(originalOut);
        System.setErr(originalErr);
    }
    @AfterClass
    public static void dropTestDB() throws SQLException {
        try (Statement statement = connection.createStatement()) {
            statement.execute(String.format("DROP DATABASE IF EXISTS \"%s\" ", NAME_DB_TEST));
        }
        connection.close();
    }

    @Test
    public void testloadApplication() throws SQLException, ClassNotFoundException {
        String expected = "db is empty." + LINE_SEPARATOR +
                "user" + LINE_SEPARATOR +
                LINE_SEPARATOR +
                "1 | user1 | password1" + LINE_SEPARATOR +
                "2 | user2 | password2" + LINE_SEPARATOR +
                "3 | user3 | password3" + LINE_SEPARATOR +
                LINE_SEPARATOR +
                "1 | user1 | password1" + LINE_SEPARATOR +
                "3 | user3 | password3" + LINE_SEPARATOR +
                "2 | userсhange1 | password2" + LINE_SEPARATOR +
                LINE_SEPARATOR +
                "1 | user1 | password1" + LINE_SEPARATOR +
                "2 | userсhange1 | password2" + LINE_SEPARATOR +
                LINE_SEPARATOR;
        new Application().simpleSQL();
        assertEquals(expected, outContent.toString());
    }
}
