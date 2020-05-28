package exceptions;

@SuppressWarnings("serial")
public class UserAlreadyExistsException extends Exception {
	
	public UserAlreadyExistsException() {
		super("U¿ytkownik o podanym loginie ju¿ istnieje.");
	}
}
