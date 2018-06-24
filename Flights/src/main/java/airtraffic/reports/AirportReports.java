package airtraffic.reports;

import java.sql.ResultSet;
import airtraffic.ReportContext;


public interface AirportReports {
   ResultSet reportAirportsForState(ReportContext context);
   ResultSet reportAirportsNearLocation(ReportContext context);
   ResultSet reportAirportMetrics(ReportContext context);
   ResultSet reportAirportsWithHighestCancellationRate(ReportContext context);
}