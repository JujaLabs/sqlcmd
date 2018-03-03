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
        if ((newDataSets != null)) {
            if ((newDataSets.length == 0)
                    || ((newDataSets[0].row().split(",").length >= dataset.row().split(",").length))) {
                newDataSets = Arrays.copyOf(newDataSets, newDataSets.length + 1);
                newDataSets[newDataSets.length - 1] = dataset;
                tables.put(tableName, newDataSets);
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean update(String tableName, int id) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean delete(String tableName, int id) {
        for (Map.Entry<String, DataSet[]> entry : tables.entrySet()) {
            if (entry.getKey().equals(tableName)) {
                DataSet[] dataSets = entry.getValue();
                int index = 0;
                for (DataSet dataSet : dataSets) {
                    if (dataSet.row().split(",")[0].equals(String.valueOf("'" + id + "'"))) {
                        System.arraycopy(dataSets, index + 1, dataSets, index, dataSets.length - 1 - index);
                        DataSet[] newDataSet = Arrays.copyOf(dataSets, dataSets.length - 1);
                        tables.put(tableName, newDataSet);
                        return tables.get(tableName).length == newDataSet.length;
                    }
                    index++;
                }
            }
        }
        return false;
    }

    @Override
    public String[] getTableNames() {
        if (connected) {
            return (tables.keySet().size() == 0) ? new String[]{} : tables.keySet().toArray(new String[0]);
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

    public void createTable(String tableName) {
        tables.put(tableName, new DataSet[0]);
    }

    public void deleteTable(String tableName) {
        tables.remove(tableName);
    }
}
