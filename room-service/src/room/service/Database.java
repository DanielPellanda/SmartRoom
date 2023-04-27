package room.service;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import room.service.channel.http.HttpRequestObject;
import room.service.channel.http.HttpResponse;

/**
 * A class that handles the data exchanges between the devices.
 */
class Database {
	private class Request {
		public int status = 0;
		public int rollerBlind = 0;
		public boolean light = false;
		
		public void reset() {
			status = 0;
		}
	}
	
	private Request request = new Request();
	private boolean personDetected = false;
	private boolean lightOn = false;
	private int lightLevel = 0;
	private int status = 0;
	private int rollerBlind = 0;
	private int hours = 0;
	private int mins = 0;
	
	/**
	 * Updates the data related to the ESP component.
	 * @param input a string of the data received.
	 * @return an HttpResponse object that can be used as a response for the end receiver.
	 */
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
			personDetected = parameters[0].compareTo("1") == 0;
			lightLevel = Integer.parseInt(parameters[1]);
		}
		
		final String response = "Data updated successfully";
		return new HttpResponse(200, response.length(), response);
	}
	
	/**
	 * Updates the data related to the Arduino component.
	 * @param input a string of the data received.
	 */
	public void updateArduinoData(final String input) {
		final String[] parameters = input.split(";");
		if (parameters.length < 5) {
			return;
		}
		
		synchronized(this) {
			try {
				status = Integer.parseInt(parameters[0]);
				hours = Integer.parseInt(parameters[1]);
				mins = Integer.parseInt(parameters[2]);
				rollerBlind = Integer.parseInt(parameters[4]);
			} catch (NumberFormatException e) {
				System.err.println("Received a not valid integer value....");
			}
			lightOn = parameters[3].compareTo("1") == 0 ? true : false;
		}
	}
	
	/**
	 * Handles the request from a device to take control of the room.
	 * @param postData the new parameters to be applied for the room.
	 * @return an HttpResponse object that can be used as a response for the end receiver.
	 */
	protected HttpResponse putRequest(final Optional<String> postData) {
		if (postData.isEmpty()) {
			final String response = "Java Error: post data is empty";
			return new HttpResponse(400, response.length(), response);
		}
		final Map<String, String> postArgs = new HashMap<>();
		HttpRequestObject.parseQuery(postArgs, postData.get());
		
		synchronized(this) {
			 if (postArgs.containsKey("status")) {
				this.request.status = Integer.parseInt(postArgs.get("status"));
				if (request.status == 0) {
					request.status = 2;
				}
			 }
			 if (postArgs.containsKey("light")) {
				this.request.light = postArgs.get("light").compareTo("1") == 0;
			 }
			 if (postArgs.containsKey("roll")) {
				this.request.rollerBlind = Integer.parseInt(postArgs.get("roll"));
			 }
		}
		
		final String response = "Settings successfully applied.";
		return new HttpResponse(200, response.length(), response);
	}
	
	/**
	 * @return a string with the data related to Arduino.
	 */
	public String getArduinoData() {
		String data = "";
		synchronized (this) {
			data = request.status + ";" + (request.light ? "1" : "0") + ";" + request.rollerBlind + ";" + (personDetected ? "1" : "0") + ";" + lightLevel;
			request.reset();
		}
		return data;
	}
	
	/**
	 * @return an HttpResponse to send to the dashboard for periodic update.
	 */
	public HttpResponse getDashboardData() {
		final String response = this.status + ";" + this.hours + ";" + this.mins + ";" + (this.lightOn ? "1" : "0") + ";" + this.rollerBlind;
		return new HttpResponse(200, response.length(), response);
	}

}
