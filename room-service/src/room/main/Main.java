package room.main;

import room.service.Service;
import room.service.ServiceController;

public class Main {
	
	public static void main(String[] args) {
		final Service serv = new ServiceController();
		serv.setup();
		serv.start();
	}

}
