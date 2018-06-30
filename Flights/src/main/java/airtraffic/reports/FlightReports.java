package airtraffic.reports;

import java.sql.ResultSet;
import java.time.format.DateTimeFormatter;
import airtraffic.ReportContext;

public interface FlightReports {
   DateTimeFormatter YEAR_MONTH_FORMAT = 
         DateTimeFormatter.ofPattern("MMM yyyy");
   ResultSet reportTotalFlightsFromOrigin(ReportContext context);
   ResultSet reportTotalFlightsToDestination(ReportContext context);
   ResultSet reportTotalFlightsFromOriginToDestination(ReportContext context);
   ResultSet reportTopFlightsByOrigin(ReportContext context);
   ResultSet reportTopDestinationsFromOrigin(ReportContext context);
   ResultSet reportMostPopularRoutes(ReportContext context);
   ResultSet reportWorstAverageDepartureDelayByOrigin(ReportContext context);
   ResultSet reportWorstAverageArrivalDelayByDestination(ReportContext context);
   ResultSet reportMostCancelledFlightsByOrigin(ReportContext context);
   ResultSet reportTotalFlightsByOriginState(ReportContext context);
   ResultSet reportTotalFlightsByDestinationState(ReportContext context);
   ResultSet reportLongestFlights(ReportContext context);
   ResultSet reportShortestFlights(ReportContext context);
   ResultSet reportTotalFlightsByDistanceRange(ReportContext context);
   ResultSet reportDaysWithLeastCancellations(ReportContext context);
   ResultSet reportDaysWithMostCancellations(ReportContext context);
   ResultSet reportTotalMonthlyFlights(ReportContext context);
   ResultSet reportTotalDailyFlights(ReportContext context);
   ResultSet reportTotalFlightsByDayOfWeek(ReportContext context);
   ResultSet reportMostFlightsByDay(ReportContext context);
   ResultSet reportLeastFlightsByDay(ReportContext context);
   ResultSet reportMostFlightsByOriginByDay(ReportContext context);
   ResultSet reportMostFlightsByCarrierByDay(ReportContext context);
}