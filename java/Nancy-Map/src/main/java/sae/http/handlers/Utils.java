package sae.http.handlers;

import java.io.IOException;
import java.io.OutputStream;

import com.sun.net.httpserver.HttpExchange;

/**
 * Utils
 */
public class Utils {

	protected static void sendJson(HttpExchange exchange, String json) throws IOException {
		OutputStream os = exchange.getResponseBody();
		if (json ==null) {
			sendError(exchange);
		}

		exchange.getResponseHeaders().add("Access-Control-Allow-Origin", "*");

		byte[] response = json.getBytes();
		exchange.getResponseHeaders().set("Content-Type", "application/json");
		exchange.sendResponseHeaders(200, response.length);
		os.write(response);
		os.close();


	}


	protected static void sendError(HttpExchange exchange, Throwable err) throws IOException {
		sendError(exchange);

		err.printStackTrace();
	}

	protected static void sendError(HttpExchange exchange) throws IOException {

		System.err.println(">> ERROR AT ");
		for (StackTraceElement stackTrace : Thread.currentThread().getStackTrace()) {
			System.err.println("\t"+stackTrace.toString());
			
		}

		exchange.sendResponseHeaders(400, -1);

	}
}
