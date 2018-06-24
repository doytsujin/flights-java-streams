package airtraffic.reports;

import airtraffic.ReportContext;

public interface LiveReports {
   void reportAirportMetrics(ReportContext context);
   void reportCarrierMetrics(ReportContext context);
}