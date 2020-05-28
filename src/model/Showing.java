package model;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import com.google.gson.annotations.Expose;

import exceptions.SeatAlreadyTakenException;
import utils.Counter;

@XmlRootElement(name = "showing")
@XmlAccessorType(XmlAccessType.NONE)
public class Showing extends Entity {
	private final static transient int ROWS = 10;
	private final static transient int COLUMNS = 15;
	private final static transient SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("dd-MM-yyyy HH:mm");
	
	private static transient Counter indexCounter = new Counter();
	
	@Expose
	private int identifier;
	@Expose
	private int filmId;
	@Expose
	private String filmTitle;
	@Expose
	private Date date;
	@Expose
	private List<Seat> takenSeats;
	
	public Showing() { 
		this.takenSeats = new ArrayList<>();
	}
	
	public Showing(Film film, String dateString) {
		this.takenSeats = new ArrayList<>();
		this.filmId = film.getIdentifier();
		this.filmTitle = film.getTitle();
		try {
			this.date = DATE_FORMAT.parse(dateString);
		} catch (ParseException e) {
			e.printStackTrace();
		}
	}
	
	@XmlElement(name = "filmId")
	public int getFilmId() {
		return filmId;
	}
	
	@XmlElement(name = "filmTitle")
	public String getFilmTitle() {
		return filmTitle;
	}
	
	@XmlElement(name = "dateEpoch")
	public long getDateEpoch() {
		return date.getTime();
	}
	
	public List<Seat> getTakenSeats() {
		return takenSeats;
	}
	
	@XmlElement(name = "seatsRowNumber")
	public int getRowNumber() {
		return ROWS;
	}
	
	@XmlElement(name = "seatsColumnNumber")
	public int getColumnNumber() {
		return COLUMNS;
	}
	
	private Seat findSeat(Seat seat) {
		for (Seat takenSeat : takenSeats)
			if (takenSeat.getRow() == seat.getRow() && takenSeat.getColumn() == seat.getColumn())
				return takenSeat;
		return null;
	}
	
	public Boolean isSeatTaken(Seat seat) {
		return findSeat(seat) != null;
	}
	
	public void takeSeat(Seat seat) {
		takenSeats.add(seat);
	}
	
	public void freeSeat(Seat seat) {
		Seat toRemove = findSeat(seat);
		takenSeats.remove(toRemove);
	}

	@XmlElement(name = "identifier")
	public int getIdentifier() {
		return identifier;
	}
	
	public void setIdentifier(int id) {
		this.identifier = id;
	}

	public int generateIdentifier() {
		int id = indexCounter.nextValue();
		this.identifier = id;
		return id;
	}
	
	public void makeReservation(Reservation reservation) throws SeatAlreadyTakenException {
		for (Seat seat : reservation.getSeats()) {
			if (isSeatTaken(seat))
				throw new SeatAlreadyTakenException();
			else
				takeSeat(seat);
		}
	}
	
	public void cancelReservation(Reservation reservation) {
		for (Seat seat : reservation.getSeats())
			freeSeat(seat);
	}
}
