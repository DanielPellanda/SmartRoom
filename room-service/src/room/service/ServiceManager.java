package room.service;

import java.io.IOException;
import java.net.SocketException;
import java.net.UnknownHostException;

import jssc.SerialPortException;

public class ServiceManager {
	
	private final Database data = new Database();
	private CommService httpServ;
	private CommService serialServ;
	
	public ServiceManager() {
		try {
			httpServ = new HttpCommService(data);
			serialServ = new SerialCommService(data);
			
		} catch (SerialPortException e) {
			System.err.println(e.toString());
			System.exit(25);
			
		} catch (UnknownHostException e) {
			System.err.println(e.toString());
			System.exit(26);
			
		} catch (SocketException e) {
			System.err.println(e.toString());
			System.exit(27);
			
		} catch (IOException e) {
			System.err.println(e.toString());
			System.exit(28);
			
		} catch (IllegalArgumentException e) {
			System.err.println(e.toString());
			System.exit(29);
			
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(1);
		}
	}

	/**
	 * Begins the excecution of Room Service.
	 */
	public void start() {
		httpServ.start();
		serialServ.start();
	}
	
}
