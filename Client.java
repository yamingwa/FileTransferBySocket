package edu.usc.yaming;

import edu.usc.yaming.protocol.*;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class Client {
	public Socket clientSocket;
	public BufferedReader inputReader = null;
	
	public Client(String ServerAddr, int port) {
		try {
			this.clientSocket = new Socket(ServerAddr, port);
		} catch (Exception e) {
			System.out.println("Wrong server address or port number");
		}
	}
	
	public void request() throws Exception {
		inputReader = new BufferedReader(new InputStreamReader(System.in));
		ClientProtocol clientProt = new ClientProtocol(clientSocket);
		boolean disconnect = false;
		
		while (!disconnect) {
			String inputLine = inputReader.readLine();
			String[] command = inputLine.split(" ");
			if (command[0].compareToIgnoreCase("LS") == 0) {
				clientProt.list();
			} else if (command.length == 2) {
				if (command[0].compareToIgnoreCase("GET") == 0) {
					System.out.println("Requesting file: " + command[1]);
					clientProt.receive(command[1]);
				}
				if (command[0].compareToIgnoreCase("SEND") == 0) {
					System.out.println("Sending file: " + command[1]);
					clientProt.send(command[1]);
				}
			} else if (command[0].compareToIgnoreCase("CLOSE") == 0) {
				System.out.println("Closing Connection...");
				disconnect = true;
				inputReader.close();
				clientProt.close();
				clientSocket.close();
				System.out.println("Connection closed");
			} else {
				System.out.println("Invalid command");
			}
		}
		
	}
}
