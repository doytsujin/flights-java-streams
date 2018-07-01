package airtraffic.reports;

import java.sql.ResultSet;
import airtraffic.ReportContext;


/**
 * Interface that describes all of the carrier reports.
 *
 * @author Tony Piazza <tony@piazzaconsulting.com>
 */
public interface CarrierReports {
   ResultSet reportMostCancelledFlightsByCarrier(ReportContext context);
   ResultSet reportCarrierMetrics(ReportContext context);
   ResultSet reportCarriersWithHighestCancellationRate(ReportContext context);
}