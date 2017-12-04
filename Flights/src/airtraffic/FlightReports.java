package airtraffic;

import java.time.format.DateTimeFormatter;

public interface FlightReports {
   DateTimeFormatter YEAR_MONTH_FORMAT = 
         DateTimeFormatter.ofPattern("MMM yyyy");
   void reportTotalFlightsFromOrigin(ReportContext context);
   void reportTotalFlightsToDestination(ReportContext context);
   void reportTotalFlightsFromOriginToDestination(ReportContext context);
   void reportTopFlightsByOrigin(ReportContext context);
   void reportTopDestinationsFromOrigin(ReportContext context);
   void reportMostPopularRoutes(ReportContext context);
   void reportWorstAverageDepartureDelayByOrigin(ReportContext context);
   void reportWorstAverageArrivalDelayByDestination(ReportContext context);
   void reportMostCancelledFlightsByOrigin(ReportContext context);
   void reportTotalFlightsByOriginState(ReportContext context);
   void reportTotalFlightsByDestinationState(ReportContext context);
   void reportLongestFlights(ReportContext context);
   void reportShortestFlights(ReportContext context);
   void reportTotalFlightsByDistanceRange(ReportContext context);
   void reportDaysWithLeastCancellations(ReportContext context);
   void reportDaysWithMostCancellations(ReportContext context);
   void reportTotalMonthlyFlights(ReportContext context);
   void reportTotalDailyFlights(ReportContext context);
   void reportTotalFlightsByDayOfWeek(ReportContext context);
   void reportMostFlightsByDay(ReportContext context);
   void reportLeastFlightsByDay(ReportContext context);
   void reportMostFlightsByOriginByDay(ReportContext context);
   void reportMostFlightsByCarrierByDay(ReportContext context);
}