package airtraffic.app;

import static org.apache.commons.lang3.StringUtils.repeat;
import org.beryx.textio.TextTerminal;
import airtraffic.ReportContext;
import airtraffic.reports.LiveReports;


/**
 * Provides methods for executing each of the live reports.
 *
 * @author Tony Piazza <tony@piazzaconsulting.com>
 */
public class LiveReportsApp extends AbstractReportsApp<LiveReports> {
   public static void main(String[] args) throws Exception {
      new LiveReportsApp().executeSelectedReport();
   }

   public void reportAirportMetrics(ReportContext context) {
      LiveReports impl = getBean(LiveReports.class, readStyle());
      context.setYear(readYear())
             .setAirport(readAirport("Airport"));

      TextTerminal<?> terminal = context.getTerminal();
      terminal.printf("Airport metrics for %s\n\n", context.getAirport().getName());
      terminal.println("     Total\t Cancelled\t  Diverted\t   Origins\tDestinations");
      terminal.println(repeat("-", 77));

      impl.reportAirportMetrics(context);
   }

   public void reportCarrierMetrics(ReportContext context) {
      LiveReports impl = getBean(LiveReports.class, readStyle());
      context.setYear(readYear())
             .setCarrier(readCarrier());

      TextTerminal<?> terminal = context.getTerminal();
      terminal.printf("Carrier metrics for %s\n\n", context.getCarrier().getName());
      terminal.println("     Total\t Cancelled\t  Diverted\t  Airports");
      terminal.println(repeat("-", 59));

      impl.reportCarrierMetrics(context);
   }
}