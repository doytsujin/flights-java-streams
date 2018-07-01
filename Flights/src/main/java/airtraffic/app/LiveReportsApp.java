package airtraffic.app;

import static org.apache.commons.lang3.StringUtils.repeat;
import org.beryx.textio.TextTerminal;
import airtraffic.ReportContext;
import airtraffic.reports.LiveReports;

public class LiveReportsApp extends AbstractReportsApp<LiveReports> {
   public static void main(String[] args) throws Exception {
      new LiveReportsApp().executeSelectedReport();
   }

   @Override
   protected LiveReports impl() {
      return getBean(LiveReports.class);
   }

   public void reportAirportMetrics(ReportContext context) {
      context.setYear(readYear())
             .setAirport(readAirport("Airport"));

      TextTerminal<?> terminal = context.getTerminal();
      terminal.printf("Airport metrics for %s\n\n", context.getAirport().getName());
      terminal.println("     Total\t Cancelled\t  Diverted\t   Origins\tDestinations");
      terminal.println(repeat("-", 77));

      impl().reportAirportMetrics(context);
   }

   public void reportCarrierMetrics(ReportContext context) {
      context.setYear(readYear())
             .setCarrier(readCarrier());

      TextTerminal<?> terminal = context.getTerminal();
      terminal.printf("Carrier metrics for %s\n\n", context.getCarrier().getName());
      terminal.println("     Total\t Cancelled\t  Diverted\t  Airports");
      terminal.println(repeat("-", 59));

      impl().reportCarrierMetrics(context);
   }
}