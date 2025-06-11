package sae.http.handlers;

import java.io.IOException;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONObject;

import com.sun.net.httpserver.HttpContext;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

/**
 * EndpointHandler
 */
public class EndpointHandler implements HttpHandler {

	private HashMap<String, HttpContext> endpoints;

	public EndpointHandler(HashMap<String, HttpContext> contexts) {
		this.endpoints = contexts;
	}

	@Override
	public void handle(HttpExchange exchange) throws IOException {

		JSONObject json = new JSONObject();
		JSONArray arr = new JSONArray();
	
		endpoints.keySet().forEach(arr::put);
		json.put("endpoints", arr);

		Utils.sendJson(exchange, json.toString());	
	}

	
}
