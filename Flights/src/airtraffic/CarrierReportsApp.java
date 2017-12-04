package airtraffic;

import static org.apache.commons.lang3.StringUtils.repeat;

import org.beryx.textio.TextTerminal;

import airtraffic.iterator.IteratorCarrierReports;
import airtraffic.stream.StreamCarrierReports;

public class CarrierReportsApp extends AbstractReportsApp implements CarrierReports {
   public static void main(String[] args) throws Exception {
      new CarrierReportsApp().executeSelectedReport();
   }

   @Override
   public void reportMostCancelledFlightsByCarrier(ReportContext context) {
      final String style = readStyleOption();
      context.setYear(readYear())
             .setLimit(readLimit(10, 1, 100));

      TextTerminal<?> terminal = context.getTerminal();
      terminal.println("Carrier\t\t\t\t Count");
      terminal.println("-----------------------------------------");

      getImpl(style).reportMostCancelledFlightsByCarrier(context);
   }

   @Override
   public void reportCarrierMetrics(ReportContext context) {
      final String style = readStyleOption();
      context.setYear(readYear());

      TextTerminal<?> terminal = context.getTerminal();
      terminal.print("Code    Carrier Name                        ");
      terminal.println("Total        Cancelled %   Diverted %    Airports");
      terminal.println(repeat("-", 94));

      getImpl(style).reportCarrierMetrics(context);
   }

   @Override
   public void reportCarriersWithHighestCancellationRate(ReportContext context) {
      final String style = readStyleOption();
      context.setYear(readYear())
             .setLimit(readLimit(10, 1, 100));

      TextTerminal<?> terminal = context.getTerminal();
      terminal.println("Carrier                           Rate");
      terminal.println("---------------------------------------");

      getImpl(style).reportCarriersWithHighestCancellationRate(context);
   }

   private CarrierReports getImpl(String style) {
      return "iterator".equals(style) ? 
         new IteratorCarrierReports() : 
         new StreamCarrierReports();
   }
}