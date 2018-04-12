package juja.sqlcmd;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class InMemoryDatabaseManager implements DatabaseManager {
    private static final String NAME_DB_TEST = "sqlcmd_test";
    private static final String LOGIN_FOR_TEST = "sqlcmd";
    private static final String PASSWORD_FOR_TEST = "sqlcmd";

    private boolean connected = false;
    private HashMap<String, DataSet[]> tables = new HashMap<>();


    @Override
    public boolean connect(String database, String user, String password) {
        connected = NAME_DB_TEST.equals(database) && LOGIN_FOR_TEST.equals(user) && PASSWORD_FOR_TEST.equals(password);
        return connected;
    }

    @Override
    public boolean insert(String tableName, DataSet dataset) {
        DataSet[] newDataSets = tables.get(tableName);
        if (newDataSets != null && isDataSetsValid(dataset, newDataSets)) {
            newDataSets = Arrays.copyOf(newDataSets, newDataSets.length + 1);
            newDataSets[newDataSets.length - 1] = dataset;
            tables.put(tableName, newDataSets);
            return true;
        }
        return false;
    }

    private boolean isDataSetsValid(DataSet dataset, DataSet[] newDataSets) {
        return newDataSets.length == 0 || newDataSets[0].length() >= dataset.length();
    }

    @Override
    public boolean update(String tableName, int id) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean delete(String tableName, int id) {
        if (tables.containsKey(tableName)) {
            DataSet[] dataSets = tables.get(tableName);
            for (int index = 0; index < dataSets.length; index++) {
                String currentId = dataSets[index].row().split(",")[0];
                if (currentId.equals("'" + id + "'")) {
                    System.arraycopy(dataSets, index + 1, dataSets, index, dataSets.length - 1 - index);
                    DataSet[] newDataSet = Arrays.copyOf(dataSets, dataSets.length - 1);
                    tables.put(tableName, newDataSet);
                    return tables.get(tableName).length == newDataSet.length;
                }
            }
        }
        return false;
    }

    @Override
    public String[] getTableNames() {
        if (connected) {
            return (tables.isEmpty()) ? new String[]{} : tables.keySet().toArray(new String[0]);
        } else {
            return new String[]{};
        }
    }

    @Override
    public DataSet[] getTableData(String tableName) {
        DataSet[] result = tables.get(tableName);
        return (result == null) ? new DataSet[0] : result;
    }

    @Override
    public void close() {
        connected = false;
    }

    @Override
    public boolean isConnect() {
        return true;
    }

    void createTable(String tableName) {
        tables.put(tableName, new DataSet[0]);
    }

    void deleteTable(String tableName) {
        tables.remove(tableName);
    }
}
