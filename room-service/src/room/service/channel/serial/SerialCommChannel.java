package room.service.channel.serial;

import java.util.concurrent.*;
import jssc.*;

/**
 * Comm channel implementation based on serial port.
 * 
 * @author aricci
 *
 */
public class SerialCommChannel implements SerialPortEventListener {

	private final SerialPort serialPort;
	private final BlockingQueue<String> queue;
	private StringBuffer currentMsg = new StringBuffer("");
	
	public SerialCommChannel() throws SerialPortException, IllegalArgumentException {
		queue = new ArrayBlockingQueue<String>(100);

		serialPort = new SerialPort(searchPort());
		serialPort.openPort();

		serialPort.setParams(SerialPort.BAUDRATE_9600,
		                         SerialPort.DATABITS_8,
		                         SerialPort.STOPBITS_1,
		                         SerialPort.PARITY_NONE);

		serialPort.setFlowControlMode(SerialPort.FLOWCONTROL_RTSCTS_IN | 
		                                  SerialPort.FLOWCONTROL_RTSCTS_OUT);

		serialPort.addEventListener(this);
	}

	
	public void sendMsg(String msg) {
		char[] array = (msg+"\n").toCharArray();
		byte[] bytes = new byte[array.length];
		for (int i = 0; i < array.length; i++){
			bytes[i] = (byte) array[i];
		}
		try {
			synchronized (serialPort) {
				serialPort.writeBytes(bytes);
			}
		} catch(Exception ex){
			ex.printStackTrace();
		}
		System.out.println("Arduino <- " + msg);
	}

	
	public String receiveMsg() throws InterruptedException {
		return queue.take();
	}

	public boolean isMsgAvailable() {
		return !queue.isEmpty();
	}

	/**
	 * This should be called when you stop using the port.
	 * This will prevent port locking on platforms like Linux.
	 * @throws SerialPortException 
	 */
	public void close() throws SerialPortException {
		if (serialPort != null) {
			serialPort.removeEventListener();
			serialPort.closePort();
		}
	}


	public void serialEvent(final SerialPortEvent event) {
		/* if there are bytes received in the input buffer */
		if (event.isRXCHAR()) {
            try {
            		String msg = serialPort.readString(event.getEventValue());
            		
            		msg = msg.replaceAll("\r", "");
            		
            		currentMsg.append(msg);
            		
            		boolean goAhead = true;
            		
        			while(goAhead) {
        				String msg2 = currentMsg.toString();
        				int index = msg2.indexOf("\n");
            			if (index >= 0) {
            				queue.put(msg2.substring(0, index));
            				currentMsg = new StringBuffer("");
            				if (index + 1 < msg2.length()) {
            					currentMsg.append(msg2.substring(index + 1)); 
            				}
            			} else {
            				goAhead = false;
            			}
        			}
        			
            } catch (Exception ex) {
            		ex.printStackTrace();
                System.out.println("Error in receiving string from COM-port: " + ex);
            }
        }
	}
	
	/**
	 * @return the serial port connected to arduino.
	 */
	private String searchPort() {
		final String pattern = "Arduino Uno";
		final String failString = "null";
		
		 for (Object port : com.fazecast.jSerialComm.SerialPort.getCommPorts()) {
			 if (port.toString().contains(pattern)) {
				 return port.toString().substring(13, 17);
			 }
		 }
		
		 return failString;
	}
}
