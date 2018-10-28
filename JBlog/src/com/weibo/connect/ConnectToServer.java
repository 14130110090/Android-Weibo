package com.weibo.connect;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;

public class ConnectToServer {
	Socket socket = null;
	DataInputStream inputStream = null;
	DataOutputStream outputStream = null;

	public ConnectToServer(String address, int port) {
		try {
			socket = new Socket(address, port);
			socket.setSoTimeout(5000);
			inputStream = new DataInputStream(socket.getInputStream());
			outputStream = new DataOutputStream(socket.getOutputStream());
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}
    public boolean isConnected(){
    	if(socket!=null)return true;
    	return false;
    }
	public DataInputStream getInputStream() {
		return inputStream;
	}

	public DataOutputStream getOutputStream() {
		return outputStream;
	}

	public void closeConnection() {
		try {
			if (inputStream != null) {
				inputStream.close();
				inputStream = null;
			}
			if (outputStream != null) {
				outputStream.close();
				outputStream = null;
			}
			if (socket != null) {
				socket.close();
				socket = null;
			}

		} catch (IOException e) {
			e.printStackTrace();

		}
	}
	
	
	
}
