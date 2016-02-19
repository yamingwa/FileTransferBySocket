package edu.usc.yaming.test;

import edu.usc.yaming.*;
public class TestClient {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		String serverAddr = "localhost";
		int port = 4444;
		Client client = new Client(serverAddr, port);
		System.out.println("Start requesting...");
		try {
			client.request();
		} catch(Exception e) {
			System.out.println("Error while requesting");
		}
	}

}
