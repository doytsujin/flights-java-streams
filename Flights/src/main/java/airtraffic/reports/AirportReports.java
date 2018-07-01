package airtraffic.reports;

import java.sql.ResultSet;
import airtraffic.ReportContext;


/**
 * Interface that describes all of the airport reports.
 *
 * @author Tony Piazza <tony@piazzaconsulting.com>
 */
public interface AirportReports {
   ResultSet reportAirportsForState(ReportContext context);
   ResultSet reportAirportsNearLocation(ReportContext context);
   ResultSet reportAirportMetrics(ReportContext context);
   ResultSet reportAirportsWithHighestCancellationRate(ReportContext context);
}