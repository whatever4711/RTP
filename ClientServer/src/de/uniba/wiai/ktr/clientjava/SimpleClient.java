package de.uniba.wiai.ktr.clientjava;
import java.io.*;
import java.net.*;

public class SimpleClient {
	
	public static void main(String[] args){
		Socket clientSocket = null;
		int serverPortNumber = 4711;
		String serverIP = "127.0.0.1";
		PrintWriter out = null; // schreibt auf den Socket
		BufferedReader in = null; // liest vom Socket
		String ping = "Client-Ping"; // zu versendende Nachricht
		String pong; // Antwort vom Server
		
		// Öffne den Socket
		try {
			clientSocket = new Socket(InetAddress.getByName(serverIP),serverPortNumber);
			//initialisiere Reader und Writer
			out = new PrintWriter(clientSocket.getOutputStream(),true);
			
			in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
			
			// Sende Daten zum Server
			out.println(ping);
			
			// Empfange Daten vom Server
			pong = in.readLine();
			
			System.out.println("Client received from Server: " + pong);
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(1);
		} finally{
			// Verbindungsabbau: Schließe Reader, Writer und Socket
			// Reihenfolge beachten! Erst Streams, dann Sockets
			try {
				out.close();
				in.close();
				clientSocket.close();
			} catch (IOException e) {
				e.printStackTrace();
				System.exit(1);
			}
			
		}
	}

}
