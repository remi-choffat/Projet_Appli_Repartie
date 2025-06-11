package sae.http.handlers;

import java.io.IOException;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import sae.bd.ServiceBd;

/**
 * DbRestosHandler
 */
public class DbRestosHandler implements HttpHandler {


	ServiceBd bd;

	public DbRestosHandler(ServiceBd bd) {
		this.bd = bd;
	}

	@Override
	public void handle(HttpExchange exchange) throws IOException {
		label : {
			if (bd == null) {
				Utils.sendError(exchange);
				break label;
			}
			String json = bd.getRestos();
			Utils.sendJson(exchange, json.toString());
		}
	}
}
