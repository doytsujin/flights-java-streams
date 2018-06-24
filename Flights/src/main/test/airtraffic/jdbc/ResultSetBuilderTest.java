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
    private ResultSetBuilder builder = new ResultSetBuilder();

    @Test
    void testWithOneColumnAndOneRow() throws SQLException {
        final String NAME_1 = "first";
        builder.addColumn(NAME_1, Types.INTEGER);
        builder.addRow(1);
        ResultSet rs = builder.build();
        ResultSetMetaData meta = rs.getMetaData();
        assertEquals(meta.getColumnCount(), 1, "Wrong column count");
        assertEquals(meta.getColumnName(1), NAME_1, "Wrong column name");
        assertTrue(rs.next(), "Should be on first row");
        assertEquals(rs.getInt(1), 1, "Wrong column value");
        assertFalse(rs.next(), "Should be after first row");        
    }

    @Test
    void testWithOneColumnAndNoData() throws SQLException {
        final String NAME_1 = "first";
        builder.addColumn(NAME_1, Types.INTEGER);
        ResultSet rs = builder.build();
        ResultSetMetaData meta = rs.getMetaData();
        assertEquals(meta.getColumnCount(), 1, "Wrong column count");
        assertEquals(meta.getColumnName(1), NAME_1, "Wrong column name");
        assertFalse(rs.next(), "Should be no data");
    }

    @Test
    void testWithNoColumnsAndNoData() {
        assertThrows(IllegalStateException.class,
                     () -> builder.build(),
                     "Should not build");
    }
}