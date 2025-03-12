import java.net.*;
import java.io.*;

//Code copied from Geeks4geeks https://www.geeksforgeeks.org/socket-programming-in-java/
public class Client{
	private Socket s = null;
	private DataInputStream in = null;
	private DataOutputStream out = null;

	public Client(String addr, int port){
		try{
			s = new Socket(addr, port);
			System.out.println("Now Connected to: " + addr);

			in = new DataInputStream(System.in);

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

		String m = "";
		while(!m.equals("Over")){
			try{
				m = in.readLine();
				out.writeUTF(m);
			}
			catch(IOException i){
				System.out.println(i);
			}
		}

		try{
			in.close();
			out.close();
			s.close();
		}
		catch(IOException i){
			System.out.println(i);
		}
	}

	public static void main(String[] args){
		Client c = new Client("127.0.0.1", 5000);
	}

}