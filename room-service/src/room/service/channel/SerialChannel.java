package room.service.channel;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import jssc.SerialPort;
import jssc.SerialPortEvent;
import jssc.SerialPortEventListener;
import jssc.SerialPortException;

public class ArduinoChannel implements CommChannel, SerialPortEventListener {

	private static final long SEND_INTERVAL = 1000;
	
	private final List<String> sendQueue = new ArrayList<>();
	private final SerialPort serialPort;
	
	public ArduinoChannel(String portName) {
		serialPort = new SerialPort(portName);
	}
	
	public void start() {
		try {
		    serialPort.openPort();

		    serialPort.setParams(SerialPort.BAUDRATE_9600,
		                         SerialPort.DATABITS_8,
		                         SerialPort.STOPBITS_1,
		                         SerialPort.PARITY_NONE);

		    serialPort.setFlowControlMode(SerialPort.FLOWCONTROL_RTSCTS_IN | 
		                                  SerialPort.FLOWCONTROL_RTSCTS_OUT);

		    serialPort.addEventListener(this, SerialPort.MASK_RXCHAR);
		}
		catch (SerialPortException ex) {
		    System.out.println("There are an error on writing string to port т: " + ex);
		}
		
		new SerialSender(serialPort).start();
	}

	/**
	 * This should be called when you stop using the port.
	 * This will prevent port locking on platforms like Linux.
	 */
	public synchronized void close() {
		try {
			if (serialPort != null) {
				serialPort.removeEventListener();
				serialPort.closePort();
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	public synchronized void send(String msg) {
		sendQueue.add(msg);
	}
	
	/**
	 * Handle an event on the serial port. Read the data and print it.
	 */
	@Override
	public synchronized void serialEvent(SerialPortEvent event) {
        if(event.isRXCHAR() && event.getEventValue() > 0) {
            try {
                String receivedData = serialPort.readString(event.getEventValue());
                System.out.print("Arduino :: " + receivedData);
            }
            catch (SerialPortException ex) {
                System.out.println("Error in receiving string from COM-port: " + ex);
            }
        }
	}

	class SerialSender extends Thread {
		private SerialPort port;

		public SerialSender(SerialPort port){
			this.port = port;
		}
		
		private synchronized void sendAll() throws Exception{
			Iterator<String> it = sendQueue.iterator();
			while(it.hasNext()) {
				port.writeString(it.next(), "UTF-8");
			}
			sendQueue.clear();
		}
		
		@Override
		public void run(){
			while (true){
				try {
					sendAll();
					Thread.sleep(SEND_INTERVAL);
				} catch (Exception ex){
					ex.printStackTrace();
				}
			}
		}
	}
}