package room.service;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Collections;
import java.util.regex.Pattern;

import room.service.channel.http.HttpChannel;

/**
 * The main class that handles the server.
 */
public class HttpCommService implements CommService {
	
	private final int espPort = 5067;
	private final int dashboardPort = 8120;
	private final String networkHost = "wlan";
	private static final String IPV4_REGEX =
            "^(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\." +
            "(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\." +
            "(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\." +
            "(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)$";
	
	private HttpChannel espConnector;
	private HttpChannel dashboardConnector;
		
	public HttpCommService(Database data) {
		
		try {
			
			final InetAddress wlanAddr = getWlanInterfaceAddress(); 
			espConnector = new HttpChannel("ESP", new InetSocketAddress(wlanAddr, espPort));
			dashboardConnector = new HttpChannel("Dashboard", new InetSocketAddress(wlanAddr, dashboardPort));
			
			espConnector.addHandler("/updateData", req -> data.updateEspData(req.getPostData()));
			dashboardConnector.addHandler("/requestData", req -> data.getDashboardData());
			dashboardConnector.addHandler("/accessControl", req -> data.putRequest(req.getPostData()));
			
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

	/**
	 * Begins the execution of the server.
	 */
	@Override
	public void start() {
		espConnector.start();
		dashboardConnector.start();
	}
	
	/**
	 * Terminates the execution of the server
	 */
	@Override
	public void close() {
		dashboardConnector.close();
		espConnector.close();
	}
	
	/**
	 * @return an InetAddress object for a network interface that satisfies the pattern specified in networkHost.
	 * @throws UnknownHostException if there aren't available network interfaces that satisfy the requirement.
	 * @throws SocketException if the socket encounters a problem.
	 */
	private InetAddress getWlanInterfaceAddress() throws UnknownHostException, SocketException {
		final Pattern ipv4Pattern = Pattern.compile(IPV4_REGEX);
		for (NetworkInterface ie : Collections.list(NetworkInterface.getNetworkInterfaces())) {
			if (ie.getName().contains(networkHost)) {
				for (InetAddress addr : Collections.list(ie.getInetAddresses())) {
					if (ipv4Pattern.matcher(addr.getHostAddress()).matches()) {
						return addr;
					}
	        	}
			}
		}
		throw new UnknownHostException("Cannot find a valid WLAN address");
	}
}