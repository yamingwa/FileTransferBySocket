package edu.usc.yaming.protocol;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class ServerProtocol{
	public Socket childSocket;
	public DataInputStream inputStream = null;
	public DataOutputStream outputStream = null;
	private String path = "/Users/Daniel/Desktop/share";
	
	public ServerProtocol(Socket soc) throws Exception{
		this.childSocket = soc;
		this.inputStream = new DataInputStream(soc.getInputStream());
		this.outputStream = new DataOutputStream(soc.getOutputStream());
	}
	
	public String getCommand() throws Exception{
		return inputStream.readUTF();
	}
	
	/*
	 * List the files in share folder
	 * The path of this folder is fixed, The command
	 * which changes the current directory will be 
	 * implemented later
	 */
	public void list() throws Exception{
		File folder = new File(path);
		File[] files = folder.listFiles();
		
		String temp = new String();
		for (File i:files){
			temp += i.getName() + " ";
		}
		outputStream.writeUTF(temp);
		outputStream.flush();
			
	}
	
	public void send(String name) throws Exception{
		String fileName = path + "/" + name;
		File file = new File(fileName);
		
		if (!file.exists()) {
			outputStream.writeLong(-1);
			outputStream.flush();
			return;
		} 
		
		System.out.println("Sending file: " + name);
		outputStream.writeLong(file.length());
		outputStream.flush();
		
		FileInputStream fileInput = new FileInputStream(file);
		byte[] buffer = new byte[1024];
		long leftBytes = file.length();
		int readBytes = 0;
		while (leftBytes > 0) {
			if (leftBytes > buffer.length) {
				readBytes = fileInput.read(buffer);
				outputStream.write(buffer);
				outputStream.flush();
				leftBytes -= readBytes;
			} else {
				readBytes = fileInput.read(buffer, 0, (int)leftBytes);
				outputStream.write(buffer, 0, (int)leftBytes);
				outputStream.flush();
				leftBytes -= readBytes;
			}
		}
		fileInput.close();
		
	}
	
	public void receive(String name) throws Exception{
		
		long size = inputStream.readLong();
		
		if (size < 0) {
			System.out.println("No such file or directory!");
			return;
		}
		System.out.println("Receiving file: " + name);
		System.out.println("File size: " + size);
		
		String fileName = path + "/" + name;
		File file = new File(fileName);
		FileOutputStream fileOutput = new FileOutputStream(file);
		byte[] buffer = new byte[1024];
		long leftBytes = size;
		int readBytes = 0;
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
		if (inputStream != null)
			inputStream.close();
		if (outputStream != null)
			outputStream.close();
		
		childSocket.close();
	}
}
