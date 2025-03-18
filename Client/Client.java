import java.net.*;
import java.io.*;
import java.util.Objects;

/*
 * Camden Genta
 * Last Updated 3/18/2025
 *
 * Description: A client that joins a server and sends messages with commands to:
 * login, logout, send message, and create and new user
 *
 * Referenced Code from geeksforgeeks https://www.geeksforgeeks.org/socket-programming-in-java/
 */


public class Client{
	private Socket s = null;
	private DataInputStream userInput = null;
	private DataInputStream serverResponse = null;
	private DataOutputStream out = null;
	private boolean loggedIn = false;

	public Client(String addr, int port){
		try{
			s = new Socket(addr, port);
			System.out.println("Now Connected to: " + addr);

			userInput = new DataInputStream(System.in);
			serverResponse = new DataInputStream(new BufferedInputStream(s.getInputStream()));

			out = new DataOutputStream(s.getOutputStream());
		}
		catch(UnknownHostException u){
			System.out.println(u);
			return;
		}
		catch(IOException i){
			System.out.println(i);
			return;
		}

		String message = "";
		while(!message.equals("Over")){
			try{
				message = userInput.readLine();
				String[] messageParts = message.split(" ");
				if(Objects.equals(messageParts[0],"login")){
					if(loggedIn){ System.out.println("Error: You are already logged in"); }
					else{
						String response = sendMessage(message);
						if(!response.startsWith("Error")){
							loggedIn = true;
						}
					}
				}else if(Objects.equals(messageParts[0],"logout")){
					if(!loggedIn){ System.out.println("Error: You are not logged in"); }
					else{
						//this exits the program because server responds with Over
						//shit coding ik
						message = sendMessage(message);
					}
				}else if (Objects.equals(messageParts[0],"send")) {
					if(!loggedIn){ System.out.println("Error: You are not logged in"); }
					else{
						sendMessage(message);
					}
				}else if (Objects.equals(messageParts[0],"newuser")){
					if(loggedIn){ 
						System.out.println("Error: You are logged in"); 
					} else if (messageParts[1].length() < 3 || messageParts[1].length() > 32) {
						System.out.println("Error: Username incorrect length. Should be between 3 and 32 characters");
					} else if (messageParts[2].length() < 4 || messageParts[2].length() > 8) {
						System.out.println("Error: Password incorrect length. Should be between 4 and 8 characters");
					} else{
						sendMessage(message);
					}
				}else{
					System.out.println("Error: Invalid Command.\nCommands are login, logout, send, or newuser");
				}
//				out.writeUTF(message);
//				System.out.println("Sent a message to server");
//				serverMessage = serverResponse.readUTF();
//				System.out.println("Server Sent: " + serverMessage);
			}
			catch(IOException i){
				System.out.println(i);
			}
		}

		try{
			userInput.close();
			serverResponse.close();
			out.close();
			s.close();
		}
		catch(IOException i){
			System.out.println(i);
		}
	}

	private String sendMessage(String message){
		try {
			out.writeUTF(message);
			String response = serverResponse.readUTF();
			System.out.println(response);
			return response;
		}
		catch(IOException i){
			System.out.println(i);
		}
		return null;
	}

	public static void main(String[] args){
		Client c = new Client("127.0.0.1", 11172);
	}

}