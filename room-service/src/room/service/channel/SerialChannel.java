package room.service.channel;

import java.io.UnsupportedEncodingException;

import jssc.SerialPort;
import jssc.SerialPortEvent;
import jssc.SerialPortEventListener;
import jssc.SerialPortException;

public class SerialChannel implements SerialPortEventListener {

	private final SerialPort serialPort;
	private boolean hasStarted = false;
	private String lastReceivedData = "";
	
	public SerialChannel(String portName) {
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
		    hasStarted = true;
		} catch (SerialPortException ex) {
			System.err.println(ex.toString());
		}
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
			}
		}
	}

	public synchronized void send(String msg) {
		if (hasStarted) {
			try {
				serialPort.writeString(msg, "UTF-8");
			} catch (SerialPortException | UnsupportedEncodingException e) {
				System.err.println(e.getMessage());
			}
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
                System.out.print("Arduino:: " + lastReceivedData);
            }
            catch (SerialPortException ex) {
                System.err.println("Error in receiving string from COM-port: \n\t" + ex.toString());
            }
        }
	}
}