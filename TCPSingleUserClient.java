import java.net.*;
import java.io.*;
import java.util.*;
import java.util.concurrent.CountDownLatch;

class Listener extends Thread {
	Socket socket;
	BufferedReader in;
	CountDownLatch latch;
	public Listener(Socket socket, CountDownLatch _latch) {
		super(socket.getLocalSocketAddress().toString().split("/")[1]);
		this.socket = socket;
		
		latch = _latch;
	}

	public void run() {
		try{
			in = new BufferedReader(new InputStreamReader(this.socket.getInputStream()));
			while (true) {
				String str = in.readLine();
				if (str.split(":")[2].equals("BYE"))
					break;
				System.out.println(str);
			}
			in.close();
			socket.close();
		}
		catch(Exception e)
		{

		}
		latch.countDown();
	}

}

class Speaker extends Thread {
	Socket socket;
	PrintWriter out;
	CountDownLatch latch;
	public Speaker(Socket socket, CountDownLatch _latch) {
		super(socket.getLocalSocketAddress().toString().split("/")[1]);
		this.socket = socket;
		latch = _latch;
	}

	public void run() {
		try{
			out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())), true);			
			Scanner sc = new Scanner(System.in);
			while(true) {
				String s = sc.nextLine();
				//out.println("Sending Line " + i + " ....");
				//String str = in.readLine();
				//System.out.println(in.readLine());
				out.println(this.getName() + ": " + s);
				if(s.equals("BYE")) break;
			}
			sc.close();
			out.close();
			socket.close();
		}
		catch(Exception e)
		{

		}
		latch.countDown();
	}
}

public class TCPSingleUserClient {
	public static final int PORT = 8080;
	public static void main(String[] args) throws IOException {

		InetAddress addr = InetAddress.getByName("172.22.27.186");

		System.out.println("addr = " + addr);
		Socket socket = new Socket(addr, PORT);
		try {
			System.out.println("socket = " + socket);
			System.out.println("Successful connected!");
			CountDownLatch latch = new CountDownLatch(2);
			Listener L = new Listener(socket, latch);
			L.start();
			Speaker S = new Speaker(socket, latch);
			S.start();
			while(socket.isClosed() == false && socket.isConnected() == true) ;
			//latch.await();
			
		} 
		catch(Exception e)
		{

		}
		finally {
			System.out.println("closing...");
			socket.close();
		}
	}
}
