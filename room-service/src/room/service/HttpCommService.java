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

public class HttpCommService implements CommService {
	
	private static final String IPV4_REGEX =
            "^(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\." +
            "(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\." +
            "(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\." +
            "(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)$";
	
	private final String networkHostPattern = "wlan";
	
	private final int port = 5067;
	private final String channelName = "Room service";
	private HttpChannel httpServer;

	/**
	 * @param data the Database object where store the data.
	 * @throws UnknownHostException  if there aren't available network interfaces that satisfy the requirement.
	 * @throws SocketException if the socket encounters a problem in its instantiation.
	 * @throws IOException if the HTTP server cannot be created.
	 */
	public HttpCommService(final Database data) throws UnknownHostException, SocketException, IOException{
		final String[] handlerNames = new String[] { 
			"/updateData",
			"/requestData",
			"/accessControl"
		};
		
		final InetAddress wlanAddr = getPatternInterfaceAddress(networkHostPattern); 
		httpServer = new HttpChannel(channelName, new InetSocketAddress(wlanAddr, port));
		
		httpServer.addHandler(handlerNames[0], req -> data.updateEspData(req.getPostData()));
		httpServer.addHandler(handlerNames[1], req -> data.getDashboardData());
		httpServer.addHandler(handlerNames[2], req -> data.putRequest(req.getPostData()));
	}

	@Override
	public void start() {
		httpServer.start();
	}
	
	@Override
	public void close() {
		httpServer.close();
	}
	
	/**
	 * @param networkPattern the pattern to search.
	 * @return an InetAddress object for a network interface that satisfies the pattern specified in networkPattern.
	 * @throws UnknownHostException if there aren't available network interfaces that satisfy the requirement.
	 * @throws SocketException if the socket encounters a problem.
	 */
	private InetAddress getPatternInterfaceAddress(final String networkPattern) throws UnknownHostException, SocketException {
		final String exceptionMsg = "Cannot find a valid address";
		final Pattern ipv4Pattern = Pattern.compile(IPV4_REGEX);
		
		for (NetworkInterface ie : Collections.list(NetworkInterface.getNetworkInterfaces())) {
			if (ie.getName().contains(networkPattern)) {
				for (InetAddress addr : Collections.list(ie.getInetAddresses())) {
					if (ipv4Pattern.matcher(addr.getHostAddress()).matches()) {
						return addr;
					}
	        	}
			}
		}
		throw new UnknownHostException(exceptionMsg);
	}
}
