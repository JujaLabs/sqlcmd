package juja.sqlcmd;

import org.junit.After;

public class InMemoryDatabaseManagerTest extends DatabaseManagerTest {

    @After
    public void closeConnection() {
        deleteTablesInDatabase(TABLE_NAME);
        databaseManager.close();
    }

    @Override
    DatabaseManager getDatabaseManager() {
        return new InMemoryDatabaseManager();
    }

    @Override
    void createTableInDatabase(String tableName) {
        ((InMemoryDatabaseManager) databaseManager).createTable(tableName);
    }

    @Override
    void deleteTablesInDatabase(String tableName) {
        ((InMemoryDatabaseManager) databaseManager).deleteTable(tableName);
    }

    @Override
    DataSet createDataSet(String[] row) {
        DataSet oneRow = new DataSet(row.length);
        for (int i = 0; i < row.length; i++) {
            oneRow.add(i, row[i]);
        }
        return oneRow;
    }

    @Override
    void createTableWithData(String tableName) {
        ((InMemoryDatabaseManager) databaseManager).createTable(tableName);
        DataSet dataSet = new DataSet(2);
        dataSet.add(0, "1");
        dataSet.add(1, "user");
        databaseManager.insert(tableName, dataSet);
    }
}
