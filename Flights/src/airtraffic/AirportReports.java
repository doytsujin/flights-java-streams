package airtraffic;

public interface AirportReports {
   void reportAirportsForState(Repository repository);
   void reportAirportsNearLocation(Repository repository);
   void reportAirportMetrics(Repository repository);
   void reportAirportsWithHighestCancellationRate(Repository repository);
}