package model;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "seat")
@XmlAccessorType(XmlAccessType.NONE)
public class Seat {
	private int row;
	private int column;
	
	public Seat() { }
	
	public Seat(int row, int column) {
		this.row = row;
		this.column = column;
	}

	@XmlElement(name = "row")
	public int getRow() {
		return row;
	}

	public void setRow(int row) {
		this.row = row;
	}

	@XmlElement(name = "column")
	public int getColumn() {
		return column;
	}

	public void setColumn(int column) {
		this.column = column;
	}
	
	public boolean compare(Seat seat) {
		return this.row == seat.getRow() && this.column == seat.getColumn();
	}
}
