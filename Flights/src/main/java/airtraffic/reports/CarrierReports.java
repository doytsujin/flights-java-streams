package airtraffic.reports;

import java.sql.ResultSet;
import airtraffic.ReportContext;

public interface CarrierReports {
   ResultSet reportMostCancelledFlightsByCarrier(ReportContext context);
   ResultSet reportCarrierMetrics(ReportContext context);
   ResultSet reportCarriersWithHighestCancellationRate(ReportContext context);
}