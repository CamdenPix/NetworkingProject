import java.net.*;
import java.io.*;
import java.nio.file.*;
import java.util.HashMap;
import java.util.Map;

/*
* Camden Genta
* Last Updated 3/18/2025
*
* Description: A server that handles a client joining, logging in and out, creating a user, and sending messages
*
* Referenced Code from geeksforgeeks https://www.geeksforgeeks.org/socket-programming-in-java/
*/

public class Server{
	private Socket s = null;
	private ServerSocket ss = null;
	private DataInputStream clientResponse = null;
	private DataOutputStream out = null;
	private Map<String, User> users = new HashMap<>();
	private User connectedUser = null;

	public Server(int port){
		try{
			ss = new ServerSocket(port);
			System.out.println("Reading in users...");
			readUsers();

			System.out.println("Server Started, waiting for a client...");
			s = ss.accept();
			System.out.println("Client Accepted");

			clientResponse = new DataInputStream(new BufferedInputStream(s.getInputStream()));
			out = new DataOutputStream(s.getOutputStream());

			String message = "";
			while(true){
				try{
					message = clientResponse.readUTF();
					String[] messageParts = message.split(" ");

					if(messageParts[0].equals("login")){
						//Use the UserID provided by Client to get the object from the Map and then compare the passwords
						//I love Map data structures
						connectedUser = users.get(messageParts[1]); //Note: temp is a reference here, not a new object
						if(connectedUser == null){
							returnError("User not found");
						} else if(!connectedUser.getPassword().equals(messageParts[2]) && !connectedUser.isLoggedIn()){
							connectedUser = null;
							returnError("Wrong Password");
						} else{
							connectedUser.setLoggedIn(true);
							out.writeUTF("Successfully logged in");
						}
					}

					else if(messageParts[0].equals("logout")){
						if(connectedUser == null) {
							returnError("Not logged in");
						} else{
							connectedUser.setLoggedIn(false);
							connectedUser = null;
							out.writeUTF("Over");
						}
					}

					else if(messageParts[0].equals("send")){
						if(connectedUser == null) {
							returnError("Not logged in");
						}
						else{
							out.writeUTF(connectedUser.getUsername() + ": " + message.substring(5));
						}
					}

					else if(messageParts[0].equals("newuser")){
						if(connectedUser != null){
							returnError("Already logged in");
						} else if (messageParts.length != 3) { //this check should technically be done client side
							returnError("Invalid number of arguments");
						} else{
							if(createUser(messageParts[1], messageParts[2])){
								out.writeUTF("Success");
							} else {
								returnError("Failed to create user");
							}
						}
					}

					else{
						returnError("Invalid Command");
					}
					//System.out.println("Client Sent: "+m);
				}
				catch(IOException i){
					System.out.println(i);

					s = ss.accept();
					System.out.println("Client Accepted");

					clientResponse = new DataInputStream(new BufferedInputStream(s.getInputStream()));
					out = new DataOutputStream(s.getOutputStream());
				}
			}
		}
		catch(IOException i){
			System.out.println(i);
		}
	
	}

	private void readUsers(){
		String file = "NetworkingProject/users.csv";
		String line = "";

		try (BufferedReader br = new BufferedReader(new FileReader(file))) {
			while ((line = br.readLine()) != null) {
				String[] values = line.split(",");
				User u = new User(values[0], values[1]);
				users.put(values[0], u);
			}
		} catch (IOException e) {
			System.out.println(e);
		}
	}

	private void returnError(String error){
		try {
			out.writeUTF("Error: " + error);
		} catch (IOException e) {
			System.out.println(e);
		}
	}

	private boolean createUser(String username, String password){
		//Check to see if User already exists
		if(users.containsKey(username)){
			return false;
		}

		String entry = "\n" + username + "," + password;
		try {
			Files.write(Paths.get("NetworkingProject/users.csv"), entry.getBytes(), StandardOpenOption.APPEND);
			User u = new User(username, password);
			users.put(username, u);
		}catch (IOException e) {
			System.out.println(e);
			return false;
		}
		return true;
	}

	public static void main(String[] args)
	{
		Server s = new Server(11172);
	}
}