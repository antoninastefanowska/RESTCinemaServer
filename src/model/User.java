package model;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "user")
@XmlAccessorType(XmlAccessType.NONE)
public class User {
	private Authorization authorization;
	private List<Reservation> reservations;
	
	public User() { 
		this.reservations = new ArrayList<>();
	}
	
	public User(Authorization authorization) {
		this();
		this.authorization = authorization;
	}

	@XmlElement(name = "authorization")
	public Authorization getAuthorization() {
		return authorization;
	}

	public void setAuthorization(Authorization authorization) {
		this.authorization = authorization;
	}

	public List<Reservation> getReservations() {
		return reservations;
	}
	
	public void setReservations(List<Reservation> reservations) {
		this.reservations = reservations;
	}
}
