package airtraffic;

public interface LiveReports {
   void reportAirportMetrics(ReportContext context);
   void reportCarrierMetrics(ReportContext context);
}