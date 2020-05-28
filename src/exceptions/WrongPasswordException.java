package exceptions;

@SuppressWarnings("serial")
public class WrongPasswordException extends Exception {
	
	public WrongPasswordException() {
		super("Nieprawid³owe has³o.");
	}
}
