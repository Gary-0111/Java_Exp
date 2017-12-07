import java.io.*;
import java.net.*;
import java.util.*;
import java.util.concurrent.CountDownLatch;


class ServerThread extends Thread {
	private static ArrayList<Socket> list = new ArrayList<Socket>();
	private Socket socket;
	private BufferedReader in;
	private PrintWriter out;
	
	ServerThread(Socket socket){
		list.add(socket);
		this.socket = socket;
	}
	
	public void run(){
		try{
			in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			while(true) {
				String str = in.readLine();
				if (str.split(":")[2].equals(" BYE"))
					break;
				System.out.println(str);
				for(Socket socket1:list){
					out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket1.getOutputStream())), true);
					out.println(str);
				}
			}
			list.remove(socket);
			System.out.println(socket.getRemoteSocketAddress().toString().split("/")[1] + " leave the room.");
			socket.close();
		}
		catch (Exception e){
			
		}
	}
}

public class TCPSingleUserServer {

	public static final int PORT = 8080;

	public static void main(String[] args) throws IOException {
		ServerSocket server;
		try {
			server = new ServerSocket(PORT);
			System.out.println("Started: " + server);
			while(true) {
				Socket socket = server.accept();
				System.out.println(socket.getRemoteSocketAddress().toString().split("/")[1] + " enter the room.");
				new ServerThread(socket).start();
			}
		}
		catch (Exception e) {
			System.out.println(e.toString());
		}
	}
}
