package airtraffic;

public interface AirportReports {
   void reportAirportsForState(ReportContext context);
   void reportAirportsNearLocation(ReportContext context);
   void reportAirportMetrics(ReportContext context);
   void reportAirportsWithHighestCancellationRate(ReportContext context);
}