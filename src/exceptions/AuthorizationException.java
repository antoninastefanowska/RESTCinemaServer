package exceptions;

@SuppressWarnings("serial")
public class AuthorizationException extends Exception {

	public AuthorizationException() {
		super("B��d autentykacji. Nie masz uprawnie� do tej operacji.");
	}
}
