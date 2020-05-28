package model;

import java.io.IOException;
import java.util.Base64;
import java.util.Base64.Decoder;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "authorization")
@XmlAccessorType(XmlAccessType.NONE)
public class Authorization {
	private String username;
	private String password;
	
	public Authorization() { }
	
	public Authorization(String username, String password) {
		this.username = username;
		this.password = password;
	}
	
	public Authorization(String header) throws IOException {
		fromHeader(header);
	}
	
	public void fromHeader(String header) throws IOException {
		Decoder decoder = Base64.getDecoder();
		String decoded = new String(decoder.decode(header.split(" ")[1]), "UTF-8");
		String[] tokens = decoded.split(":");
		this.username = tokens[0];
		this.password = tokens[1];
	}

	@XmlElement(name = "username")
	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	@XmlElement(name = "password")
	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}
	
	public boolean checkAuthorization(Authorization authentication) {
		return username.equals(authentication.getUsername()) && password.equals(authentication.getPassword());
	}
}
