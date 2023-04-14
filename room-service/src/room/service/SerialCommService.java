package room.service;

import jssc.SerialPortException;
import room.service.channel.serial.SerialCommChannel;

/**
 * The main class that handles the server.
 */
public class SerialCommService implements CommService {
	
	private final long period = 200;
	private Thread communicate;
	private SerialCommChannel arduinoConnector;
	private Database data;
	
	public SerialCommService(Database data) {
		this.data = data;
		try {
			arduinoConnector = new SerialCommChannel();
		} catch (Exception e) {
			System.err.println(e.toString());
			System.exit(1);
		}
	}

	/**
	 * Begins the execution of the server.
	 */
	@Override
	public void start() {
		communicate = new Thread() {
			@Override
			public void run() {
				while(true) {
					synchronized (data) {
						arduinoConnector.sendMsg(data.getArduinoData());
						if(arduinoConnector.isMsgAvailable()) {
							String msg = "";
							try {
								msg = arduinoConnector.receiveMsg();
								data.updateArduinoData(msg);
								System.out.println("Arduino -> " + msg);
							} catch (InterruptedException e) {
								System.err.println("Interrupted while waiting for serial data...");
							} catch (ArrayIndexOutOfBoundsException ea) {
								System.err.println("Couldn't read received output...");
							}
						}
					}
					try {
						Thread.sleep(period);
					} catch (InterruptedException e) {
						System.err.println("Interrupted while sleeping for serial data...");
					}
				}
			}
		};
		communicate.start();
	}

	@Override
	public void close() {
		try {
			arduinoConnector.close();
		} catch (SerialPortException e) {
			System.err.println(e.toString());
			System.exit(25);
		}	
	}
}