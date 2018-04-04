package juja.sqlcmd;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;

public class JdbcDatabaseManager implements DatabaseManager {
    private static final String DB_CONNECTION_URL = "jdbc:postgresql://127.0.0.1:5432/";

    private Connection connection;

    @Override
    public boolean connect(String database, String user, String password) {
        try {
            Class.forName("org.postgresql.Driver");
            connection = DriverManager.getConnection(DB_CONNECTION_URL + database + "?loggerLevel=OFF", user, password);
            return true;
        } catch (ClassNotFoundException | SQLException e) {
            return false;
        }
    }

    @Override
    public String[] getTableNames() throws SQLException {
        String sqlQuery = "SELECT relname FROM pg_stat_user_tables ORDER BY relname";
        try (Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(sqlQuery)) {
            int size = numberOfEntries("pg_stat_user_tables");
            String[] tableNames = new String[size];
            int index = 0;
            while (resultSet.next()) {
                tableNames[index++] = resultSet.getString(1);
            }
            return tableNames;
        }
    }

    @Override
    public DataSet[] getTableData(String tableName) throws SQLException {
        if (!hasTable(tableName)) return new DataSet[0];
        int tableSize = numberOfEntries(tableName);
        if (tableSize == 0) return new DataSet[0];
        String sqlQuery = String.format("SELECT * FROM %s", tableName);
        try (Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(sqlQuery)) {
            ResultSetMetaData rsmd = resultSet.getMetaData();
            int rowSize = rsmd.getColumnCount();
            return tableRows(resultSet, rowSize, tableSize);
        }
    }

    @Override
    public boolean insert(String tableName, DataSet dataset) {
        String insertQueryValues = dataSetCsv(dataset);
        String sqlQuery = String.format("INSERT INTO %s VALUES(%s)", tableName, insertQueryValues);
        try (Statement statement = connection.createStatement()) {
            statement.executeUpdate(sqlQuery);
            return true;
        } catch (SQLException e) {
            return false;
        }
    }

    private String dataSetCsv(DataSet dataset) {
        String[] values = dataset.values();
        StringBuilder csvBuilder = new StringBuilder();
        for (String value : values) {
            csvBuilder.append("'")
                    .append(value)
                    .append("'")
                    .append(",");
        }
        int lastCommaIndex = csvBuilder.lastIndexOf(",");
        return csvBuilder.substring(0, lastCommaIndex);
    }

    @Override
    public boolean update(String tableName, int id) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean delete(String tableName, int id) {
        String sqlQuery = String.format("DELETE FROM %s WHERE id=%s", tableName, id);
        try (Statement statement = connection.createStatement()) {
            int rowsAffected = statement.executeUpdate(sqlQuery);
            return rowsAffected > 0;
        } catch (SQLException e) {
            return false;
        }
    }

    @Override
    public void close() throws SQLException {
        if (connection != null) connection.close();
    }

    private DataSet[] tableRows(ResultSet resultSet, int rowSize, int tableSize) throws SQLException {
        DataSet[] tableData = new DataSet[tableSize];
        int rowNumber = 0;
        while (resultSet.next()) {
            DataSet tableRow = new DataSet(rowSize);
            for (int i = 0; i < rowSize; i++) {
                String columnValue = resultSet.getString(i + 1);
                tableRow.insertValue(i, columnValue);
            }
            tableData[rowNumber++] = tableRow;
        }
        return tableData;
    }

    @Override
    public boolean hasConnection() {
        return connection != null;
    }

    private int numberOfEntries(String tableName) throws SQLException {
        String sqlQuery = String.format("SELECT COUNT(*) as RECORDS FROM %s", tableName);
        try (Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(sqlQuery)) {
            if (resultSet.next())
                return resultSet.getInt("RECORDS");
            else return 0;
        }
    }

    private boolean hasTable(String tableName) throws SQLException {
        String[] tableNames = getTableNames();
        for (String name : tableNames) {
            if (name.equals(tableName)) return true;
        }
        return false;
    }
}