package room.service;

import jssc.SerialPortException;
import room.service.channel.serial.SerialCommChannel;

/**
 * The main class that handles the server.
 */
public class SerialCommService implements CommService {
	
	private final long updatePeriod = 210;
	private SerialCommChannel arduinoConnector;
	private Database data;
	
	public SerialCommService(final Database data) {
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
		new Thread(() -> {
			while(true) {
				arduinoConnector.sendMsg(data.getArduinoData());
				data.updateArduinoData(getArduinoStringMessage());
				invokeSleep(updatePeriod);
			}
		}).start();
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
	
	private String getArduinoStringMessage() {
		String msg = "";
		while(arduinoConnector.isMsgAvailable()) {
			try {
				msg = arduinoConnector.receiveMsg();
				System.out.println("Arduino -> " + msg);
			} catch (InterruptedException e) {
				System.err.println("Interrupted while waiting for serial data...");
			} catch (ArrayIndexOutOfBoundsException ea) {
				System.err.println("Couldn't read received output...");
			}
		}
		return msg;
	}
	
	private void invokeSleep(final long sleepPeriod) {
		try {
			Thread.sleep(sleepPeriod);
		} catch (InterruptedException e) {
			System.err.println("Interrupted thread sleep...");
		}
	}
}