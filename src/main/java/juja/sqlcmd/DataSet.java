package juja.sqlcmd;

import java.util.Arrays;

public class DataSet {

    private String[] row;

    DataSet(int length) {
        if (length < 0) {
            String exceptionMessage = String.format("DataSet length = %s, but should be greater or equal to 0.", length);
            throw new IllegalArgumentException(exceptionMessage);
        }
        this.row = new String[length];
    }

    public void add(int columnIndex, String value) {
        if ((columnIndex >= 0) && (columnIndex < row.length)) {
            this.row[columnIndex] = value;
        } else {
            String exceptionMessage = String.format("Column index = %s, but should be between 0 and %s.",
                    columnIndex, row.length - 1);
            throw new IllegalArgumentException(exceptionMessage);
        }
    }

    public String toString() {
        StringBuilder result = new StringBuilder();
        for (String field : row) {
            result.append(field).append(",");
        }
        return result.toString().substring(0,result.length()-1);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DataSet dataSet = (DataSet) o;
        return Arrays.equals(row, dataSet.row);
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(row);
    }
}
