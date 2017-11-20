package airtraffic;

/**
 * Wraps exceptions thrown in the Repository class.
 *
 * @author tony@piazzaconsulting.com
 */
public class RepositoryException extends RuntimeException {
	private static final long serialVersionUID = 1L;

	public RepositoryException(Throwable t) {
		super(t);
	}
}