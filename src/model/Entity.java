package model;

import java.util.List;

public abstract class Entity {
	private List<Link> links;
	
	public List<Link> getLinks() {
		return links;
	}
	
	public void setLinks(List<Link> links) {
		this.links = links;
	}
}
