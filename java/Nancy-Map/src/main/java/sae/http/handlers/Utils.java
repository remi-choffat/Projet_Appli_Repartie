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
        // System.out.println(json);
        OutputStream os = exchange.getResponseBody();
        if (json == null) {
            sendError(exchange);
            return;
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
