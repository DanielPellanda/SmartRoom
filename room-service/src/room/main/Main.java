package room.main;

import room.service.ServiceManager;

public class Main {
	
	public static void main(final String[] args) {
		ServiceManager manager = new ServiceManager();
		manager.start();
	}

}
