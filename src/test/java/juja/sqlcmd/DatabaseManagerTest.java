package juja.sqlcmd;

import org.junit.Before;
import org.junit.Test;

import java.sql.Connection;
import java.sql.SQLException;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public abstract class DatabaseManagerTest {
    private static final String NAME_DB_TEST = "sqlcmd_test";
    private static final String LOGIN_FOR_TEST = "sqlcmd";
    private static final String PASSWORD_FOR_TEST = "sqlcmd";

    DatabaseManager databaseManager;

    static Connection connection;
    static final String TABLE_NAME = "table_name";

    @Before
    public void setup() {
        databaseManager = getDatabaseManager();
    }

    abstract DatabaseManager getDatabaseManager();

    abstract void createTableInDatabase(String tableName) throws SQLException;

    abstract void createTableWithData(String tableName) throws SQLException;

    abstract void deleteTablesInDatabase(String tableName) throws SQLException;

    abstract DataSet createDataSet(String[] row);

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
    public void testGetTableNameWhenDbHasTables() throws SQLException {
        createTableInDatabase(TABLE_NAME);
        databaseManager.connect(NAME_DB_TEST, LOGIN_FOR_TEST, PASSWORD_FOR_TEST);
        String[] expected = {TABLE_NAME};
        String[] actual = databaseManager.getTableNames();
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
        createTableInDatabase(TABLE_NAME);
        databaseManager.connect(NAME_DB_TEST, LOGIN_FOR_TEST, PASSWORD_FOR_TEST);
        DataSet[] expected = new DataSet[0];
        DataSet[] actual = databaseManager.getTableData(TABLE_NAME);
        assertArrayEquals(expected, actual);
    }

    @Test
    public void testGetTableData() throws SQLException {
        createTableWithData(TABLE_NAME);
        DataSet firstRow = createDataSet(new String[]{"1", "user"});
        databaseManager.connect(NAME_DB_TEST, LOGIN_FOR_TEST, PASSWORD_FOR_TEST);
        DataSet[] expected = new DataSet[]{firstRow};
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
        DataSet dataSet = createDataSet(new String[]{"111", "someName"});
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
    public void testDeleteWhenValidData() throws SQLException {
        createTableWithData(TABLE_NAME);
        databaseManager.connect(NAME_DB_TEST, LOGIN_FOR_TEST, PASSWORD_FOR_TEST);
        boolean actual = databaseManager.delete(TABLE_NAME, 1);
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
        assertFalse(actual);
    }

}