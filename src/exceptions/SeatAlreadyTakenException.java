package exceptions;

@SuppressWarnings("serial")
public class SeatAlreadyTakenException extends Exception {
	
	public SeatAlreadyTakenException() {
		super("Jedno z wybranych miejsc zosta³o ju¿ zajête.");
	}
}
