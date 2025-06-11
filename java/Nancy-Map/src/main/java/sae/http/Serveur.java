package sae.http;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.rmi.AlreadyBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.HashMap;
import java.util.Map.Entry;

import org.json.JSONObject;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

import sae.proxyHttp.ServiceProxy;

/**
 * Serveur
 */
public class Serveur implements ServiceServeurHttp {



	HashMap<String, String> proxy_endpoints = new HashMap<>();
	HashMap<String, String> db_endpoints = new HashMap<>();

	ServiceProxy proxy;


	private HttpServer server;

	public Serveur(int port) throws IOException {
		InetSocketAddress inet = new InetSocketAddress(port);
		server = HttpServer.create(inet, 0);

		proxy_endpoints.put("/incidents", "https://carto.g-ny.org/data/cifs/cifs_waze_v2.json");

		db_endpoints.put("", "");

	}


	public void start(String regip, int regport) throws RemoteException {
		server.start();
		Registry reg = LocateRegistry.getRegistry(regip, regport);
		try {
			reg.bind(ServiceServeurHttp.SERVICE_NAME, (ServiceServeurHttp)UnicastRemoteObject.exportObject(this, 0));
		} catch (AlreadyBoundException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public void enregisterServiceProxy(ServiceProxy service) throws RemoteException {
		this.proxy = service;

		for (Entry<String, String> e : proxy_endpoints.entrySet()) {
			server.removeContext(e.getKey());
			server.createContext(e.getKey(), new HttpHandler() {
				@Override
				public void handle(HttpExchange exchange) throws IOException {
					OutputStream os = exchange.getResponseBody();


					label :  {
						JSONObject json = proxy.getJson(e.getValue());
						byte[] response = json.toString().getBytes();
						exchange.sendResponseHeaders(200, response.length);
						os.write(response);
					}
					os.close();
				}
				});
		}
	}

}
