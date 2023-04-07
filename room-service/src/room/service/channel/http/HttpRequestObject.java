package room.service.channel.http;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Map.Entry;

import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;

/**
 * A class used for handle http requests and data related to it.
 */
public class HttpRequestObject {
	private final Map<String, String> uriParameters = new HashMap<>();
	private final String path;
	private final String headers;
	private Optional<String> postData = Optional.empty();
	
	public HttpRequestObject(final HttpExchange ex) {
		path = ex.getRequestURI().getRawPath();
		headers = parseHeaders(ex.getRequestHeaders());
		parseQuery(uriParameters, ex.getRequestURI().getRawQuery());
		try {
			postData = Optional.ofNullable(new BufferedReader(new InputStreamReader(ex.getRequestBody(), "utf-8")).readLine());
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * @param h the headers to parse.
	 * @return a string with headers sent by parameters.
	 */
	public static String parseHeaders(final Headers h) {
		String response = "";
		for (Entry<String, List<String>> entry : h.entrySet()){
			response += entry.toString() + "\n";
		}
		return response;
	}
	
	
	/**
	 * @param parameters a map object where the query should be parsed.
	 * @param query the string query to parse for uri parameters.
	 */
	public static void parseQuery(final Map<String, String> parameters, final String query) {
		if (Optional.ofNullable(query).isEmpty()) {
			return;
		}
		
        for (String par : query.split("&")) {
        	if (par.split("=").length >= 2) {
            	parameters.put(par.split("=")[0], par.split("=")[1]);
        	}
        }
	}
	
	/**
	 * @return a string with the uri requested.
	 */
	public String getPath() {
		return path;
	}
	
	/**
	 * @return a string with the headers of the request.
	 */
	public String getHeaders() {
		return headers;
	}
	
	/**
	 * @return a string with the post data of the request.
	 */
	public Optional<String> getPostData() {
		return postData;
	}
	
	/**
	 * @return the get parameters of the request.
	 */
	public Map<String, String> getParameters() {
		return uriParameters;
	}
	
	/**
	 * @return true if the request has parameters, false if not.
	 */
	public boolean hasParameters() {
		return !uriParameters.isEmpty();
	}
}