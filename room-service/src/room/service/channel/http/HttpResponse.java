package room.service.channel.http;

/**
 * A class used to handle http responses.
 */
public class HttpResponse {
	private final int code;
	private final long size;
	private final String response;
	
	public HttpResponse(final int code, final long size, final String response) {
		this.code = code;
		this.size = size;
		this.response = response;
	}
	
	/**
	 * @return the esit code of the request.
	 */
	public int getEsitCode() {
		return code;
	}
	
	/**
	 * @return the byte size of the request.
	 */
	public long getSize() {
		return size;
	}
	
	/**
	 * @return a string response for the request.
	 */
	public String getResponse() {
		return response;
	}
}