package de.uniba.wiai.ktr.serverjava;

import java.io.*;
import java.net.*;

public class SimpleServer {

	public static void main(String[] args) {

		ServerSocket serverSocket = null;
		int serverPortNumber = 4711;
		Socket clientSocket = null;
		PrintWriter out = null;
		BufferedReader in = null;
		String fromClient; // Nachricht des Clients
		String fromServer = "Server-Pong"; // Server-Antwort

		// Öffne Server Socket
		try {
			serverSocket = new ServerSocket(serverPortNumber);
			System.out.println("Server listens on port: " + serverPortNumber);
			// Öffne eigenen Socket fuer die weiterführende Kommunikation mit dem Client, damit der Server-Socket für die Kommunikation mit weiteren Clients genutzt werden kann.
			clientSocket = serverSocket.accept();

			// Initialisiere Reader & Writer
			out = new PrintWriter(clientSocket.getOutputStream(), true);
			in = new BufferedReader(new InputStreamReader(
					clientSocket.getInputStream()));

			// Empfange Daten vom Client ...
			fromClient = in.readLine();
			System.out.println("Server received from Client: " + fromClient);

			// Sende Daten zum Client
			out.println(fromServer);

		} catch (IOException e) {
			e.printStackTrace();
			System.exit(1);
		} finally {
			// Beende Kommunikation/Verbindung
			// Schließe Streams vor Sockets. Reihenfolge beachten!
			try {
				out.close();
				in.close();
				clientSocket.close();
				serverSocket.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}
