package model;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "person")
@XmlAccessorType(XmlAccessType.NONE)
public class Person {	
	private String name;
	private String surname;
	
	public Person() { }
	
	public Person(String name, String surname) {
		this.name = name;
		this.surname = surname;
	}

	@XmlElement(name = "name")
	public String getName() {
		return name;
	}

	@XmlElement(name = "surname")
	public String getSurname() {
		return surname;
	}
}
