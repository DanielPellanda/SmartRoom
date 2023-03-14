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
	
	private final InetSocketAddress socketAddress;
	private final HttpServer server;
	
	private final String servername;
	private boolean hasStarted = false;
	
	public HttpChannel (final String servername, final InetSocketAddress fullAddress) throws IOException{
		this.socketAddress = fullAddress;
		this.servername = servername;
		
		server = HttpServer.create(socketAddress, 0);
		server.createContext("/", new BasicHandler(req -> getHttpResource(req)));
		server.createContext("/head", new BasicHandler(req -> new HttpResponse(200, req.getHeaders().length(), req.getHeaders())));
		server.setExecutor(null);
	}
	
	public void start() {
		server.start();
		hasStarted = true;
		System.out.println(servername + " HTTP Server starting on " + getFullServerAddress());
	}

	public void close() {
		if (hasStarted) {
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
	
	private HttpResponse getHttpResource(final HttpRequestObject req) {
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
	}
	
	public class BasicHandler implements HttpHandler {
		
		private final Function<HttpRequestObject, HttpResponse> processResponse;
		
		public BasicHandler(final Function<HttpRequestObject, HttpResponse> processResponse) {
			this.processResponse = processResponse;
		}

		@Override
		public void handle(final HttpExchange exchange) throws IOException {
			final HttpRequestObject obj = new HttpRequestObject(exchange);
			final HttpResponse response = processResponse.apply(obj);
			exchange.sendResponseHeaders(response.getEsitCode(), response.getSize());
			
            OutputStream os = exchange.getResponseBody();
            os.write(response.getResponse().getBytes());
            os.close();
            
            System.out.println(servername + " received HTTP request for " + obj.getPath() + ". Response sent: \n" + response.getResponse());
		}
	}
}
