package airtraffic;

public interface FlightReports {
   void reportTotalFlightsFromOrigin(Repository repository);
   void reportTotalFlightsToDestination(Repository repository);
   void reportTotalFlightsFromOriginToDestination(Repository repository);
   void reportTopFlightsByOrigin(Repository repository);
   void reportTopDestinationsFromOrigin(Repository repository);
   void reportMostPopularRoutes(Repository repository);
   void reportWorstAverageDepartureDelayByOrigin(Repository repository);
   void reportWorstAverageArrivalDelayByDestination(Repository repository);
   void reportMostCancelledFlightsByOrigin(Repository repository);
   void reportTotalFlightsByOriginState(Repository repository);
   void reportTotalFlightsByDestinationState(Repository repository);
   void reportLongestFlights(Repository repository);
   void reportShortestFlights(Repository repository);
   void reportTotalFlightsByDistanceRange(Repository repository);
   void reportDaysWithLeastCancellations(Repository repository);
   void reportDaysWithMostCancellations(Repository repository);
   void reportTotalMonthlyFlights(Repository repository);
   void reportTotalDailyFlights(Repository repository);
   void reportTotalFlightsByDayOfWeek(Repository repository);
   void reportMostFlightsByDay(Repository repository);
   void reportLeastFlightsByDay(Repository repository);
   void reportMostFlightsByOriginByDay(Repository repository);
   void reportMostFlightsByCarrierByDay(Repository repository);
}