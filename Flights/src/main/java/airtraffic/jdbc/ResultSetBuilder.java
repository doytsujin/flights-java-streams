package airtraffic.jdbc;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import javax.sql.rowset.RowSetMetaDataImpl;
import airtraffic.reports.ReportException;


/**
 * Builds a ResultSet from a List of Object arrays.
 * 
 * @author Tony Piazza <tony@piazzaconsulting.com>
 */
public class ResultSetBuilder {
    private RowSetMetaDataImpl meta = new RowSetMetaDataImpl();
    private List<Object[]> data = new ArrayList<>();
    private int columnIndex = 1;

    public ResultSetBuilder addColumn(String name, int type) {
        try {
            meta.setColumnCount(columnIndex);
            meta.setColumnLabel(columnIndex, name);
            meta.setColumnName(columnIndex, name);
            meta.setColumnType(columnIndex, type);
        } catch (SQLException e) {
            throw new ReportException(e);
        }
        columnIndex++;
        return this;
    }

    public ResultSetBuilder addRow(Object... values) {
        if (columnIndex == 1) {
            throw new IllegalStateException("No columns exist");
        }
        try {
            if (values.length != meta.getColumnCount()) {
                throw new IllegalArgumentException("Invalid column count");
            }
        } catch (SQLException e) {
            throw new ReportException(e);
        }
        data.add(values);
        return this;
    }

    public ResultSet build() {
        if (columnIndex == 1) {
            throw new IllegalStateException("No columns exist");
        }
        return new SimpleResultSet(meta, data);
    }
}
