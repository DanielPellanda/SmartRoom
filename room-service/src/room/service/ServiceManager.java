package room.service;

public class ServiceManager {
	
	private final Database data = new Database();
	private CommService httpServ;
	private CommService serialServ;
	
	public ServiceManager() {
		this.httpServ = new HttpCommService(data);
		this.serialServ = new SerialCommService(data);
	}

	public void start() {
		httpServ.start();
		serialServ.start();
	}
	
}
