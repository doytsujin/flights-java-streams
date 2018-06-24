package airtraffic.app;

import static org.apache.commons.lang3.StringUtils.repeat;

import org.beryx.textio.TextTerminal;
import airtraffic.ReportContext;
import airtraffic.iterator.IteratorFlightReports;
import airtraffic.reports.FlightReports;
import airtraffic.stream.StreamFlightReports;

public class FlightReportsApp extends AbstractReportsApp implements FlightReports {
   public static void main(String[] args) throws Exception {
      new FlightReportsApp().executeSelectedReport();
   }

   @Override
   public void reportTotalFlightsFromOrigin(ReportContext context) {
      final String style = readStyleOption();
      context.setYear(readYear())
             .setOrigin(readAirport("Origin"));

      getImpl(style).reportTotalFlightsFromOrigin(context);
   }

   @Override
   public void reportTotalFlightsToDestination(ReportContext context) {
      getImpl(readStyleOption()).reportTotalFlightsToDestination(context);
   }

   @Override
   public void reportTotalFlightsFromOriginToDestination(ReportContext context) {
      getImpl(readStyleOption()).reportTotalFlightsFromOrigin(context);
   }

   @Override
   public void reportTopFlightsByOrigin(ReportContext context) {
      final String style = readStyleOption();
      context.setYear(readYear())
             .setLimit(readLimit(10, 1, 100));

      TextTerminal<?> terminal = context.getTerminal();
      terminal.println("\nOrigin\t\tCount");
      terminal.println(repeat("-", 27));

      getImpl(style).reportTopFlightsByOrigin(context);
   }

   @Override
   public void reportTopDestinationsFromOrigin(ReportContext context) {
      final String style = readStyleOption();
      context.setYear(readYear())
             .setOrigin(readAirport("Origin"))
             .setLimit(readLimit(10, 1, 100));

      TextTerminal<?> terminal = context.getTerminal();
      terminal.printf("Top destinations from %s\n\n", 
                      context.getOrigin().getName());
      terminal.println("Destination\t   Count");
      terminal.println(repeat("-", 30));

      getImpl(style).reportTopDestinationsFromOrigin(context);
   }

   @Override
   public void reportMostPopularRoutes(ReportContext context) {
      final String style = readStyleOption();
      context.setYear(readYear())
             .setLimit(readLimit(10, 1, 100));

      TextTerminal<?> terminal = context.getTerminal();
      terminal.println("Route\t\t    Count");
      terminal.println(repeat("-", 27));

      getImpl(style).reportMostPopularRoutes(context);
   }

   @Override
   public void reportWorstAverageDepartureDelayByOrigin(ReportContext context) {
      final String style = readStyleOption();
      context.setYear(readYear())
             .setLimit(readLimit(10, 1, 100));

      TextTerminal<?> terminal = context.getTerminal();
      terminal.println("Origin\tDelay (min)");
      terminal.println(repeat("-", 22));

      getImpl(style).reportWorstAverageDepartureDelayByOrigin(context);
   }

   @Override
   public void reportWorstAverageArrivalDelayByDestination(ReportContext context) {
      final String style = readStyleOption();
      context.setYear(readYear())
             .setLimit(readLimit(10, 1, 100));

      TextTerminal<?> terminal = context.getTerminal();
      terminal.println("Destination\tDelay (min)");
      terminal.println(repeat("-", 28));

      getImpl(style).reportWorstAverageArrivalDelayByDestination(context);
   }

   @Override
   public void reportMostCancelledFlightsByOrigin(ReportContext context) {
      final String style = readStyleOption();
      context.setYear(readYear())
             .setLimit(readLimit(10, 1, 100));

      TextTerminal<?> terminal = context.getTerminal();
      terminal.println("Origin\t\t  Count");
      terminal.println(repeat("-", 27));

      getImpl(style).reportMostCancelledFlightsByOrigin(context);
   }

   @Override
   public void reportTotalFlightsByOriginState(ReportContext context) {
      final String style = readStyleOption();
      context.setYear(readYear())
             .setLimit(readLimit(10, 1, 100));

      TextTerminal<?> terminal = context.getTerminal();
      terminal.println("State\t  Count");
      terminal.println(repeat("-", 19));

      getImpl(style).reportTotalFlightsByOriginState(context);
   }

   @Override
   public void reportTotalFlightsByDestinationState(ReportContext context) {
      final String style = readStyleOption();
      context.setYear(readYear())
             .setLimit(readLimit(10, 1, 100));

      TextTerminal<?> terminal = context.getTerminal();
      terminal.println("State\tCount");
      terminal.println(repeat("-", 19));

      getImpl(style).reportTotalFlightsByDestinationState(context);
   }

   @Override
   public void reportLongestFlights(ReportContext context) {
      final String style = readStyleOption();
      context.setYear(readYear())
             .setLimit(readLimit(10, 1, 100));

      TextTerminal<?> terminal = context.getTerminal();
      terminal.println("Flight #     Date\tCarrier\tOrigin\tDestination\tDistance");
      terminal.println(repeat("-", 65));

      getImpl(style).reportLongestFlights(context);
   }

   @Override
   public void reportShortestFlights(ReportContext context) {
      final String style = readStyleOption();
      context.setYear(readYear())
             .setLimit(readLimit(10, 1, 100));

      TextTerminal<?> terminal = context.getTerminal();
      terminal.println("Flight #     Date\tCarrier\tOrigin\tDestination\tDistance");
      terminal.println(repeat("-", 65));

      getImpl(style).reportShortestFlights(context);
   }

   @Override
   public void reportTotalFlightsByDistanceRange(ReportContext context) {
      final String style = readStyleOption();
      context.setYear(readYear());

      TextTerminal<?> terminal = context.getTerminal();
      terminal.println("Range\t\tCount");
      terminal.println(repeat("-", 27));

      getImpl(style).reportTotalFlightsByDistanceRange(context);
   }

   @Override
   public void reportDaysWithLeastCancellations(ReportContext context) {
      final String style = readStyleOption();
      context.setYear(readYear())
             .setLimit(readLimit(10, 1, 100));

      TextTerminal<?> terminal = context.getTerminal();
      terminal.println("Date\t\tCount");
      terminal.println(repeat("-", 24));

      getImpl(style).reportDaysWithLeastCancellations(context);
   }

   @Override
   public void reportDaysWithMostCancellations(ReportContext context) {
      final String style = readStyleOption();
      context.setYear(readYear())
             .setLimit(readLimit(10, 1, 100));

      TextTerminal<?> terminal = context.getTerminal();
      terminal.println("Date\t\tCount");
      terminal.println(repeat("-", 24));

      getImpl(style).reportDaysWithMostCancellations(context);
   }

   @Override
   public void reportTotalMonthlyFlights(ReportContext context) {
      final String style = readStyleOption();
      context.setYear(readYear());

      TextTerminal<?> terminal = context.getTerminal();
      terminal.println("Month\t\tCount");
      terminal.println(repeat("-", 27));

      getImpl(style).reportTotalMonthlyFlights(context);
   }

   @Override
   public void reportTotalDailyFlights(ReportContext context) {
      final String style = readStyleOption();
      context.setYear(readYear());

      TextTerminal<?> terminal = context.getTerminal();
      terminal.println("Day\t\t   Count");
      terminal.println(repeat("-", 27));

      getImpl(style).reportTotalDailyFlights(context);
   }

   @Override
   public void reportTotalFlightsByDayOfWeek(ReportContext context) {
      final String style = readStyleOption();
      context.setYear(readYear());

      TextTerminal<?> terminal = context.getTerminal();
      terminal.println("Day of Week\t   Count");
      terminal.println(repeat("-", 27));

      getImpl(style).reportTotalFlightsByDayOfWeek(context);
   }

   @Override
   public void reportMostFlightsByDay(ReportContext context) {
      final String style = readStyleOption();
      context.setYear(readYear())
             .setLimit(readLimit(10, 1, 100));

      TextTerminal<?> terminal = context.getTerminal();
      terminal.println("Day\t\t   Count");
      terminal.println(repeat("-", 27));

      getImpl(style).reportMostFlightsByDay(context);
   }

   @Override
   public void reportLeastFlightsByDay(ReportContext context) {
      final String style = readStyleOption();
      context.setYear(readYear())
             .setLimit(readLimit(10, 1, 100));

      TextTerminal<?> terminal = context.getTerminal();
      terminal.println("Day\t\t   Count");
      terminal.println(repeat("-", 27));

      getImpl(style).reportLeastFlightsByDay(context);
   }

   @Override
   public void reportMostFlightsByOriginByDay(ReportContext context) {
      final String style = readStyleOption();
      context.setYear(readYear())
             .setLimit(readLimit(10, 1, 100));

      TextTerminal<?> terminal = context.getTerminal();
      terminal.println("Origin\t\t\t\tDate\t\t     Count");
      terminal.println(repeat("-", 59));

      getImpl(style).reportMostFlightsByOriginByDay(context);
   }

   @Override
   public void reportMostFlightsByCarrierByDay(ReportContext context) {
      final String style = readStyleOption();
      context.setYear(readYear())
             .setLimit(readLimit(10, 1, 100));

      TextTerminal<?> terminal = context.getTerminal();
      terminal.println("Carrier\t\t\t\tDate\t\t     Count");
      terminal.println(repeat("-", 59));

      getImpl(style).reportMostFlightsByCarrierByDay(context);
   }

   private FlightReports getImpl(String style) {
      return "iterator".equals(style) ? 
         new IteratorFlightReports() : 
         new StreamFlightReports();
   }
}