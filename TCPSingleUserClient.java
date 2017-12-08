import java.net.*;
import java.io.*;
import java.util.*;
import java.util.concurrent.CountDownLatch;

class Listener extends Thread {
	private Socket socket;
	private BufferedReader in;

	public Listener(Socket socket, String nickname) {
		super(nickname);
		this.socket = socket;
	}

	public void run() {
		try {
			in = new BufferedReader(new InputStreamReader(this.socket.getInputStream()));
			while (socket.isClosed() == false && socket.isConnected() == true) {
				String str = in.readLine();
				if (str.split(":")[1].equals(" BYE"))
					break;
				System.out.println(str);
			}
			in.close();
			socket.close();
		}
		catch(Exception e) {

		}
	}

}

class Speaker extends Thread {
	private Socket socket;
	private PrintWriter out;

	public Speaker(Socket socket, String nickname) {
		super(nickname);
		this.socket = socket;
	}

	public void run() {
		try{
			out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())), true);			
			out.println(this.getName()+"["+this.socket.getLocalSocketAddress().toString().split("/")[1]+"]");			
			Scanner sc = new Scanner(System.in);
			while(socket.isClosed() == false && socket.isConnected() == true) {
				String s = sc.nextLine();
				out.println(this.getName() + ": " + s);
				if(s.equals("BYE")) break;
			}
			sc.close();
			out.close();
			socket.close();
		}
		catch(Exception e) {

		}
	}
}

public class TCPSingleUserClient {
	public static final int PORT = 8080;

	public static void main(String[] args) throws IOException {
		String nickname;
		System.out.print("Please enter your Nickname: ");
		nickname = new BufferedReader(new InputStreamReader(System.in)).readLine();
		InetAddress addr = InetAddress.getByName("202.192.34.44");

		System.out.println("addr = " + addr);
		Socket socket = new Socket(addr, PORT);
		try {
			System.out.println("socket = " + socket);
			System.out.println("Successful connected!");
			Listener L = new Listener(socket, nickname);
			L.start();
			Speaker S = new Speaker(socket, nickname);
			S.start();
			while(socket.isClosed() == false && socket.isConnected() == true) ;
		} 
		catch(Exception e) {

		}
		finally {
			System.out.println("closing...");
			socket.close();
		}
	}
}
