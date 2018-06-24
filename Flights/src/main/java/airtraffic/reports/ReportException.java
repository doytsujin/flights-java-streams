package airtraffic.reports;


/**
 * Custom exception for reports
 * 
 * @author Tony Piazza <tony@piazzaconsulting.com>
 */
public class ReportException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    public ReportException(Throwable t) {
        super(t);
    }
}