package sae.http.handlers;

import java.io.IOException;
import java.io.OutputStream;

import com.sun.net.httpserver.HttpExchange;

/**
 * Utils
 */
public class Utils {

    protected static void sendText(HttpExchange exchange, String text) throws IOException {
        OutputStream os = exchange.getResponseBody();
        if (text == null) {
            sendError(exchange);
            return;
        }
        exchange.getResponseHeaders().add("Access-Control-Allow-Origin", "*");

        byte[] response = text.getBytes();
        exchange.getResponseHeaders().set("Content-Type", "text/plain");
        exchange.sendResponseHeaders(200, response.length);
        os.write(response);
        os.close();
    }

    protected static void sendOk(HttpExchange exchange, String json) throws IOException {
        exchange.sendResponseHeaders(200, -1);
    }

    protected static void sendJson(HttpExchange exchange, String json) throws IOException {
        OutputStream os = exchange.getResponseBody();
        if (json == null) {
            sendError(exchange);
            return;
        }

				exchange.getResponseHeaders().set("Access-Control-Allow-Origin", "*");
				// String origin = exchange.getRequestHeaders().getFirst("Origin");
				// if (origin != null && (origin.contains("localhost") || origin.contains("https://webetu.iutnc.univ-lorraine.fr"))) {
				// 	exchange.getResponseHeaders().set("Access-Control-Allow-Origin", origin);
				// 	System.out.println("cors ok " + origin);
				// } else {
				// 	exchange.getResponseHeaders().set("Access-Control-Allow-Origin", "null");
				// }
				exchange.getResponseHeaders().add("Access-Control-Allow-Methods", "GET, POST, OPTIONS");
				exchange.getResponseHeaders().add("Access-Control-Allow-Headers", "Content-Type");
				exchange.getResponseHeaders().add("Access-Control-Allow-Credentials", "true");

				if ("OPTIONS".equalsIgnoreCase(exchange.getRequestMethod())) {
					exchange.sendResponseHeaders(204, -1); // Pas de contenu
					return;
				}

        byte[] response = json.getBytes();
        exchange.getResponseHeaders().set("Content-Type", "application/json");
        exchange.sendResponseHeaders(200, response.length);
        os.write(response);
        os.close();
    }


    protected static void sendError(HttpExchange exchange, Throwable err) throws IOException {
        sendError(exchange);
        System.err.println(err.getMessage());
    }

    protected static void sendError(HttpExchange exchange) throws IOException {

        System.err.println(">> ERROR AT ");
        for (StackTraceElement stackTrace : Thread.currentThread().getStackTrace()) {
            System.err.println("\t" + stackTrace.toString());

        }

        exchange.sendResponseHeaders(400, -1);
    }
}
