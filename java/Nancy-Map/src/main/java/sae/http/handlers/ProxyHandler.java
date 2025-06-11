package sae.http.handlers;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map.Entry;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import sae.http.Serveur;
import sae.proxyHttp.ServiceProxy;

/**
 * ProxyHandler
 */
public class ProxyHandler implements HttpHandler {



	ServiceProxy proxy;
	String url;

	public ProxyHandler(ServiceProxy proxy, String url) {
		this.proxy = proxy;
		this.url = url;
	}


	@Override
	public void handle(HttpExchange exchange) throws IOException {
		label :  {
			if (proxy==null) {
				Utils.sendError(exchange);
				break label;
			}

			String json = proxy.getJson(url);
			Utils.sendJson(exchange, json);
		}


	}
}
