package model;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "link")
@XmlAccessorType(XmlAccessType.NONE)
public class Link {
	private String rel;
	private String url;
	
	public Link() { }
	
	public Link(String url, String rel) {
		this.url = url;
		this.rel = rel;
	}

	@XmlElement(name = "rel")
	public String getRel() {
		return rel;
	}

	public void setRel(String rel) {
		this.rel = rel;
	}

	@XmlElement(name = "url")
	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}
}
