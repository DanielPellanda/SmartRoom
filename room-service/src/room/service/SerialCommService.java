package room.service;

import jssc.SerialPortException;
import room.service.channel.serial.SerialCommChannel;

/**
 * The main class that handles the server.
 */
public class SerialCommService implements CommService {
	
	private final long updatePeriod = 200;
	
	private final SerialCommChannel arduinoConnector;
	private final Database data;
	
	/**
	 * @param data the Database object where store the data.
	 * @throws SerialPortException if the serial port fails to connect.
	 * @throws IllegalArgumentException if the Arduino connector cannot instantiate his storage. 
	 */
	public SerialCommService(final Database data) throws SerialPortException, IllegalArgumentException {
		this.data = data;
		arduinoConnector = new SerialCommChannel();
	}


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