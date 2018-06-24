package airtraffic.app;

import static org.apache.commons.lang3.StringUtils.left;
import static org.apache.commons.lang3.StringUtils.repeat;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.beryx.textio.TextTerminal;
import airtraffic.ReportContext;
import airtraffic.iterator.IteratorCarrierReports;
import airtraffic.reports.CarrierReports;
import airtraffic.reports.ReportException;
import airtraffic.stream.StreamCarrierReports;

public class CarrierReportsApp extends AbstractReportsApp implements CarrierReports {
   public static void main(String[] args) throws Exception {
      new CarrierReportsApp().executeSelectedReport();
   }

   @Override
   public ResultSet reportMostCancelledFlightsByCarrier(ReportContext context) {
      final String style = readStyleOption();
      context.setYear(readYear())
             .setLimit(readLimit(10, 1, 100));

      TextTerminal<?> terminal = context.getTerminal();
      terminal.println("Carrier\t\t\t\t Count");
      terminal.println("-----------------------------------------");

      ResultSet rs = getImpl(style).reportMostCancelledFlightsByCarrier(context);
      try {
         while(rs.next()) {
            terminal.printf("%-24s\t%,8d\n", 
                            left(rs.getString("Name"), 24), 
                            rs.getInt("TotalFlights"));
         }
      } catch (SQLException e) {
         throw new ReportException(e);
      }

      return rs;
   }

   @Override
   public ResultSet reportCarrierMetrics(ReportContext context) {
      final String style = readStyleOption();
      context.setYear(readYear());

      TextTerminal<?> terminal = context.getTerminal();
      terminal.print("Code    Carrier Name                        ");
      terminal.println("Total        Cancelled %   Diverted %    Airports");
      terminal.println(repeat("-", 94));

      ResultSet rs = getImpl(style).reportCarrierMetrics(context);
      try {
         while(rs.next()) {
            terminal.printf(" %2s     %-30s     %,9d    %6.1f        %6.1f         %,5d\n",
                            rs.getString("Code"),
                            left(rs.getString("Name"), 30),
                            rs.getInt("TotalFlights"),
                            rs.getDouble("CancellationRate") * 100.0,
                            rs.getDouble("DiversionRate") * 100.0,
                            rs.getDouble("TotalAirports"));
         }
      } catch (SQLException e) {
         throw new ReportException(e);
      }

      return rs;
   }

   @Override
   public ResultSet reportCarriersWithHighestCancellationRate(ReportContext context) {
      final String style = readStyleOption();
      context.setYear(readYear())
             .setLimit(readLimit(10, 1, 100));

      TextTerminal<?> terminal = context.getTerminal();
      terminal.println("Carrier                           Rate");
      terminal.println("---------------------------------------");

      ResultSet rs = getImpl(style).reportCarriersWithHighestCancellationRate(context);
      try {
         while(rs.next()) {
            terminal.printf("%-30s\t%6.1f\n", 
                            left(rs.getString("Name"), 30), 
                            rs.getDouble("CancellationRate") * 100.0);
         }
      } catch (SQLException e) {
         throw new ReportException(e);
      }

      return rs;
   }

   private CarrierReports getImpl(String style) {
      return "iterator".equals(style) ? 
         new IteratorCarrierReports() : 
         new StreamCarrierReports();
   }
}