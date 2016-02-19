package edu.usc.yaming.test;

import edu.usc.yaming.*;

public class TestServer {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		int port = 4444;
		Server server = new Server(port);
		try {
			System.out.println("Server starts listening...");
			server.listen();
		} catch(Exception e) {
			System.out.println("Error while listening");
			System.exit(-1);
		}
	}

}
