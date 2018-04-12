package juja.sqlcmd;

public interface DatabaseManager {
    boolean connect(String database, String user, String password);

    /*
     * Method for inserting data into a table.
     * If successful, the method returns true, else return false
     */
    boolean insert(String tableName, DataSet dataset);

    /*
     * Method for update data in the table.
     * If successful, the method returns true, else return false
     */
    boolean update(String tableName, int id);

    /*
     * Method for deleting data from a table.
     * If successful, the method returns true, else return false
     */
    boolean delete(String tableName, int id);

    String[] getTableNames();

    DataSet[] getTableData(String tableName);

    void close();

    boolean isConnect();
}
