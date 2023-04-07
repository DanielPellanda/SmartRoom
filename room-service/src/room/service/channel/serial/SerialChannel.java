package room.service.channel.serial;

import java.io.UnsupportedEncodingException;

import jssc.SerialPort;
import jssc.SerialPortEvent;
import jssc.SerialPortEventListener;
import jssc.SerialPortException;

/**
 * A class for handling the serial connection of Arduino.
 */
public class SerialChannel implements SerialPortEventListener {

	private final SerialPort serialPort;
	private boolean hasStarted = false;
	private String lastReceivedData = "";
	
	public SerialChannel() throws SerialPortException{
		serialPort = new SerialPort(searchPort());
		serialPort.openPort();
		serialPort.closePort();
	}
	
	public void start() throws SerialPortException{
		serialPort.openPort();
		serialPort.setParams(SerialPort.BAUDRATE_9600,
		                     SerialPort.DATABITS_8,
		                     SerialPort.STOPBITS_1,
		                     SerialPort.PARITY_NONE);
		serialPort.setFlowControlMode(SerialPort.FLOWCONTROL_RTSCTS_IN | 
		                              SerialPort.FLOWCONTROL_RTSCTS_OUT);
		serialPort.addEventListener(this, SerialPort.MASK_RXCHAR);
		hasStarted = true;
		
		System.out.println("Arduino channel starting on serial port " + serialPort.getPortName());
	}

	/**
	 * This should be called when you stop using the port.
	 * This will prevent port locking on platforms like Linux.
	 */
	public synchronized void close() {
		if (hasStarted) {
			try {
				if (serialPort != null) {
					serialPort.removeEventListener();
					serialPort.closePort();
				}
			} catch (Exception ex) {
				System.err.println(ex.getMessage());
			} finally {
				hasStarted = false;
			}
		}
	}

	public synchronized void send(String msg) throws SerialPortException, UnsupportedEncodingException{
		if (hasStarted) {
			serialPort.writeString(msg, "UTF-8");
			System.out.println("Arduino <- " + msg);
		}
	}
	
	public synchronized String read() {
		return lastReceivedData;
	}
	
	/**
	 * Handle an event on the serial port. Read the data and print it.
	 */
	@Override
	public synchronized void serialEvent(SerialPortEvent event) {
        if(event.isRXCHAR() && event.getEventValue() > 0 && hasStarted) {
            try {
                lastReceivedData = serialPort.readString(event.getEventValue());
                System.out.print("Arduino -> " + lastReceivedData);
            }
            catch (SerialPortException ex) {
                System.err.println("Error in receiving string from COM-port: \n\t" + ex.toString());
            }
        }
	}
	
	private String searchPort() {
		 for (Object port : com.fazecast.jSerialComm.SerialPort.getCommPorts()) {
			 if (port.toString().contains("Arduino Uno")) {
				 return port.toString();
			 }
		 }
		 return "null";
	}
}