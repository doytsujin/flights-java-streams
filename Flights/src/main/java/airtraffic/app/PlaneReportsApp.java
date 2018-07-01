package airtraffic.app;

import static org.apache.commons.lang3.StringUtils.left;
import static org.apache.commons.lang3.StringUtils.repeat;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.beryx.textio.TextTerminal;
import airtraffic.ReportContext;
import airtraffic.reports.PlaneReports;
import airtraffic.reports.ReportException;


/**
 * Provides methods for executing each of the plane reports.
 *
 * @author Tony Piazza <tony@piazzaconsulting.com>
 */
public class PlaneReportsApp extends AbstractReportsApp<PlaneReports> {
   public static void main(String[] args) throws Exception {
      new PlaneReportsApp().executeSelectedReport();
   }

   public void reportTotalPlanesByManfacturer(ReportContext context) {
      PlaneReports impl = getBean(PlaneReports.class, getStyleAnnotation());
      TextTerminal<?> terminal = context.getTerminal();
      terminal.println("Manufacturer\t\t\tCount");
      terminal.println("---------------------------------------");

      ResultSet rs = impl.reportTotalPlanesByManfacturer(context);
      try {
         while(rs.next()) {
            terminal.printf("%-25s\t%5d\n", 
                            rs.getString("Manufacturer"), 
                            rs.getInt("TotalPlanes"));
         }
      } catch (SQLException e) {
         throw new ReportException(e);
      }
   }

   public void reportTotalPlanesByYear(ReportContext context) {
      PlaneReports impl = getBean(PlaneReports.class, getStyleAnnotation());
      TextTerminal<?> terminal = context.getTerminal();
      terminal.println("Year\tCount");
      terminal.println("------------------");

      ResultSet rs = impl.reportTotalPlanesByYear(context);
      try {
         while(rs.next()) {
            terminal.printf("%4d\t%5d\n", 
                            rs.getInt("Year"), 
                            rs.getInt("TotalPlanes"));
         }
      } catch (SQLException e) {
         throw new ReportException(e);
      }
   }

   public void reportTotalPlanesByAircraftType(ReportContext context) {
      PlaneReports impl = getBean(PlaneReports.class, getStyleAnnotation());
      TextTerminal<?> terminal = context.getTerminal();
      terminal.println("Aircraft Type\t\t\tCount");
      terminal.println("---------------------------------------");

      ResultSet rs = impl.reportTotalPlanesByAircraftType(context);
      try {
         while(rs.next()) {
            terminal.printf("%-25s\t%5d\n", 
                            rs.getInt("Type"), 
                            rs.getInt("TotalPlanes"));
         }
      } catch (SQLException e) {
         throw new ReportException(e);
      }
   }

   public void reportTotalPlanesByEngineType(ReportContext context) {
      PlaneReports impl = getBean(PlaneReports.class, getStyleAnnotation());
      TextTerminal<?> terminal = context.getTerminal();
      terminal.println("Engine Type\t\t\tCount");
      terminal.println("---------------------------------------");

      ResultSet rs = impl.reportTotalPlanesByEngineType(context);
      try {
         while(rs.next()) {
            terminal.printf("%-25s\t%5d\n", 
                            rs.getInt("Type"), 
                            rs.getInt("TotalPlanes"));
         }
      } catch (SQLException e) {
         throw new ReportException(e);
      }
   }

   public void reportPlanesWithMostCancellations(ReportContext context) {
      PlaneReports impl = getBean(PlaneReports.class, getStyleAnnotation());
      context.setYear(readYear())
             .setLimit(readLimit(10, 1, 100));

      TextTerminal<?> terminal = context.getTerminal();
      terminal.println("Tail #\t\tCount");
      terminal.println("-----------------------");

      ResultSet rs = impl.reportPlanesWithMostCancellations(context);
      try {
         while(rs.next()) {
            terminal.printf("%-8s\t%,6d\n",
                            rs.getInt("TailNumber"), 
                            rs.getInt("TotalCancellations"));
         }
      } catch (SQLException e) {
         throw new ReportException(e);
      }
   }

   public void reportMostFlightsByPlane(ReportContext context) {
      PlaneReports impl = getBean(PlaneReports.class, getStyleAnnotation());
      context.setYear(readYear())
             .setLimit(readLimit(10, 1, 100));

      TextTerminal<?> terminal = context.getTerminal();
      terminal.println("Tail #\t  Manufacturer\t\tModel #\t\tCount");
      terminal.println(repeat("-", 67));

      ResultSet rs = impl.reportMostFlightsByPlane(context);
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
   }

   public void reportMostFlightsByPlaneModel(ReportContext context) {
      PlaneReports impl = getBean(PlaneReports.class, getStyleAnnotation());
      context.setYear(readYear())
             .setLimit(readLimit(10, 1, 100));

      TextTerminal<?> terminal = context.getTerminal();
      terminal.println("Manufacturer\t\t\tModel #\t\t\t  Count\t\tDaily Avg");
      terminal.println(repeat("-", 82));

      ResultSet rs = impl.reportMostFlightsByPlaneModel(context);
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
   }

   public void reportTotalFlightsByPlaneManufacturer(ReportContext context) {
      PlaneReports impl = getBean(PlaneReports.class, getStyleAnnotation());
      context.setYear(readYear());

      TextTerminal<?> terminal = context.getTerminal();
      terminal.println("Manufacturer\t\t\t Count");
      terminal.println("-------------------------------------------");

      ResultSet rs = impl.reportTotalFlightsByPlaneManufacturer(context);
      try {
         while(rs.next()) {
            terminal.printf("%-25s\t%,10d\n",
                            left(rs.getString("Manufacturer"), 25),
                            rs.getInt("TotalFlights"));
         }
      } catch (SQLException e) {
         throw new ReportException(e);
      }
   }

   public void reportTotalFlightsByPlaneAgeRange(ReportContext context) {
      PlaneReports impl = getBean(PlaneReports.class, getStyleAnnotation());
      context.setYear(readYear());

      TextTerminal<?> terminal = context.getTerminal();
      terminal.println("Age Range\tCount");
      terminal.println(repeat("-", 27));

      ResultSet rs = impl.reportTotalFlightsByPlaneAgeRange(context);
      try {
         while(rs.next()) {
            terminal.printf("%-10s\t%,10d\n",
                            rs.getString("Range"),
                            rs.getInt("TotalFlights"));
         }
      } catch (SQLException e) {
         throw new ReportException(e);
      }
   }

   public void reportTotalFlightsByAircraftType(ReportContext context) {
      PlaneReports impl = getBean(PlaneReports.class, getStyleAnnotation());
      context.setYear(readYear());

      TextTerminal<?> terminal = context.getTerminal();
      terminal.println("Aircraft Type\t\t\tCount");
      terminal.println("-------------------------------------------");

      ResultSet rs = impl.reportTotalFlightsByAircraftType(context);
      try {
         while(rs.next()) {
            terminal.printf("%-25s\t%,10d\n",
                            rs.getString("Type"),
                            rs.getInt("TotalFlights"));
         }
      } catch (SQLException e) {
         throw new ReportException(e);
      }
   }

   public void reportTotalFlightsByEngineType(ReportContext context) {
      PlaneReports impl = getBean(PlaneReports.class, getStyleAnnotation());
      context.setYear(readYear());

      TextTerminal<?> terminal = context.getTerminal();
      terminal.println("Engine Type\t\t\tCount");
      terminal.println("-------------------------------------------");

      ResultSet rs = impl.reportTotalFlightsByEngineType(context);
      try {
         while(rs.next()) {
            terminal.printf("%-25s\t%,10d\n",
                            rs.getString("Type"),
                            rs.getInt("TotalFlights"));
         }
      } catch (SQLException e) {
         throw new ReportException(e);
      }
   }
}