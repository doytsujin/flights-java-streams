package airtraffic.app;

import static org.apache.commons.lang3.StringUtils.left;
import static org.apache.commons.lang3.StringUtils.repeat;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.beryx.textio.TextTerminal;
import airtraffic.ReportContext;
import airtraffic.reports.AirportReports;
import airtraffic.reports.ReportException;


/**
 * Provides methods for executing each of the airport reports.
 *
 * @author Tony Piazza <tony@piazzaconsulting.com>
 */
public class AirportReportsApp extends AbstractReportsApp {
   public static void main(String[] args) throws Exception {
      new AirportReportsApp().executeSelectedReport();
   }

   public void reportAirportMetrics(ReportContext context) {
      AirportReports impl = getBean(AirportReports.class, readStyle());
      context.setYear(readYear());

      TextTerminal<?> terminal = context.getTerminal();
      terminal.print("\nIATA    Airport Name                        ");
      terminal.println("Total        Cancelled %   Diverted %");
      terminal.println(repeat("-", 82));

      try (ResultSet rs = impl.reportAirportMetrics(context)) {
         while (rs.next()) {
            terminal.printf("%3s     %-30s     %,9d    %6.1f        %6.1f\n", 
                            rs.getString("IATA"),
                            left(rs.getString("Name"), 29), 
                            rs.getInt("TotalFlights"),
                            rs.getDouble("CancellationRate") * 100.0, 
                            rs.getDouble("DiversionRate") * 100.0);
         }
      } catch (SQLException e) {
         throw new ReportException(e);
      }
   }

   public void reportAirportsForState(ReportContext context) throws ReportException {
      AirportReports impl = getBean(AirportReports.class, readStyle());
      context.setState(readState());

      TextTerminal<?> terminal = context.getTerminal();
      terminal.println("\nIATA\tAirport Name\t\t\t\t\tCity");
      terminal.println(repeat("-", 77));

      try (ResultSet rs = impl.reportAirportsForState(context)) {
         while (rs.next()) {
            terminal.printf("%3s\t%-40s\t%-20s\n", 
                            rs.getString("IATA"), 
                            rs.getString("Name"),
                            rs.getString("City"));
         }
      } catch (SQLException e) {
         throw new ReportException(e);
      }
   }

   public void reportAirportsNearLocation(ReportContext context) throws ReportException {
      AirportReports impl = getBean(AirportReports.class, readStyle());
      context.setLocation(readGeoLocation()).setDistance(readDistanceInMiles());

      TextTerminal<?> terminal = context.getTerminal();
      terminal.println("\nIATA\tAirport Name\t\t\t\t\tState\tCity\t\tDistance");
      terminal.println(repeat("-", 89));

      try (ResultSet rs = impl.reportAirportsNearLocation(context)) {
         while (rs.next()) {
            terminal.printf("%3s\t%-40s\t %2s\t%-15s    %,4.0f\n", 
                            rs.getString("IATA"),
                            rs.getString("Name"), 
                            rs.getString("State"), 
                            left(rs.getString("City"), 15),
                            rs.getString("Distance"));
         }
      } catch (SQLException e) {
         throw new ReportException(e);
      }
   }

   public void reportAirportsWithHighestCancellationRate(ReportContext context)
         throws ReportException {
      AirportReports impl = getBean(AirportReports.class, readStyle());
      context.setYear(readYear()).setLimit(readLimit(10, 1, 100));

      TextTerminal<?> terminal = context.getTerminal();
      terminal.println("\nIATA\tName\t\t\t\tRate");
      terminal.println(repeat("-", 47));

      try (ResultSet rs = impl.reportAirportsWithHighestCancellationRate(context)) {
         while (rs.next()) {
            terminal.printf("%3s\t%-30s\t%6.1f\n", 
                            rs.getString("IATA"), 
                            rs.getString("Name"),
                            rs.getDouble("CancellationRate") * 100);
         }
      } catch (SQLException e) {
         throw new ReportException(e);
      }
   }
}