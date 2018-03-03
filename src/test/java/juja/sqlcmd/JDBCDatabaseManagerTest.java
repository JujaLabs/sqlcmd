package juja.sqlcmd;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

import static org.junit.Assert.assertFalse;

public class JDBCDatabaseManagerTest extends DatabaseManagerTest {

    private static final String LOGIN_FOR_SU = "postgres";
    private static final String PASSWORD_FOR_SU = "postgres";
    private static final String TEST_DB_CONNECTION_URL = "jdbc:postgresql://localhost:5432/";
    private static final String NAME_DB_TEST = "sqlcmd_test";
    private static final String LOGIN_FOR_TEST = "sqlcmd";
    private static final String PASSWORD_FOR_TEST = "sqlcmd";

    @BeforeClass
    public static void createConnection() throws SQLException, ClassNotFoundException {
        Class.forName("org.postgresql.Driver");
        connection = DriverManager.getConnection(TEST_DB_CONNECTION_URL, LOGIN_FOR_SU, PASSWORD_FOR_SU);
        try (Statement statement = connection.createStatement()) {
            statement.execute(String.format("CREATE DATABASE \"%s\" OWNER \"%s\"", NAME_DB_TEST, LOGIN_FOR_TEST));
        }
        connection.close();
        connection = DriverManager.getConnection(TEST_DB_CONNECTION_URL + NAME_DB_TEST, LOGIN_FOR_TEST, PASSWORD_FOR_TEST);
    }

    @After
    public void closeConnection() throws SQLException {
        deleteTablesInDatabase(TABLE_NAME);
        databaseManager.close();
    }

    @AfterClass
    public static void dropTestDB() throws SQLException {
        connection.close();
        connection = DriverManager.getConnection(TEST_DB_CONNECTION_URL, LOGIN_FOR_SU, PASSWORD_FOR_SU);
        try (Statement statement = connection.createStatement()) {
            statement.execute(String.format("DROP DATABASE IF EXISTS \"%s\" ", NAME_DB_TEST));
        }
        connection.close();
    }

    @Test
    public void testInsertWhenTypeMismatch() throws SQLException {
        databaseManager.connect(NAME_DB_TEST, LOGIN_FOR_TEST, PASSWORD_FOR_TEST);
        DataSet dataSet = createDataSet(new String[]{"typeMismatch", "name1", "25"});
        createTableWithData(TABLE_NAME);
        boolean actual = databaseManager.insert(TABLE_NAME, dataSet);
        assertFalse(actual);
    }

    @Override
    DatabaseManager getDatabaseManager() {
        return new JDBCDatabaseManager();
    }

    @Override
    void createTableInDatabase(String tableName) throws SQLException {
        executeQuery(String.format("CREATE TABLE %s (id SERIAL PRIMARY KEY)", tableName));
    }

    @Override
    void createTableWithData(String tableName) throws SQLException {
        executeQuery(String.format("CREATE TABLE %s (id INTEGER, name TEXT)", tableName));
        executeQuery(String.format("INSERT INTO %s VALUES (1, 'user')", tableName));
    }

    @Override
    void deleteTablesInDatabase(String tableName) throws SQLException {
        executeQuery(String.format("DROP TABLE IF EXISTS %s", TABLE_NAME));
    }

    @Override
    DataSet createDataSet(String[] row) {
        DataSet oneRow = new DataSet(row.length);
        for (int i = 0; i < row.length; i++) {
            oneRow.add(i, row[i]);
        }
        return oneRow;
    }

    private void executeQuery(String query) throws SQLException {
        try (Statement statement = connection.createStatement()) {
            statement.execute(query);
        }
    }
}
