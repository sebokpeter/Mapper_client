package Communication;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.concurrent.ConcurrentLinkedDeque;

import Movement.RobotPosition;

// This class is responsible for communicating with the server
public class Client implements Runnable {
	
	private ObjectOutputStream oos = null;
	private Socket s = null;
	private ConcurrentLinkedDeque<RobotPosition> positions;
	
	
	public Client() {
		positions = new ConcurrentLinkedDeque<RobotPosition>();
		try {
			System.out.println("Attempting to connect to the server...");
			s = new Socket("192.168.137.1", 5000);
			System.out.println("Connected");
			
			oos = new ObjectOutputStream(s.getOutputStream());
		}
		catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void addPosition(RobotPosition pos) {
		positions.add(pos);
	}


	/**
	 * Closes the socket used to connect to the server.
	 */
	public void stop() {
		if (s != null) {
			try {
				s.close();
				System.exit(0);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	public void run() {
		try {
			while(true) {
				if(!positions.isEmpty()) {
					RobotPosition pos = positions.remove();
					oos.writeObject(pos);
					oos.flush();
				} 
			}
			} catch (Exception e) {
				stop();
			}
	}
	
}
