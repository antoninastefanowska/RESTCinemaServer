package model;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

import com.google.gson.annotations.Expose;

import utils.Counter;

@XmlRootElement(name = "film")
@XmlAccessorType(XmlAccessType.NONE)
public class Film extends Entity {
	private static transient Counter indexCounter = new Counter();
	
	@Expose
	private int identifier;
	@Expose
	private String title;
	@Expose
	private Person director;
	@Expose
	private List<Role> roles;
	@Expose
	private String description;
	
	@XmlTransient
	private String coverFilename;
	
	public Film() {
		this.roles = new ArrayList<>();
	}
	
	public Film(String title, Person director, String description) {
		this();
		this.title = title;
		this.director = director;
		this.description = description;
	}

	@XmlElement(name = "title")
	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	@XmlElement(name = "director")
	public Person getDirector() {
		return director;
	}

	public void setDirector(Person director) {
		this.director = director;
	}

	@XmlElement(name = "description")
	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
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

	public void addRole(Role role) {
		roles.add(role);
	}

	@XmlElement(name = "roles")
	public List<Role> getRoles() {
		return roles;
	}
	
	public void setCoverFilename(String filename) {
		this.coverFilename = filename;
	}
	
	public String getCoverFilename() {
		return coverFilename;
	}
	
	public File getCoverFile() {
		return new File("./resources/img/" + coverFilename);
	}
}
