package juja.sqlcmd;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

import static junit.framework.TestCase.assertFalse;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class DatabaseManagerTest {
    private static final String NAME_DB_TEST = "sqlcmd_test";
    private static final String LOGIN_FOR_TEST = "sqlcmd";
    private static final String PASSWORD_FOR_TEST = "sqlcmd";
    private static final String LOGIN_FOR_SU = "postgres";
    private static final String PASSWORD_FOR_SU = "postgres";
    private static final String TEST_DB_CONNECTION_URL = "jdbc:postgresql://localhost:5432/";
    private static final String TABLE_NAME = "table_name";

    private static Connection connection;

    private DatabaseManager databaseManager;

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

    @Before
    public void setup() {
        databaseManager = new DatabaseManager();
    }

    @After
    public void closeConnection() throws SQLException {
        executeQuery(String.format("DROP TABLE IF EXISTS %s", TABLE_NAME));
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
    public void testConnectionWithValidParameters() {
        assertTrue(databaseManager.connect(NAME_DB_TEST, LOGIN_FOR_TEST, PASSWORD_FOR_TEST));
    }

    @Test
    public void testConnectionWhenDatabaseNotExists() {
        assertFalse(databaseManager.connect("noDatabase", LOGIN_FOR_TEST, PASSWORD_FOR_TEST));
    }

    @Test
    public void testConnectionWhenWrongUser() {
        assertFalse(databaseManager.connect(NAME_DB_TEST, "wrongUser", PASSWORD_FOR_TEST));
    }

    @Test
    public void testConnectionWhenWrongPassword() {
        assertFalse(databaseManager.connect(NAME_DB_TEST, LOGIN_FOR_TEST, "wrongPassword"));
    }

    @Test
    public void testGetTablesNameWhenDbHasNotTables() {
        databaseManager.connect(NAME_DB_TEST, LOGIN_FOR_TEST, PASSWORD_FOR_TEST);
        assertArrayEquals(new String[]{}, databaseManager.getTableNames());
    }

    @Test
    public void testGetTablesNameWhenDbHasTwoTables() throws SQLException {
        String firstTableName = "first_table";
        String secondTableName = "second_table";
        createTwoTablesInDatabase(firstTableName, secondTableName);
        databaseManager.connect(NAME_DB_TEST, LOGIN_FOR_TEST, PASSWORD_FOR_TEST);
        String[] expected = {firstTableName, secondTableName};
        String[] actual = databaseManager.getTableNames();
        executeQuery(String.format("DROP TABLE IF EXISTS %s", firstTableName));
        executeQuery(String.format("DROP TABLE IF EXISTS %s", secondTableName));
        assertArrayEquals(expected, actual);
    }


    @Test
    public void testGetTablesNameWhenConnectionNotExists() {
        assertArrayEquals(new String[]{}, databaseManager.getTableNames());
    }

    @Test
    public void testGetTableDataWhenTableNotExists() {
        databaseManager.connect(NAME_DB_TEST, LOGIN_FOR_TEST, PASSWORD_FOR_TEST);
        DataSet[] expected = new DataSet[0];
        DataSet[] actual = databaseManager.getTableData("tableNotExist");
        assertArrayEquals(expected, actual);
    }

    @Test
    public void testGetTableDataWhenConnectionNotExists() {
        DataSet[] expected = new DataSet[0];
        DataSet[] actual = databaseManager.getTableData("someTable");
        assertArrayEquals(expected, actual);
    }

    @Test
    public void testGetTableDataWhenEmptyTable() throws SQLException {
        executeQuery(String.format("CREATE TABLE %s (id SERIAL PRIMARY KEY)", TABLE_NAME));
        databaseManager.connect(NAME_DB_TEST, LOGIN_FOR_TEST, PASSWORD_FOR_TEST);
        DataSet[] expected = new DataSet[0];
        DataSet[] actual = databaseManager.getTableData(TABLE_NAME);
        assertArrayEquals(expected, actual);
    }

    @Test
    public void testGetTableData() throws SQLException {
        createTableWithData(TABLE_NAME);
        DataSet firstRow = createDataSet(new String[]{"1", "name1", "25"});
        DataSet secondRow = createDataSet(new String[]{"2", "name2", "35"});
        DataSet thirdRow = createDataSet(new String[]{"3", "name3", "45"});
        databaseManager.connect(NAME_DB_TEST, LOGIN_FOR_TEST, PASSWORD_FOR_TEST);
        DataSet[] expected = new DataSet[]{firstRow, secondRow, thirdRow};
        DataSet[] actual = databaseManager.getTableData(TABLE_NAME);
        assertArrayEquals(expected, actual);
    }

    @Test
    public void testDataSetReturnsRow() {
        DataSet dataSet = createDataSet(new String[]{"1", "name1", "25"});
        assertEquals("'1','name1','25'", dataSet.row());
    }

    @Test
    public void testInsertWhenValidData() throws SQLException {
        DataSet dataSet = createDataSet(new String[]{"111", "someName", "25"});
        createTableWithData(TABLE_NAME);
        databaseManager.connect(NAME_DB_TEST, LOGIN_FOR_TEST, PASSWORD_FOR_TEST);
        boolean actual = databaseManager.insert(TABLE_NAME, dataSet);
        assertTrue(actual);
    }

    @Test
    public void testInsertWhenTableNotExists() {
        DataSet dataSet = createDataSet(new String[]{"1", "name1", "25"});
        databaseManager.connect(NAME_DB_TEST, LOGIN_FOR_TEST, PASSWORD_FOR_TEST);
        assertFalse(databaseManager.insert("noTable", dataSet));
    }

    @Test
    public void testInsertWhenDataSetHasExtraColumns() throws SQLException {
        DataSet dataSet = createDataSet(new String[]{"1", "name1", "25", "extra"});
        createTableWithData(TABLE_NAME);
        databaseManager.connect(NAME_DB_TEST, LOGIN_FOR_TEST, PASSWORD_FOR_TEST);
        boolean actual = databaseManager.insert(TABLE_NAME, dataSet);
        assertFalse(actual);
    }

    @Test
    public void testInsertWhenTypeMismatch() throws SQLException {
        DataSet dataSet = createDataSet(new String[]{"typeMismatch", "name1", "25"});
        createTableWithData(TABLE_NAME);
        databaseManager.connect(NAME_DB_TEST, LOGIN_FOR_TEST, PASSWORD_FOR_TEST);
        boolean actual = databaseManager.insert(TABLE_NAME, dataSet);
        assertFalse(actual);
    }

    @Test
    public void testDeleteWhenValidData() throws SQLException {
        createTableWithData(TABLE_NAME);
        databaseManager.connect(NAME_DB_TEST, LOGIN_FOR_TEST, PASSWORD_FOR_TEST);
        boolean actual = databaseManager.delete(TABLE_NAME, 1);
        executeQuery(String.format("DROP TABLE IF EXISTS %s", TABLE_NAME));
        assertTrue(actual);
    }

    @Test
    public void testDeleteWhenTableNotExists() {
        databaseManager.connect(NAME_DB_TEST, LOGIN_FOR_TEST, PASSWORD_FOR_TEST);
        assertFalse(databaseManager.delete("noTable", 1));
    }

    @Test
    public void testDeleteWhenIdNotExists() throws SQLException {
        createTableWithData(TABLE_NAME);
        databaseManager.connect(NAME_DB_TEST, LOGIN_FOR_TEST, PASSWORD_FOR_TEST);
        boolean actual = databaseManager.delete(TABLE_NAME, -1);
        executeQuery(String.format("DROP TABLE IF EXISTS %s", TABLE_NAME));
        assertFalse(actual);
    }

    private DataSet createDataSet(String[] row) {
        DataSet oneRow = new DataSet(row.length);
        for (int i = 0; i < row.length; i++) {
            oneRow.add(i, row[i]);
        }
        return oneRow;
    }

    private void createTableWithData(String tableName) throws SQLException {
        executeQuery(String.format("CREATE TABLE %s (id INTEGER, name TEXT, age SMALLINT)", tableName));
        executeQuery(String.format("INSERT INTO %s VALUES (1, 'name1', 25)", tableName));
        executeQuery(String.format("INSERT INTO %s VALUES (2, 'name2', 35)", tableName));
        executeQuery(String.format("INSERT INTO %s VALUES (3, 'name3', 45)", tableName));
    }

    private void createTwoTablesInDatabase(String first_table, String second_table) throws SQLException {
        executeQuery(String.format("CREATE TABLE %s (id SERIAL PRIMARY KEY)", first_table));
        executeQuery(String.format("CREATE TABLE %s (id SERIAL PRIMARY KEY)", second_table));
    }

    private void executeQuery(String query) throws SQLException {
        try (Statement statement = connection.createStatement()) {
            statement.execute(query);
        }
    }
}