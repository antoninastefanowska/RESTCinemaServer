package exceptions;

@SuppressWarnings("serial")
public class InvalidIdException extends Exception {

	public InvalidIdException() {
		super("Brak obiektu o przes�anym identyfikatorze.");
	}
}
