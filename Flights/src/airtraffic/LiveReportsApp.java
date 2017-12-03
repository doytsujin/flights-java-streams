package airtraffic;

import static org.apache.commons.lang3.StringUtils.repeat;

import org.beryx.textio.TextTerminal;

import airtraffic.iterator.IteratorLiveReports;
import airtraffic.stream.StreamLiveReports;

public class LiveReportsApp extends AbstractReportsApp implements LiveReports {
   @Override
   public void reportAirportMetrics(ReportContext context) {
      final String style = readStyleOption();
      context.setYear(readYear())
             .setAirport(readAirport("Airport"));

      TextTerminal<?> terminal = context.getTerminal();
      terminal.printf("Airport metrics for %s\n\n", context.getAirport().getName());
      terminal.println("     Total\t Cancelled\t  Diverted\t   Origins\tDestinations");
      terminal.println(repeat("-", 77));

      getImpl(style).reportAirportMetrics(context);
   }

   @Override
   public void reportCarrierMetrics(ReportContext context) {
      final String style = readStyleOption();
      context.setYear(readYear())
             .setCarrier(readCarrier());

      TextTerminal<?> terminal = context.getTerminal();
      terminal.printf("Carrier metrics for %s\n\n", context.getCarrier().getName());
      terminal.println("     Total\t Cancelled\t  Diverted\t  Airports");
      terminal.println(repeat("-", 59));

      getImpl(style).reportCarrierMetrics(context);
   }

   private LiveReports getImpl(String style) {
      return "iterator".equals(style) ? 
               new IteratorLiveReports() : 
               new StreamLiveReports();
   }
}