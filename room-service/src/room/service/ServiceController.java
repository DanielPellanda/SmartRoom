package room.service;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Collections;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Pattern;

import room.service.channel.HttpChannel;
import room.service.channel.HttpMethod;
import room.service.channel.SerialChannel;

public class ServiceController implements Service{
	
	private static final String IPV4_REGEX =
            "^(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\." +
            "(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\." +
            "(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\." +
            "(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)$";
	
	private final int espPort = 9000;
	private final int dashboardPort = 9001;
	private final String arduinoPort = "";
	
	private HttpChannel espConnector;
	private HttpChannel dashboardConnector;
	private SerialChannel arduinoConnector;
	
	private String arduinoFeedback = "";
	private Database data;
	
	public void setup() {
		try {
			final InetAddress wlanAddr = getWlanInterfaceAddress(); 
			
			espConnector = new HttpChannel("ESP", new InetSocketAddress(wlanAddr, espPort));
			dashboardConnector = new HttpChannel("Dashboard", new InetSocketAddress(wlanAddr, dashboardPort));
			arduinoConnector = new SerialChannel(arduinoPort);
			
			espConnector.setup();
			dashboardConnector.setup();
			
			espConnector.addHandler("/updateData", p -> data.updateData(p), HttpMethod.POST);
			dashboardConnector.addHandler("/requestTelemetry", p -> arduinoFeedback, HttpMethod.GET);
		} catch (UnknownHostException e) {
			System.err.println(e.toString());
			System.exit(27);
		} catch (SocketException e) {
			System.err.println(e.toString());
			System.exit(28);
		}
	}

	@Override
	public void start() {
		espConnector.start();
		dashboardConnector.start();
		arduinoConnector.start();
		
		while(true) {
			synchronized (data) {
				if (data.updated) {
					arduinoConnector.send("Movement:" + data.movementDetected + "; Light:" + data.lightLevel);
					data.updated = false;
				}
				arduinoFeedback = arduinoConnector.read();
			}
		}
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
	
	private class Database {
		public boolean updated = false;
		public boolean movementDetected = false;
		public int lightLevel = 0;
		
		public String updateData(final Optional<Map<String, String>> parameters) {
			if (parameters.isEmpty()) {
				return "Failed update";
			}
			
			Map<String, String> params = parameters.get();
			boolean movementUpdateSuccess = false;
			boolean lightUpdateSuccess = false;
			
			synchronized(this) {
				if (params.containsKey("movement")) {
					movementDetected = params.get("movement").compareTo("true") == 0;
					if (params.get("movement").compareTo("true") == 0 || params.get("movement").compareTo("false") == 0) {
						movementUpdateSuccess = true;
					}
				}
				if (params.containsKey("light")) {
					try {
						lightLevel = Integer.parseInt(params.get("light"));
						lightUpdateSuccess = true;
					} catch (NumberFormatException e) {
						System.err.println(e.toString());
					}
				}
				if (lightUpdateSuccess && movementUpdateSuccess) {
					updated = true;
				}
			}
			
			String response = "Movement update ";
			response += movementUpdateSuccess ? "Success" : "Failed";
			response += "; Light update ";
			response += lightUpdateSuccess ? "Success" : "Failed";
			return response;
		}
	}
}
