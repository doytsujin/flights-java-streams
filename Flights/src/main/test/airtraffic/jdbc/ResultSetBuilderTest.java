package airtraffic.jdbc;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Types;
import org.junit.jupiter.api.Test;


class ResultSetBuilderTest {
    private static final String NAME_1 = "first";
    private final ResultSetBuilder builder = new ResultSetBuilder();

    @Test
    void buildWithOneColumnAndOneRow() throws SQLException {
        builder.addColumn(NAME_1, Types.INTEGER);
        builder.addRow(1);
        ResultSet rs = builder.build();
        ResultSetMetaData meta = rs.getMetaData();
        assertEquals(meta.getColumnCount(), 1, "Wrong column count");
        assertEquals(meta.getColumnName(1), NAME_1, "Wrong column name");
        assertEquals(meta.getColumnLabel(1), NAME_1, "Wrong column label");
        assertTrue(rs.next(), "Should be on first row");
        assertEquals(rs.getInt(1), 1, "Wrong column value");
        assertEquals(rs.getInt(NAME_1), 1, "Wrong column value");
        assertFalse(rs.next(), "Should be after first row");        
    }

    @Test
    void buildWithOneColumnAndNoRows() throws SQLException {
        builder.addColumn(NAME_1, Types.INTEGER);
        ResultSet rs = builder.build();
        ResultSetMetaData meta = rs.getMetaData();
        assertEquals(meta.getColumnCount(), 1, "Wrong column count");
        assertEquals(meta.getColumnName(1), NAME_1, "Wrong column name");
        assertEquals(meta.getColumnLabel(1), NAME_1, "Wrong column label");
        assertFalse(rs.next(), "Should be no data");
    }

    @Test
    void addRowWithNoColumns() {
        assertThrows(IllegalStateException.class,
                     () -> builder.addRow(1),
                     "Should not add row");
    }

    @Test
    void addEmptyRowWithOneColumn() {
        builder.addColumn(NAME_1, Types.INTEGER);
        assertThrows(IllegalArgumentException.class,
                     () -> builder.addRow(),
                     "Should not add row");
    }

    @Test
    void addWithDuplicateColumn() {
       builder.addColumn(NAME_1, Types.INTEGER);
       assertThrows(IllegalArgumentException.class,
                    () -> builder.addColumn(NAME_1, Types.INTEGER),
                    "Should not add row");
    }

    @Test
    void buildWithNoColumnsAndNoRows() {
        assertThrows(IllegalStateException.class,
                     () -> builder.build(),
                     "Should not build");
    }
}