package sae.http;

import java.util.Map;
import java.util.Map.Entry;

import org.json.JSONArray;
import org.json.JSONObject;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

import sae.bd.ServiceBd;
import sae.proxyHttp.ServiceProxy;

import static java.util.Map.entry;

import java.io.IOException;
import java.io.OutputStream;

/**
 * Endpoints
 */
public class Endpoints {


	private static Map<String, String> proxy_endpoints = Map.ofEntries(
			entry("/incidents", "https://carto.g-ny.org/data/cifs/cifs_waze_v2.json")
		);

	private static enum DB_ENDPOINTS {
		DB_RESTOS("/restos")
			;

		private final String endpoint; 
		DB_ENDPOINTS(final String c) {this.endpoint = c;}
		@Override
		public String toString() {
			return endpoint;
		}


	} 
	private static void sendJson(HttpExchange exchange, String json) throws IOException {
		OutputStream os = exchange.getResponseBody();
		if (json ==null) {
			sendError(exchange);
		}

		byte[] response = json.getBytes();
		exchange.getResponseHeaders().set("Content-Type", "application/json");
		exchange.sendResponseHeaders(200, response.length);
		os.write(response);
		os.close();


	}

	private static void sendError(HttpExchange exchange) throws IOException {
		exchange.sendResponseHeaders(400, -1);

	}

	public static void endpoints(HttpServer server) {
		server.createContext("/", new HttpHandler() {
			@Override
			public void handle(HttpExchange exchange) throws IOException {
				try {
					OutputStream os = exchange.getResponseBody();

					JSONObject json = new JSONObject();
					JSONArray endpoints = new JSONArray();

					proxy_endpoints.keySet().forEach(endpoints::put);
					for (DB_ENDPOINTS values : DB_ENDPOINTS.values()) {
						endpoints.put(values.toString());
					}

					json.put("endpoints", endpoints);

					sendJson(exchange, json.toString());
				} catch (Exception e) {
					e.printStackTrace();
					sendError(exchange);
				}
			}
		});

	}

	public static void proxy(HttpServer server, ServiceProxy proxy) {
		for (Entry<String, String> e : proxy_endpoints.entrySet()) {
			server.createContext(e.getKey(), new HttpHandler() {
				@Override
				public void handle(HttpExchange exchange) throws IOException {
					label :  {
						if (proxy==null) {
							sendError(exchange);
							break label;
						}

						String json = proxy.getJson(e.getValue());
						sendJson(exchange, json);
					}
				}
			});
		}
	}

	public static void db(HttpServer server, ServiceBd bd) {
		server.createContext(DB_ENDPOINTS.DB_RESTOS.toString(), new HttpHandler() {
			@Override
			public void handle(HttpExchange exchange) throws IOException {
				label : {
					if (bd == null) {
						sendError(exchange);
						break label;
					}
					String json = bd.getRestos();
					sendJson(exchange, json);
				}
			}
		});
	}
}
