package room.service.channel;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.Function;
import java.util.Optional;

import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

public class HttpChannel {
	
	private boolean hasStarted = false;
	private boolean setupComplete = false;
	private final String servername;
	
	private final InetSocketAddress fullAddress;
	private HttpServer server;
	
	public HttpChannel (final String servername, final InetSocketAddress fullAddress) {
		this.fullAddress = fullAddress;
		this.servername = servername;
	}
	
	public void setup() {
		try {
			server = HttpServer.create(fullAddress, 0);
			server.createContext("/", new BasicHandler(p -> "<h1>Connection with the server established</h1>", HttpMethod.NONE));
			server.createContext("/head", new BasicHandler(p -> "", HttpMethod.HEAD));
			server.setExecutor(null);
			setupComplete = true;
			
		} catch (IOException e) {
			System.out.println("An IOException occured, details: \n" + e.getMessage());
		}
	}
	
	public void start() {
		if (setupComplete) {
			server.start();
			hasStarted = true;
			System.out.println(servername + " HTTP Server starting on " + fullAddress.toString());
		}
	}

	public void close() {
		server.stop(1);
	}
	
	public void addHandler(final String Uri, final Function<Optional<Map<String, String>>, String> response, final HttpMethod method) {
		if (!hasStarted) {
			server.createContext(Uri, new BasicHandler(response, method));
		}
	}
	
	public class BasicHandler implements HttpHandler {
		
		private final Function<Optional<Map<String, String>>, String> processResponse;
		private final HttpMethod methodUsed;
		
		public BasicHandler(final Function<Optional<Map<String, String>>, String> processResponse, final HttpMethod methodUsed) {
			this.processResponse = processResponse;
			this.methodUsed = methodUsed;
		}
		
		private Map<String, String> parseQuery(String query) {
	        Map<String, String> parameters = new HashMap<String, String>();
	        for (String par : query.split("&")) {
	        	if (par.split("=").length >= 2) {
	            	parameters.put(par.split("=")[0], par.split("=")[1]);
	        	}
	        }
	        return parameters;
		}
		
		private String generateResponse(HttpExchange ex, HttpMethod method) throws IOException{
			String response = "";
			switch(method) {
				case HEAD:
					for (Entry<String, List<String>> entry : ex.getRequestHeaders().entrySet()){
						response += entry.toString() + "\n";
					}
					response += "\n" + processResponse.apply(Optional.empty());
					break;
				case GET:
					response = processResponse.apply(Optional.of(parseQuery(ex.getRequestURI().getRawQuery())));
					break;
				case POST:
					response = processResponse.apply(Optional.of(parseQuery(new BufferedReader(new InputStreamReader(ex.getRequestBody(), "utf-8")).readLine())));
					break;
				default:
					response = processResponse.apply(Optional.empty());
					break;
			}
			return response;
		}
		
		@Override
		public void handle(HttpExchange exchange) throws IOException {
			String response = generateResponse(exchange, methodUsed);
			exchange.sendResponseHeaders(200, response.length());
			
            OutputStream os = exchange.getResponseBody();
            os.write(response.getBytes());
            os.close();
		}
	}
}
