package room.service.channel.http;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.file.AccessDeniedException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.function.Function;

import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

public class HttpChannel {
	
	private boolean hasStarted = false;
	private boolean setupComplete = false;
	private final String servername;
	
	private final InetSocketAddress socketAddress;
	private HttpServer server;
	
	public HttpChannel (final String servername, final InetSocketAddress fullAddress) {
		this.socketAddress = fullAddress;
		this.servername = servername;
	}
	
	public void setup() {
		try {
			server = HttpServer.create(socketAddress, 0);
			server.createContext("/", new BasicHandler(req -> {
				String response = "<h1>Error 500</h1><br/>Internal server error.";
				long size = response.length();
				int code = 500;
				try {
					final Path path = Paths.get(System.getProperty("user.dir"), "res", req.getPath().substring(1));
					response = Files.readString(path);
					size = Files.size(path);
					code = 200;
					//System.out.println("GET: " + req.getPath());
				} catch (NoSuchFileException e) {
					System.err.println(e.toString());
					response = "<h1>Error 404</h1><br/>Resource not found.";
					size = response.length();
					code = 404;
				} catch (AccessDeniedException e) {
					System.err.println(e.toString());
					response = "<h1>Error 403</h1><br/>Resource access denied.";
					size = response.length();
					code = 403;
				} catch (Exception e) {
					e.printStackTrace();
				}
				return new HttpResponse(code, size, response);
			}));
			server.createContext("/head", new BasicHandler(req -> new HttpResponse(200, req.getHeaders().length(), req.getHeaders())));
			server.setExecutor(null);
			setupComplete = true;
			
		} catch (IOException e) {
			System.err.println(e.toString());
		}
	}
	
	public void start() {
		if (setupComplete) {
			server.start();
			hasStarted = true;
			System.out.println(servername + " HTTP Server starting on " + getFullServerAddress());
		}
	}

	public void close() {
		if (setupComplete) {
			server.stop(1);
		}
	}
	
	public void addHandler(final String uri, final Function<HttpRequestObject, HttpResponse> response) {
		if (!hasStarted) {
			server.createContext(uri, new BasicHandler(response));
		}
	}
	
	public String getFullServerAddress() {
		return socketAddress.getAddress().getHostAddress() + ":" + socketAddress.getPort();
	}
	
	public class BasicHandler implements HttpHandler {
		
		private final Function<HttpRequestObject, HttpResponse> processResponse;
		
		public BasicHandler(final Function<HttpRequestObject, HttpResponse> processResponse) {
			this.processResponse = processResponse;
		}

		@Override
		public void handle(final HttpExchange exchange) throws IOException {
			HttpRequestObject obj = new HttpRequestObject(exchange);
			HttpResponse response = processResponse.apply(obj);
			exchange.sendResponseHeaders(response.getEsitCode(), response.getSize());
			
            OutputStream os = exchange.getResponseBody();
            os.write(response.getResponse().getBytes());
            os.close();
		}
	}
}
