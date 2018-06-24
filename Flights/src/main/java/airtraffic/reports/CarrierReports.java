package airtraffic.reports;

import airtraffic.ReportContext;

public interface CarrierReports {
   void reportMostCancelledFlightsByCarrier(ReportContext context);
   void reportCarrierMetrics(ReportContext context);
   void reportCarriersWithHighestCancellationRate(ReportContext context);
}