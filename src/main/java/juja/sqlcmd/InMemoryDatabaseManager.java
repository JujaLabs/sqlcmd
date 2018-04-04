package juja.sqlcmd;

import java.sql.SQLException;

public class InMemoryDatabaseManager implements DatabaseManager {
    private Table[] tables;

    public InMemoryDatabaseManager() {
        tables = new Table[0];
    }

    public void createTable(String tableName, int columns_number) {
        if (tableName == null) throw new IllegalArgumentException("Table name must be not null!");

        if (columns_number < 0) throw new IllegalArgumentException("Number of columns must be positive!");

        Table[] tmp = new Table[tables.length + 1];
        System.arraycopy(tables, 0, tmp, 0, tables.length);
        tables = tmp;
        int lastElementIndex = tables.length - 1;
        tables[lastElementIndex] = new Table(tableName, columns_number);
    }

    @Override
    public boolean connect(String database, String user, String password) {
        return true;
    }

    @Override
    public String[] getTableNames() throws SQLException {
        int numberOfTables = tables.length;
        String[] tableNames = new String[numberOfTables];
        for (int i = 0; i < numberOfTables; i++) {
            Table currentTable = tables[i];
            tableNames[i] = currentTable.getTableName();
        }
        return tableNames;
    }

    @Override
    public DataSet[] getTableData(String tableName) throws SQLException {
        Table table = tableByName(tableName);
        return table != null ? table.tableData() : new DataSet[0];
    }

    @Override
    public boolean insert(String tableName, DataSet dataset) {
        Table table = tableByName(tableName);
        return table != null && table.insert(dataset);
    }

    @Override
    public boolean delete(String tableName, int id) {
        Table table = tableByName(tableName);
        return table != null && table.delete(id);
    }

    @Override
    public boolean update(String tableName, int id) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void close() throws SQLException {
        //NOP
    }

    @Override
    public boolean hasConnection() {
        return true;
    }

    private Table tableByName(String tableName) {
        for (Table currentTable : tables) {
            if (currentTable.getTableName().equals(tableName)) {
                return currentTable;
            }
        }
        return null;
    }

    private static class Table {
        private String tableName;
        private DataSet[] rows;
        private int columnsNumber;

        Table(String tableName, int columnsNumber) {
            this.tableName = tableName;
            this.columnsNumber = columnsNumber;
            rows = new DataSet[0];
        }

        DataSet[] tableData() {
            return rows;
        }

        String getTableName() {
            return tableName;
        }

        boolean insert(DataSet row) {
            if (row.getSize() != columnsNumber) {
                return false;
            }
            DataSet[] tmp = new DataSet[rows.length + 1];
            System.arraycopy(rows, 0, tmp, 0, rows.length);
            rows = tmp;
            int lastElementIndex = rows.length - 1;
            rows[lastElementIndex] = row;
            return true;
        }

        boolean delete(int id) {
            int rowIndexWithId = findRowById(id);
            if (rowIndexWithId != -1) {
                DataSet[] tmp = new DataSet[rows.length - 1];
                System.arraycopy(rows, 0, tmp, 0, rowIndexWithId);
                if (rows.length != rowIndexWithId) {
                    System.arraycopy(rows, rowIndexWithId + 1, tmp, rowIndexWithId, rows.length - rowIndexWithId - 1);
                }
                rows = tmp;
                return true;
            }
            return false;
        }

        private int findRowById(int id) {
            for (int index = 0; index < rows.length; index++) {
                DataSet currentRow = rows[index];
                String currentRowId = currentRow.values()[0];
                if (currentRowId.equals(String.valueOf(id))) return index;
            }
            return -1;
        }
    }
}
