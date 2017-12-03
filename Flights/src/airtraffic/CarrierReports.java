package airtraffic;

public interface CarrierReports {
   void reportMostCancelledFlightsByCarrier(Repository repository);
   void reportCarrierMetrics(Repository repository);
   void reportCarriersWithHighestCancellationRate(Repository repository);
}