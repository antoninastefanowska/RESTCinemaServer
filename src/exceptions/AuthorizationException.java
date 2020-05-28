package exceptions;

@SuppressWarnings("serial")
public class AuthorizationException extends Exception {

	public AuthorizationException() {
		super("B³¹d autentykacji. Nie masz uprawnieñ do tej operacji.");
	}
}
