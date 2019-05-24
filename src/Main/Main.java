package Main;

import Communication.Client;
import Movement.MapPilot;

public class Main {
	public static void main(String[] args) {
		 Client c = new Client();
		 MapPilot p = new MapPilot(c);

		 Thread client = new Thread(c);
		 
		 client.start();
		 p.start();
	}
}
