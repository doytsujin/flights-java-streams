package airtraffic;

public class RepositoryException extends RuntimeException {
	private static final long serialVersionUID = 1L;

	public RepositoryException(Throwable t) {
		super(t);
	}
}