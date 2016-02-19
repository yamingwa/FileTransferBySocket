package edu.usc.yaming;

import edu.usc.yaming.protocol.*;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {
	public ServerSocket serverSocket;
	public Socket childSocket;
	//public int port = 21; //FTP port number
	
	public Server(int port) {
		try {
			this.serverSocket = new ServerSocket(port);
		} catch (Exception e) {
			System.out.println("Port is not available");
		}
	}
	
	public void listen() throws Exception {
		while (true) {
			childSocket = serverSocket.accept();
			System.out.println("New connction from " + childSocket);
			//create a new thread to handle the connection
			Thread handle = new Thread(new Connection(childSocket));
			handle.start();
		}
	}
	
	/*
	 * New thread to handle the connection
	 */
	private class Connection implements Runnable {
		public Socket childSocket;
		public Connection(Socket soc) {
			this.childSocket = soc;
		}
		
		public void run() {
			try {
				ServerProtocol serverProt = new ServerProtocol(childSocket);
				boolean disconnect = false;
			
				while (!disconnect) {

					String commandLine = serverProt.getCommand();
					String[] command = commandLine.split(" ");
					
					if (command[0].compareToIgnoreCase("LIST") == 0) {
						System.out.println("Got ls command from client");
						serverProt.list();
					} else if (command.length == 2) {
							//A valid command with GET/SEND
							if (command[0].compareToIgnoreCase("GET") == 0) {
								serverProt.send(command[1]);
							}
							if (command[0].compareToIgnoreCase("SEND") == 0) {
								serverProt.receive(command[1]);
							}
						} else if (command[0].compareToIgnoreCase("CLOSE") == 0) {
							System.out.println("Client closed");
							disconnect = true;
							serverProt.close();
							childSocket.close();	
						} else {
							System.out.println("Invalid command");
						}
							
				} 
			}catch (Exception e) {

				try {
					childSocket.close();
				} catch (Exception ex) {}
			}
		}
	}
}
