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

import static org.hamcrest.Matchers.arrayContainingInAnyOrder;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

public class DatabaseManagerTest {
    private static final String DB_CONNECTION_URL = "jdbc:postgresql://127.0.0.1:5432/";
    private static final String DB_NAME = "sqlcmd";
    private static final String DB_USER = "sqlcmd";
    private static final String DB_USER_PASSWORD = "sqlcmd";
    private static final String TEST_DB_NAME = "testdatabase";
    private static final String TEST_TABLE_NAME = "test_table";

    private static Connection connection;

    private DatabaseManager databaseManager;

    @BeforeClass
    public static void setConnection() throws SQLException {
        connection = DriverManager.getConnection(DB_CONNECTION_URL + DB_NAME, DB_USER, DB_USER_PASSWORD);
        executeSqlQuery("DROP DATABASE IF EXISTS " + TEST_DB_NAME);
        executeSqlQuery("CREATE DATABASE " + TEST_DB_NAME);
        connection.close();
        connection = DriverManager.getConnection(DB_CONNECTION_URL + TEST_DB_NAME, DB_USER, DB_USER_PASSWORD);
    }

    @AfterClass
    public static void closeConnection() throws SQLException {
        connection.close();
        connection = DriverManager.getConnection(DB_CONNECTION_URL + DB_NAME, DB_USER, DB_USER_PASSWORD);
        executeSqlQuery("DROP DATABASE IF EXISTS " + TEST_DB_NAME);
        connection.close();
    }

    @Before
    public void init() throws SQLException {
        dropAllTables();
        databaseManager = new DatabaseManager();
    }

    @After
    public void closeDbManagerConnection() throws SQLException {
        databaseManager.close();
    }

    @Test
    public void connectWhenValidAllParametersReturnsTrue() {
        assertTrue(databaseManager.connect("sqlcmd", "sqlcmd", "sqlcmd"));
    }

    @Test
    public void connectWhenInvalidDatabaseNameReturnsFalse() {
        assertFalse(databaseManager.connect("Notsqlcmd", "sqlcmd", "sqlcmd"));
    }

    @Test
    public void connectWhenInvalidUserNameReturnsFalse() {
        assertFalse(databaseManager.connect("sqlcmd", "Notsqlcmd", "sqlcmd"));
    }

    @Test
    public void connectWhenInvalidUserPasswordReturnsFalse() {
        assertFalse(databaseManager.connect("sqlcmd", "sqlcmd", "Notsqlcmd"));
    }

    @Test
    public void getTableNamesWhenNoTablesInDbReturnsEmptyArray() throws SQLException {
        databaseManager.connect(TEST_DB_NAME, DB_USER, DB_USER_PASSWORD);
        String[] expected = new String[]{};
        assertArrayEquals(expected, databaseManager.getTableNames());
    }

    @Test
    public void getTableNamesWhenTwoTablesInDbReturnsTableNamesArray() throws SQLException {
        databaseManager.connect(TEST_DB_NAME, DB_USER, DB_USER_PASSWORD);
        executeSqlQuery("CREATE TABLE table1()");
        executeSqlQuery("CREATE TABLE table2()");
        String[] expected = new String[]{"table1", "table2"};
        assertArrayEquals(expected, databaseManager.getTableNames());
    }

    @Test
    public void getTableDataWhenEmptyTableReturnsEmptyArray() throws SQLException {
        databaseManager.connect(TEST_DB_NAME, DB_USER, DB_USER_PASSWORD);
        createTestTableWithIdAndName(TEST_TABLE_NAME);
        DataSet[] expected = new DataSet[]{};
        assertArrayEquals(expected, databaseManager.getTableData(TEST_TABLE_NAME));
    }

    @Test
    public void getTableDataWhenTableNotExistsReturnsEmptyArray() throws SQLException {
        databaseManager.connect(TEST_DB_NAME, DB_USER, DB_USER_PASSWORD);
        DataSet[] expected = new DataSet[]{};
        assertArrayEquals(expected, databaseManager.getTableData("WrongTableName"));
    }

    @Test
    public void getTableDataWhenValidDataReturnsTableDataArray() throws SQLException {
        databaseManager.connect(TEST_DB_NAME, DB_USER, DB_USER_PASSWORD);
        createTestTableWithIdAndName(TEST_TABLE_NAME);
        executeSqlQuery("INSERT INTO " + TEST_TABLE_NAME + " VALUES(1,'name1')");
        executeSqlQuery("INSERT INTO " + TEST_TABLE_NAME + " VALUES(2,'name2')");
        DataSet row1 = new DataSet(2);
        row1.insertValue(0, "1");
        row1.insertValue(1, "name1");
        DataSet row2 = new DataSet(2);
        row2.insertValue(0, "2");
        row2.insertValue(1, "name2");
        DataSet[] expected = new DataSet[]{row1, row2};
        DataSet[] actual = databaseManager.getTableData(TEST_TABLE_NAME);
        assertThat(actual, arrayContainingInAnyOrder(expected));
    }

    @Test
    public void insertWhenValidDataReturnsTrue() throws SQLException {
        databaseManager.connect(TEST_DB_NAME, DB_USER, DB_USER_PASSWORD);
        DataSet tableRow = new DataSet(2);
        tableRow.insertValue(0, "1");
        tableRow.insertValue(1, "name1");
        createTestTableWithIdAndName(TEST_TABLE_NAME);
        assertTrue(databaseManager.insert(TEST_TABLE_NAME, tableRow));
    }

    @Test
    public void insertWhenTypeMismatchParameterReturnsFalse() throws SQLException {
        databaseManager.connect(TEST_DB_NAME, DB_USER, DB_USER_PASSWORD);
        DataSet tableRow = new DataSet(2);
        tableRow.insertValue(0, "NAN");
        tableRow.insertValue(1, "name1");
        createTestTableWithIdAndName(TEST_TABLE_NAME);
        assertFalse(databaseManager.insert(TEST_TABLE_NAME, tableRow));
    }

    @Test
    public void insertWhenTableNotExistsReturnsFalse() throws SQLException {
        databaseManager.connect(TEST_DB_NAME, DB_USER, DB_USER_PASSWORD);
        DataSet tableRow = new DataSet(2);
        tableRow.insertValue(0, "1");
        tableRow.insertValue(1, "name1");
        assertFalse(databaseManager.insert("WrongTableName", tableRow));
    }

    @Test
    public void insertWhenInvalidNumberOfParametersReturnsFalse() throws SQLException {
        databaseManager.connect(TEST_DB_NAME, DB_USER, DB_USER_PASSWORD);
        DataSet tableRow = new DataSet(3);
        tableRow.insertValue(0, "1");
        tableRow.insertValue(1, "name1");
        tableRow.insertValue(2, "name2");
        createTestTableWithIdAndName(TEST_TABLE_NAME);
        assertFalse(databaseManager.insert(TEST_TABLE_NAME, tableRow));
    }

    @Test
    public void deleteWhenTableNotExistsReturnsFalse() {
        databaseManager.connect(TEST_DB_NAME, DB_USER, DB_USER_PASSWORD);
        assertFalse(databaseManager.delete("WrongTableName", 1));
    }

    @Test
    public void deleteWhenValidDataReturnsTrue() throws SQLException {
        databaseManager.connect(TEST_DB_NAME, DB_USER, DB_USER_PASSWORD);
        createTestTableWithIdAndName(TEST_TABLE_NAME);
        executeSqlQuery("INSERT INTO " + TEST_TABLE_NAME + " VALUES(1,'name1')");
        executeSqlQuery("INSERT INTO " + TEST_TABLE_NAME + " VALUES(2,'name2')");
        assertTrue(databaseManager.delete(TEST_TABLE_NAME, 1));
    }

    @Test
    public void deleteWhenNotExistedIdReturnsFalse() throws SQLException {
        databaseManager.connect(TEST_DB_NAME, DB_USER, DB_USER_PASSWORD);
        createTestTableWithIdAndName(TEST_TABLE_NAME);
        executeSqlQuery("INSERT INTO " + TEST_TABLE_NAME + " VALUES(1,'name1')");
        assertFalse(databaseManager.delete(TEST_TABLE_NAME, 2));
    }

    private void createTestTableWithIdAndName(String tableName) throws SQLException {
        String sqlQuery = String.format("CREATE TABLE %s(" +
                "id INTEGER," +
                "name VARCHAR(128)" +
                ")", tableName);
        executeSqlQuery(sqlQuery);
    }

    private static void executeSqlQuery(String sqlQuery) throws SQLException {
        try (Statement statement = connection.createStatement()) {
            statement.execute(sqlQuery);
        }
    }

    private static void dropAllTables() throws SQLException {
        executeSqlQuery("DROP SCHEMA public CASCADE");
        executeSqlQuery("CREATE SCHEMA public");
    }
}