package airtraffic.app;

import static org.apache.commons.lang3.StringUtils.left;
import static org.apache.commons.lang3.StringUtils.repeat;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.beryx.textio.TextTerminal;
import airtraffic.ReportContext;
import airtraffic.reports.CarrierReports;
import airtraffic.reports.ReportException;


/**
 * Provides methods for executing each of the carrier reports.
 *
 * @author Tony Piazza <tony@piazzaconsulting.com>
 */
public class CarrierReportsApp extends AbstractReportsApp<CarrierReports> {
   public static void main(String[] args) throws Exception {
      new CarrierReportsApp().executeSelectedReport();
   }

   public void reportMostCancelledFlightsByCarrier(ReportContext context) {
      CarrierReports impl = getBean(CarrierReports.class, readStyle());
      context.setYear(readYear())
             .setLimit(readLimit(10, 1, 100));

      TextTerminal<?> terminal = context.getTerminal();
      terminal.println("Carrier\t\t\t\t Count");
      terminal.println("-----------------------------------------");

      try (ResultSet rs = impl.reportMostCancelledFlightsByCarrier(context)) {
         while(rs.next()) {
            terminal.printf("%-24s\t%,8d\n", 
                            left(rs.getString("Name"), 24), 
                            rs.getInt("TotalFlights"));
         }
      } catch (SQLException e) {
         throw new ReportException(e);
      }
   }

   public void reportCarrierMetrics(ReportContext context) {
      CarrierReports impl = getBean(CarrierReports.class, readStyle());
      context.setYear(readYear());

      TextTerminal<?> terminal = context.getTerminal();
      terminal.print("Code    Carrier Name                        ");
      terminal.println("Total        Cancelled %   Diverted %    Airports");
      terminal.println(repeat("-", 94));

      try (ResultSet rs = impl.reportCarrierMetrics(context)) {
         while(rs.next()) {
            terminal.printf(" %2s     %-30s     %,9d    %6.1f        %6.1f         %,5d\n",
                            rs.getString("Code"),
                            left(rs.getString("Name"), 30),
                            rs.getInt("TotalFlights"),
                            rs.getDouble("CancellationRate") * 100.0,
                            rs.getDouble("DiversionRate") * 100.0,
                            rs.getInt("TotalAirports"));
         }
      } catch (SQLException e) {
         throw new ReportException(e);
      }
   }

   public void reportCarriersWithHighestCancellationRate(ReportContext context) {
      CarrierReports impl = getBean(CarrierReports.class, readStyle());
      context.setYear(readYear())
             .setLimit(readLimit(10, 1, 100));

      TextTerminal<?> terminal = context.getTerminal();
      terminal.println("Carrier                           Rate");
      terminal.println("---------------------------------------");

      try (ResultSet rs = impl.reportCarriersWithHighestCancellationRate(context)) {
         while(rs.next()) {
            terminal.printf("%-30s\t%6.1f\n", 
                            left(rs.getString("Name"), 30), 
                            rs.getDouble("CancellationRate") * 100.0);
         }
      } catch (SQLException e) {
         throw new ReportException(e);
      }
   }
}