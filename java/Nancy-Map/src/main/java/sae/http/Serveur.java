package sae.http;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.channels.Pipe.SourceChannel;
import java.rmi.AlreadyBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import com.sun.net.httpserver.HttpServer;

import sae.bd.ServiceBd;
import sae.proxyHttp.ServiceProxy;

/**
 * Serveur
 */
public class Serveur implements ServiceServeurHttp {


	ServiceProxy proxy;
	ServiceBd bd;


	private HttpServer server;

	public Serveur(int port) throws IOException {
		InetSocketAddress inet = new InetSocketAddress(port);
		server = HttpServer.create(inet, 0);

	
		Endpoints.endpoints(server);
		Endpoints.proxy(server, proxy);
		Endpoints.db(server, bd);

		}




	public void start(String regip, int regport) throws RemoteException {
		server.start();
		Registry reg = LocateRegistry.getRegistry(regip, regport);
		reg.rebind(ServiceServeurHttp.SERVICE_NAME, (ServiceServeurHttp)UnicastRemoteObject.exportObject(this, 0));

		System.out.format("Service started : '%s' on registry %s:%d\n", ServiceServeurHttp.SERVICE_NAME, regip, regport);
		System.out.format("Server started, listening on port %d...\n", server.getAddress().getPort());
	}

	@Override
	public void enregisterServiceProxy(ServiceProxy service) throws RemoteException {
		this.proxy = service;
	}

	@Override
	public void enregisterServiceBd(ServiceBd service) throws RemoteException {
		this.bd = service;
	}
	
}
