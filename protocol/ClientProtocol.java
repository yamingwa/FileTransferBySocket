package edu.usc.yaming.protocol;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class ClientProtocol {
	public Socket clientSocket;
	public DataInputStream inputStream = null;
	public DataOutputStream outputStream = null;
	private String path = "/Users/Daniel/Desktop/share1";
	public ClientProtocol(Socket soc) throws Exception{
		this.clientSocket = soc;
		inputStream = new DataInputStream(soc.getInputStream());
		outputStream = new DataOutputStream(soc.getOutputStream());
	}
	
	public void list() throws Exception{
		String command = "LIST";
		//Send command
		//System.out.println("Send command");
		outputStream.writeUTF(command);;
		outputStream.flush();
		
		//receive file list
		System.out.println(inputStream.readUTF());
		
	}
	
	public void send(String name) throws Exception{
		String command = "SEND " + name;
		outputStream.writeUTF(command);
		outputStream.flush();
		
		File file = new File(path + "/" + name);
		if (!file.exists()) {
			outputStream.writeLong(-1);
			outputStream.flush();
			return;
		}
		//Send file size
		outputStream.writeLong(file.length());
		outputStream.flush();
		
		FileInputStream fileInput = new FileInputStream(file);
		byte[] readBytes = new byte[1024];
		int ret = 0;
		while (ret != -1) {
			ret = fileInput.read(readBytes);
			outputStream.write(readBytes);
		}
		fileInput.close();
		outputStream.flush();
	}
	
	public void receive(String name) throws Exception{
		String command = "GET " + name;
		outputStream.writeUTF(command);
		outputStream.flush();
		
		long size = inputStream.readLong();
		
		if (size < 0) {
			System.out.println("No such file or directory!");
			return;
		}
		System.out.println("Receiving file: " + name);
		System.out.println("File size: " + size);
		
		File file = new File(path + "/" + name);
		FileOutputStream fileOutput = new FileOutputStream(file);
		long leftBytes = size;
		byte[] buffer = new byte[1024];
		int readBytes;
		while (leftBytes > 0) {
			if (leftBytes > buffer.length) {
				readBytes = inputStream.read(buffer);
				fileOutput.write(buffer);
				leftBytes -= readBytes;
			} else {
				readBytes = inputStream.read(buffer, 0, (int)leftBytes);
				fileOutput.write(buffer, 0, (int)leftBytes);
				leftBytes -= readBytes;
			}
		}
		fileOutput.close();
	}
	
	public void close() throws Exception{
		String command = "CLOSE";
		outputStream.writeUTF(command);
		outputStream.flush();
		
		if (inputStream != null)
			inputStream.close();
		if (outputStream != null)
			outputStream.close();
		clientSocket.close();
	}
}
