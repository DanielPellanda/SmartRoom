package room.service.channel.http;

public class HttpResponse {
	private final int code;
	private final long size;
	private final String response;
	
	public HttpResponse(final int code, final long size, final String response) {
		this.code = code;
		this.size = size;
		this.response = response;
	}
	
	public int getEsitCode() {
		return code;
	}
	
	public long getSize() {
		return size;
	}
	
	public String getResponse() {
		return response;
	}
}