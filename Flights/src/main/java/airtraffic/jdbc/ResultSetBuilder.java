package airtraffic.jdbc;

import static org.apache.commons.lang3.StringUtils.isBlank;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import javax.sql.rowset.RowSetMetaDataImpl;
import org.apache.commons.lang3.tuple.Pair;
import airtraffic.reports.ReportException;


/**
 * Builds a ResultSet from a List of Object arrays.
 * 
 * @author Tony Piazza <tony@piazzaconsulting.com>
 */
public class ResultSetBuilder {
   private List<Object[]> data = new ArrayList<>();
   private List<Pair<String, Integer>> columns = new ArrayList<>();

   public ResultSetBuilder addColumn(String name, int type) {
      if (isBlank(name)) {
         throw new IllegalArgumentException("Blank column name");
      }
      for(Pair<String, Integer> column : columns) {
         if (name.equalsIgnoreCase(column.getLeft())) {
            throw new IllegalArgumentException("Duplicate column: " + column);
         }
      }
      columns.add(Pair.of(name, type));
      return this;
   }

   public ResultSetBuilder addRow(Object... values) {
      if (columns.isEmpty()) {
         throw new IllegalStateException("No columns exist");
      }
      if (values.length != columns.size()) {
         throw new IllegalArgumentException("Invalid column count");
      }
      data.add(values);
      return this;
   }

   public ResultSet build() {
      if (columns.isEmpty()) {
         throw new IllegalStateException("No columns exist");
      }
      RowSetMetaDataImpl meta = new RowSetMetaDataImpl();
      try {
         meta.setColumnCount(columns.size());
         int i = 1;
         for (Pair<String, Integer> column : columns) {
            meta.setColumnLabel(i, column.getLeft());
            meta.setColumnName(i, column.getLeft());
            meta.setColumnType(i, column.getRight());
            i++;
         }
      } catch (SQLException e) {
         throw new ReportException(e);
      }
      return new SimpleResultSet(meta, data);
   }
}