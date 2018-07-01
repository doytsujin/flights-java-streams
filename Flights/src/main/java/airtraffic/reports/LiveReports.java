package airtraffic.reports;

import airtraffic.ReportContext;


/**
 * Interface that describes all of the live reports.
 *
 * @author Tony Piazza <tony@piazzaconsulting.com>
 */
public interface LiveReports {
   void reportAirportMetrics(ReportContext context);
   void reportCarrierMetrics(ReportContext context);
}