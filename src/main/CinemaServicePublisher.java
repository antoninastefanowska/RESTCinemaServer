package main;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

import com.sun.grizzly.http.SelectorThread;
import com.sun.grizzly.http.servlet.ServletAdapter;
import com.sun.jersey.api.container.grizzly.GrizzlyWebContainerFactory;

import model.Database;
import service.IPAddressCheckFilter;

public class CinemaServicePublisher {
	private static final String URL = "http://192.168.0.107:8080/cinema/";
	
	@SuppressWarnings("resource")
	public static void main(String[] args) {
		Database.initialize();
		
		Map<String, String> initParams = new HashMap<>();
		initParams.put("com.sun.jersey.config.property.packages", "service");
		
		System.out.println("Rozpoczynanie...");
		try {			
			SelectorThread threadSelector = GrizzlyWebContainerFactory.create(URL, initParams);
			ServletAdapter adapter = (ServletAdapter)threadSelector.getAdapter();
			adapter.addFilter(new IPAddressCheckFilter(), "IPAddressCheckFilter", null);
			System.out.println("Serwis gotowy.");
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		Scanner input = new Scanner(System.in);
		while (true) {
			String command = input.nextLine();
			Database.getInstance().processCommand(command);
		}
	}
}
