package room.service;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Collections;
import java.util.Optional;
import java.util.regex.Pattern;

import jssc.SerialPortException;
import room.service.channel.http.HttpChannel;
import room.service.channel.http.HttpResponse;
import room.service.channel.serial.SerialChannel;

public class ServiceController implements Service{
	
	private static final String IPV4_REGEX =
            "^(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\." +
            "(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\." +
            "(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\." +
            "(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)$";
	
	private final int espPort = 9000;
	private final int dashboardPort = 9001;
	
	private HttpChannel espConnector;
	private HttpChannel dashboardConnector;
	private SerialChannel arduinoConnector;
	
	private final Database data = new Database();
	private final long period = 200;
	
	public ServiceController() {
		try {
			final InetAddress wlanAddr = getWlanInterfaceAddress(); 
			espConnector = new HttpChannel("ESP", new InetSocketAddress(wlanAddr, espPort));
			dashboardConnector = new HttpChannel("Dashboard", new InetSocketAddress(wlanAddr, dashboardPort));
			arduinoConnector = new SerialChannel();
			
			espConnector.addHandler("/updateData", req -> data.updateEspData(req.getPostData()));
			dashboardConnector.addHandler("/requestTelemetry", req -> getTelemetry());
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
		}
	}

	@Override
	public void start() {
		espConnector.start();
		dashboardConnector.start();
		
		try {
			arduinoConnector.start();
			while(true) {
				synchronized (data) {
					arduinoConnector.send(data.personDetected + ";" + data.lightLevel);
					data.updateArduinoData(arduinoConnector.read());
					Thread.sleep(period);
				}
			}
			//Thread.sleep(60 * 1000);
		} catch (SerialPortException e) {
			System.err.println(e.toString());
			System.exit(25);
		} catch (InterruptedException e) {
			System.err.println(e.toString());
			System.exit(24);			
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(1);
		}
		
		
		dashboardConnector.close();
		espConnector.close();
		arduinoConnector.close();
	}
	
	private InetAddress getWlanInterfaceAddress() throws UnknownHostException, SocketException {
		final Pattern ipv4Pattern = Pattern.compile(IPV4_REGEX);
		for (NetworkInterface ie : Collections.list(NetworkInterface.getNetworkInterfaces())) {
			if (ie.getName().contains("wlan")) {
				for (InetAddress addr : Collections.list(ie.getInetAddresses())) {
					if (ipv4Pattern.matcher(addr.getHostAddress()).matches()) {
						return addr;
					}
	        	}
			}
		}
		throw new UnknownHostException("Cannot find a valid WLAN address");
	}
	
	private HttpResponse getTelemetry() {
		final String response = data.status + ";" + data.hours + ";" + data.mins + ";" + (data.lightOn ? "1" : "0") + ";" + data.rollerBlind;
		return new HttpResponse(200, response.length(), response);
	}
	
	private class Database {
		public boolean personDetected = false;
		public boolean lightOn = false;
		public int lightLevel = 0;
		public int status = 0;
		public int rollerBlind = 0;
		public int hours = 0;
		public int mins = 0;
		
		public HttpResponse updateEspData(final Optional<String> input) {
			if (input.isEmpty()) {
				final String response = "<h1>Error 400</h1><br/>Bad request. Parameters not specified.";
				return new HttpResponse(400, response.length(), response);
			}
			
			final String[] parameters = input.get().split(";");
			if (parameters.length <= 2) {
				final String response = "<h1>Error 400</h1><br/>Bad request. Not enough parameters.";
				return new HttpResponse(400, response.length(), response);
			}
			
			synchronized(this) {
				personDetected = parameters[0].compareTo("1") == 0 ? true : false;
				lightLevel = Integer.parseInt(parameters[1]);
			}
			
			final String response = "Data updated successfully";
			return new HttpResponse(200, response.length(), response);
		}
		
		public void updateArduinoData(final String input) {
			final String[] parameters = input.split(";");
			if (parameters.length <= 5) {
				return;
			}
			
			synchronized(this) {
				status = Integer.parseInt(parameters[0]);
				hours = Integer.parseInt(parameters[1]);
				mins = Integer.parseInt(parameters[2]);
				rollerBlind = Integer.parseInt(parameters[4]);
				lightOn = parameters[3].compareTo("1") == 0 ? true : false;
			}
		}
	}
}
