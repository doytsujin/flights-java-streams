package airtraffic;

import static org.apache.commons.lang3.StringUtils.repeat;

import org.beryx.textio.TextTerminal;

import airtraffic.iterator.IteratorAirportReports;
import airtraffic.stream.StreamAirportReports;

public class AirportReportsApp extends AbstractReportsApp implements AirportReports {
   public static void main(String[] args) throws Exception {
      new AirportReportsApp().executeSelectedReport();
   }

   @Override
   public void reportAirportMetrics(ReportContext context) {
      final String style = readStyleOption();
      context.setYear(readYear());

      TextTerminal<?> terminal = context.getTerminal();
      terminal.print("\nIATA    Airport Name                        ");
      terminal.println("Total        Cancelled %   Diverted %");
      terminal.println(repeat("-", 82));

      getImpl(style).reportAirportMetrics(context);
   }

   @Override
   public void reportAirportsForState(ReportContext context) {
      final String style = readStyleOption();
      context.setState(readState());

      TextTerminal<?> terminal = context.getTerminal();
      terminal.println("\nIATA\tAirport Name\t\t\t\t\tCity");
      terminal.println(repeat("-", 77));

      getImpl(style).reportAirportsForState(context);
   }

   @Override
   public void reportAirportsNearLocation(ReportContext context) {
      final String style = readStyleOption();
      context.setLocation(readGeoLocation())
             .setDistance(readDistanceInMiles());

      TextTerminal<?> terminal = context.getTerminal();
      terminal.println("\nIATA\tAirport Name\t\t\t\t\tState\tCity\t\tDistance");
      terminal.println(repeat("-", 89));

      getImpl(style).reportAirportsNearLocation(context);
   }

   @Override
   public void reportAirportsWithHighestCancellationRate(ReportContext context) {
      final String style = readStyleOption();
      context.setYear(readYear())
             .setLimit(readLimit(10, 1, 100));

      TextTerminal<?> terminal = context.getTerminal();
      terminal.println("\nIATA\tName\t\t\t\tRate");
      terminal.println(repeat("-", 47));

      getImpl(style).reportAirportsWithHighestCancellationRate(context);
   }

   private AirportReports getImpl(String style) {
      return "iterator".equals(style) ? 
         new IteratorAirportReports() : 
         new StreamAirportReports();
   }
}