package airtraffic.app;

import static org.apache.commons.lang3.StringUtils.left;
import static org.apache.commons.lang3.StringUtils.repeat;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.beryx.textio.TextTerminal;
import airtraffic.ReportContext;
import airtraffic.reports.PlaneReports;
import airtraffic.reports.ReportException;
import airtraffic.reports.iterator.IteratorPlaneReports;
import airtraffic.reports.stream.StreamPlaneReports;

public class PlaneReportsApp extends AbstractReportsApp implements PlaneReports {
   public static void main(String[] args) throws Exception {
      new PlaneReportsApp().executeSelectedReport();
   }

   @Override
   public ResultSet reportTotalPlanesByManfacturer(ReportContext context) {
      final String style = readStyleOption();

      TextTerminal<?> terminal = context.getTerminal();
      terminal.println("Manufacturer\t\t\tCount");
      terminal.println("---------------------------------------");

      ResultSet rs = getImpl(style).reportTotalPlanesByManfacturer(context);
      try {
         while(rs.next()) {
            terminal.printf("%-25s\t%5d\n", 
                            rs.getString("Manufacturer"), 
                            rs.getInt("TotalPlanes"));
         }
      } catch (SQLException e) {
         throw new ReportException(e);
      }

      return rs;
   }

   @Override
   public ResultSet reportTotalPlanesByYear(ReportContext context) {
      final String style = readStyleOption();

      TextTerminal<?> terminal = context.getTerminal();
      terminal.println("Year\tCount");
      terminal.println("------------------");

      ResultSet rs = getImpl(style).reportTotalPlanesByYear(context);
      try {
         while(rs.next()) {
            terminal.printf("%4d\t%5d\n", 
                            rs.getInt("Year"), 
                            rs.getInt("TotalPlanes"));
         }
      } catch (SQLException e) {
         throw new ReportException(e);
      }

      return rs;
   }

   @Override
   public ResultSet reportTotalPlanesByAircraftType(ReportContext context) {
      final String style = readStyleOption();

      TextTerminal<?> terminal = context.getTerminal();
      terminal.println("Aircraft Type\t\t\tCount");
      terminal.println("---------------------------------------");

      ResultSet rs = getImpl(style).reportTotalPlanesByAircraftType(context);
      try {
         while(rs.next()) {
            terminal.printf("%-25s\t%5d\n", 
                            rs.getInt("Type"), 
                            rs.getInt("TotalPlanes"));
         }
      } catch (SQLException e) {
         throw new ReportException(e);
      }

      return rs;
   }

   @Override
   public ResultSet reportTotalPlanesByEngineType(ReportContext context) {
      final String style = readStyleOption();

      TextTerminal<?> terminal = context.getTerminal();
      terminal.println("Engine Type\t\t\tCount");
      terminal.println("---------------------------------------");

      ResultSet rs = getImpl(style).reportTotalPlanesByEngineType(context);
      try {
         while(rs.next()) {
            terminal.printf("%-25s\t%5d\n", 
                            rs.getInt("Type"), 
                            rs.getInt("TotalPlanes"));
         }
      } catch (SQLException e) {
         throw new ReportException(e);
      }

      return rs;
   }

   @Override
   public ResultSet reportPlanesWithMostCancellations(ReportContext context) {
      final String style = readStyleOption();

      context.setYear(readYear())
             .setLimit(readLimit(10, 1, 100));

      TextTerminal<?> terminal = context.getTerminal();
      terminal.println("Tail #\t\tCount");
      terminal.println("-----------------------");

      ResultSet rs = getImpl(style).reportPlanesWithMostCancellations(context);
      try {
         while(rs.next()) {
            terminal.printf("%-8s\t%,6d\n",
                            rs.getInt("TailNumber"), 
                            rs.getInt("TotalCancellations"));
         }
      } catch (SQLException e) {
         throw new ReportException(e);
      }

      return rs;
   }

   @Override
   public ResultSet reportMostFlightsByPlane(ReportContext context) {
      final String style = readStyleOption();

      context.setYear(readYear())
             .setLimit(readLimit(10, 1, 100));

      TextTerminal<?> terminal = context.getTerminal();
      terminal.println("Tail #\t  Manufacturer\t\tModel #\t\tCount");
      terminal.println(repeat("-", 67));

      ResultSet rs = getImpl(style).reportMostFlightsByPlane(context);
      try {
         while(rs.next()) {
            terminal.printf("%-8s  %-20s  %-10s  %,10d\n",
                            rs.getString("TailNumber"),
                            left(rs.getString("Manufacturer"), 20),
                            left(rs.getString("ModelNumber"), 10),
                            rs.getInt("TotalFlights"));
         }
      } catch (SQLException e) {
         throw new ReportException(e);
      }

      return rs;
   }

   @Override
   public ResultSet reportMostFlightsByPlaneModel(ReportContext context) {
      final String style = readStyleOption();
      context.setYear(readYear())
             .setLimit(readLimit(10, 1, 100));

      TextTerminal<?> terminal = context.getTerminal();
      terminal.println("Manufacturer\t\t\tModel #\t\t\t  Count\t\tDaily Avg");
      terminal.println(repeat("-", 82));

      ResultSet rs = getImpl(style).reportMostFlightsByPlaneModel(context);
      try {
         while(rs.next()) {
            terminal.printf("%-25s\t%-20s\t%,10d\t%8.1f",
                            left(rs.getString("Manufacturer"), 25),
                            left(rs.getString("ModelNumber"), 20),
                            rs.getInt("TotalFlights"),
                            rs.getFloat("DailyAverage"));
         }
      } catch (SQLException e) {
         throw new ReportException(e);
      }

      return rs;
   }

   @Override
   public ResultSet reportTotalFlightsByPlaneManufacturer(ReportContext context) {
      final String style = readStyleOption();
      context.setYear(readYear());

      TextTerminal<?> terminal = context.getTerminal();
      terminal.println("Manufacturer\t\t\t Count");
      terminal.println("-------------------------------------------");

      ResultSet rs = getImpl(style).reportTotalFlightsByPlaneManufacturer(context);
      try {
         while(rs.next()) {
            terminal.printf("%-25s\t%,10d\n",
                            left(rs.getString("Manufacturer"), 25),
                            rs.getInt("TotalFlights"));
         }
      } catch (SQLException e) {
         throw new ReportException(e);
      }

      return rs;
   }

   @Override
   public ResultSet reportTotalFlightsByPlaneAgeRange(ReportContext context) {
      final String style = readStyleOption();
      context.setYear(readYear());

      TextTerminal<?> terminal = context.getTerminal();
      terminal.println("Age Range\tCount");
      terminal.println(repeat("-", 27));

      ResultSet rs = getImpl(style).reportTotalFlightsByPlaneAgeRange(context);
      try {
         while(rs.next()) {
            terminal.printf("%-10s\t%,10d\n",
                            rs.getString("Range"),
                            rs.getInt("TotalFlights"));
         }
      } catch (SQLException e) {
         throw new ReportException(e);
      }

      return rs;
   }

   @Override
   public ResultSet reportTotalFlightsByAircraftType(ReportContext context) {
      final String style = readStyleOption();
      context.setYear(readYear());

      TextTerminal<?> terminal = context.getTerminal();
      terminal.println("Aircraft Type\t\t\tCount");
      terminal.println("-------------------------------------------");

      ResultSet rs = getImpl(style).reportTotalFlightsByAircraftType(context);
      try {
         while(rs.next()) {
            terminal.printf("%-25s\t%,10d\n",
                            rs.getString("Type"),
                            rs.getInt("TotalFlights"));
         }
      } catch (SQLException e) {
         throw new ReportException(e);
      }

      return rs;
   }

   @Override
   public ResultSet reportTotalFlightsByEngineType(ReportContext context) {
      final String style = readStyleOption();
      context.setYear(readYear());

      TextTerminal<?> terminal = context.getTerminal();
      terminal.println("Engine Type\t\t\tCount");
      terminal.println("-------------------------------------------");

      ResultSet rs = getImpl(style).reportTotalFlightsByEngineType(context);
      try {
         while(rs.next()) {
            terminal.printf("%-25s\t%,10d\n",
                            rs.getString("Type"),
                            rs.getInt("TotalFlights"));
         }
      } catch (SQLException e) {
         throw new ReportException(e);
      }

      return rs;
   }

   private PlaneReports getImpl(String style) {
      return "iterator".equals(style) ? 
         new IteratorPlaneReports() : 
         new StreamPlaneReports();
   }
}