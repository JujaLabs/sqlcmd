package juja.sqlcmd;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class Application {

    private static final String POSTGRESQL_DRIVER = "org.postgresql.Driver";
    private static final String DB_CONNECTION_PATH = "jdbc:postgresql://127.0.0.1:5432/sqlcmd";
    private static final String DB_USER = "sqlcmd";
    private static final String DB_PASSWORD = "sqlcmd";
    private static final String TABLE_NAME = "user";
    public static final String SHOW_EXISTING_TABLES_NAME = "SELECT * FROM pg_catalog.pg_tables WHERE schemaname != 'pg_catalog' AND schemaname != 'information_schema';";
    private static final String LINE_SEPARATOR = System.lineSeparator();
    public static final String COLUMN_SEPARATOR = " | ";
    private Connection connection;

    public static void main(String[] arg) throws SQLException {
        new Application().simpleSQL();
    }

    public void simpleSQL() throws SQLException {
        checkJDBCDriver();
        createDbConnection();
        showExistingTablesName();
        createTable(TABLE_NAME);
        for (int i = 1; i < 4; i++) {
            addUser("user" + i, "password" + i);
        }
        showExistingTablesName();
        showExistingDataInTable(TABLE_NAME);
        updateDataByTitle("user2", "userchange1", TABLE_NAME, "name");
        showExistingDataInTable(TABLE_NAME);
        deleteDataByTitle(TABLE_NAME, "name", "user3");
        showExistingDataInTable(TABLE_NAME);
        connection.close();
    }

    private void deleteDataByTitle(String tableName, String columnName, String title) throws SQLException {
        String sqlQuery = String.format("DELETE FROM \"%1$s\" WHERE %2$s = ? ", tableName, columnName);
        try (PreparedStatement preparedStatement = connection.prepareStatement(sqlQuery)) {
            preparedStatement.setString(1, title);
            preparedStatement.executeUpdate();
        }
    }

    private void updateDataByTitle(String oldData, String newData, String tableName, String columnName) throws SQLException {
        String sqlQuery = String.format("UPDATE \"%3$s\" SET %4$s = \'%2$s\' WHERE %4$s = \'%1s\';", oldData, newData, tableName, columnName);
        try (Statement statement = connection.createStatement()) {
            statement.executeUpdate(sqlQuery);
        }
    }

    private void showExistingDataInTable(String tableName) throws SQLException {
        String sqlQuery = String.format("SELECT * FROM \"%s\"", tableName);
        try (Statement statement = connection.createStatement(); ResultSet resultSet = statement.executeQuery(sqlQuery);) {
            if (tableName != null) {
                int columnCount = resultSet.getMetaData().getColumnCount();
                StringBuilder tableData = new StringBuilder();
                while (resultSet.next()) {
                    for (int i = 1; i <= columnCount; i++) {
                        tableData.append(resultSet.getString(i)).append(COLUMN_SEPARATOR);
                    }
                    int startPosition = tableData.length() - COLUMN_SEPARATOR.length();
                    int endPosition = tableData.length();
                    tableData.replace(startPosition, endPosition, LINE_SEPARATOR);
                }
                System.out.println(tableData.toString());
            }
        }
    }

    private void addUser(String userName, String userPassword) throws SQLException, NullPointerException {
        String sqlQuery = String.format("INSERT INTO \"%s\"(name, password) VALUES('%s','%s')", TABLE_NAME, userName, userPassword);
        try (Statement statement = connection.createStatement()) {
            statement.executeUpdate(sqlQuery);
        }
    }

    private void createTable(String tableName) throws SQLException {
        try (Statement statement = connection.createStatement()) {
            String sqlQuery = String.format("CREATE TABLE public.\"%s\"(id SERIAL PRIMARY KEY , name TEXT  NOT NULL,password TEXT  NOT NULL)", tableName);
            statement.executeUpdate(sqlQuery);
        }
    }

    private void showExistingTablesName() throws SQLException {
        try (Statement statement = connection.createStatement(); ResultSet rs = statement.executeQuery(SHOW_EXISTING_TABLES_NAME)) {
            System.out.println("Existing Tables:");
            if (rs.isBeforeFirst()) {
                while (rs.next()) {
                    System.out.println("-" + rs.getString("tablename") + LINE_SEPARATOR);
                }
            } else {
                System.out.println("db is empty" + LINE_SEPARATOR);
            }
        }
    }

    private void createDbConnection() {
        try {
            connection = DriverManager.getConnection(DB_CONNECTION_PATH, DB_USER, DB_PASSWORD);
        } catch (SQLException e) {
            System.out.println("Connection Failed! Check output console");
            e.printStackTrace();
        }
        if (connection != null) {
            System.out.println("Connection created" + LINE_SEPARATOR);
        } else {
            System.out.println("Failed to make connection!");
        }
    }

    private void checkJDBCDriver() {
        System.out.println("-------- PostgreSQL "
                + "JDBC Connection  ------------");
        try {
            Class.forName(POSTGRESQL_DRIVER);
        } catch (ClassNotFoundException e) {
            System.out.println("Where is your PostgreSQL JDBC Driver? "
                    + "Include in your library path!");
            e.printStackTrace();
        }
        System.out.println("PostgreSQL JDBC Driver Registered!");
    }

}
