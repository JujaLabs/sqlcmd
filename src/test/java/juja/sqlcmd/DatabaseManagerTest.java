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
import java.util.Date;

import static junit.framework.TestCase.assertFalse;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class DatabaseManagerTest {
    private static final String DB_NAME = "sqlcmd";
    private static final String USER = "sqlcmd";
    private static final String PASSWORD = "sqlcmd";
    private static final String TEST_DB_CONNECTION_URL = "jdbc:postgresql://localhost:5432/";
    private static final String TABLE_NAME = "table_name";

    private static Connection connection;
    private static String dbName;

    private DatabaseManager databaseManager;

    @BeforeClass
    public static void createConnection() throws SQLException, ClassNotFoundException {
        Class.forName("org.postgresql.Driver");
        connection = DriverManager.getConnection(TEST_DB_CONNECTION_URL, USER, PASSWORD);
        dbName = new Date().toString();
        try (Statement statement = connection.createStatement()) {
            statement.execute(String.format("CREATE DATABASE \"%s\" ", dbName));
        }
        connection.close();
        connection = DriverManager.getConnection(TEST_DB_CONNECTION_URL + dbName, USER, PASSWORD);
    }

    @Before
    public void setup() {
        databaseManager = new DatabaseManager();
    }

    @Test
    public void testConnectionWithCorrectParameters() {
        assertTrue(databaseManager.connect(DB_NAME, USER, PASSWORD));
    }

    @Test
    public void testConnectionIfDatabaseNotExists() {
        assertFalse(databaseManager.connect("noDatabase", USER, PASSWORD));
    }

    @Test
    public void testConnectionIfWrongUser() {
        assertFalse(databaseManager.connect(DB_NAME, "wrongUser", PASSWORD));
    }

    @Test
    public void testConnectionIfWrongPassword() {
        assertFalse(databaseManager.connect(DB_NAME, USER, "wrongPassword"));
    }

    @Test
    public void testGetTablesNameWhenDbHasNotTables() {
        databaseManager.connect(dbName, USER, PASSWORD);
        assertArrayEquals(new String[]{}, databaseManager.getTableNames());
    }

    @Test
    public void testGetTablesNameWhenDbHasTwoTables() throws SQLException {
        String firstTableName = "first_table";
        String secondTableName = "second_table";
        createTwoTablesInDatabase(firstTableName, secondTableName);
        databaseManager.connect(dbName, USER, PASSWORD);
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
        databaseManager.connect(dbName, USER, PASSWORD);
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
        databaseManager.connect(dbName, USER, PASSWORD);
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
        databaseManager.connect(dbName, USER, PASSWORD);
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
        databaseManager.connect(dbName, USER, PASSWORD);
        boolean actual = databaseManager.insert(TABLE_NAME, dataSet);
        assertTrue(actual);
    }

    @Test
    public void testInsertWhenTableNotExists() {
        DataSet dataSet = createDataSet(new String[]{"1", "name1", "25"});
        databaseManager.connect(dbName, USER, PASSWORD);
        assertFalse(databaseManager.insert("noTable", dataSet));
    }

    @Test
    public void testInsertWhenDataSetHasExtraColumns() throws SQLException {
        DataSet dataSet = createDataSet(new String[]{"1", "name1", "25", "extra"});
        createTableWithData(TABLE_NAME);
        databaseManager.connect(dbName, USER, PASSWORD);
        boolean actual = databaseManager.insert(TABLE_NAME, dataSet);
        assertFalse(actual);
    }

    @Test
    public void testInsertWhenTypeMismatch() throws SQLException {
        DataSet dataSet = createDataSet(new String[]{"typeMismatch", "name1", "25"});
        createTableWithData(TABLE_NAME);
        databaseManager.connect(dbName, USER, PASSWORD);
        boolean actual = databaseManager.insert(TABLE_NAME, dataSet);
        assertFalse(actual);
    }

    @Test
    public void testDeleteWhenValidData() throws SQLException {
        createTableWithData(TABLE_NAME);
        databaseManager.connect(dbName, USER, PASSWORD);
        boolean actual = databaseManager.delete(TABLE_NAME, 1);
        executeQuery(String.format("DROP TABLE IF EXISTS %s", TABLE_NAME));
        assertTrue(actual);
    }

    @Test
    public void testDeleteWhenTableNotExists() {
        databaseManager.connect(dbName, USER, PASSWORD);
        assertFalse(databaseManager.delete("noTable", 1));
    }

    @Test
    public void testDeleteWhenIdNotExists() throws SQLException {
        createTableWithData(TABLE_NAME);
        databaseManager.connect(dbName, USER, PASSWORD);
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

    @After
    public void closeConnection() throws SQLException {
        executeQuery(String.format("DROP TABLE IF EXISTS %s", TABLE_NAME));
        databaseManager.close();
    }

    @AfterClass
    public static void dropTestDB() throws SQLException {
        connection.close();
        connection = DriverManager.getConnection(TEST_DB_CONNECTION_URL, USER, PASSWORD);
        try (Statement statement = connection.createStatement()) {
            statement.execute(String.format("DROP DATABASE IF EXISTS \"%s\" ", dbName));
        }
        connection.close();
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
