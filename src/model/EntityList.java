package model;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSeeAlso;

@XmlRootElement(name = "list")
@XmlAccessorType(XmlAccessType.NONE)
@XmlSeeAlso({Showing.class, Film.class, Seat.class, Reservation.class})
public class EntityList<T> {
	private List<T> list;
	
	public EntityList() { }
	
	public EntityList(List<T> list) {
		this.list = list;
	}
	
	@XmlElement(name = "list")
	public List<T> getList() {
		return list;
	}
	
	public void setList(List<T> list) {
		this.list = list;
	}
}
