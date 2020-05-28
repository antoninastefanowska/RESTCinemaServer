package model;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import com.google.gson.annotations.Expose;

import utils.Counter;

@XmlRootElement(name = "reservation")
@XmlAccessorType(XmlAccessType.NONE)
public class Reservation extends Entity {
	private final static String UPPER = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private final static String LOWER = UPPER.toLowerCase();
    private final static String NUM = "1234567890";
    private final static String ALPHANUM = UPPER + LOWER + NUM;
	
	private static Counter indexCounter = new Counter();
	
	@Expose
	private int identifier;
	@Expose
	private int showingId;
	@Expose
	private String code;
	@Expose
	private List<Seat> seats;
	
	public Reservation() {
		this.seats = new ArrayList<>();
	}
	
	public Reservation(int showingId) {
		this.showingId = showingId;
		this.seats = new ArrayList<>();
	}
	
	@XmlElement(name = "identifier")
	public int getIdentifier() {
		return identifier;
	}
	
	public int generateIdentifier() {
		int id = indexCounter.nextValue();
		this.identifier = id;
		return id;
	}
	
	public void setIdentifier(int id) {
		this.identifier = id;
	}
	
	public String generateCode() {
		int length = 15;
        StringBuilder sb = new StringBuilder();
        Random random = new Random();
        for (int i = 0; i < length; i++) {
            int index = random.nextInt(ALPHANUM.length());
            sb.append(ALPHANUM.charAt(index));
        }
        code = sb.toString();
        return code;
    }
	
	@XmlElement(name = "code")
	public String getCode() {
		return code;
	}
	
	public void setCode(String code) {
		this.code = code;
	}

	public void setId(int id) {
		this.identifier = id;
	}
	
	public void addSeat(int row, int column) {
		seats.add(new Seat(row, column));
	}

	@XmlElement(name = "seats")
	public List<Seat> getSeats() {
		return seats;
	}

	@XmlElement(name = "showingId")
	public int getShowingId() {
		return showingId;
	}

	public void setShowingId(int showingId) {
		this.showingId = showingId;
	}
}
