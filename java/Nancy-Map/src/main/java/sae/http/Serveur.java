package sae.http;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import static java.util.Map.entry;

import com.sun.net.httpserver.HttpContext;
import com.sun.net.httpserver.HttpServer;

import sae.bd.ServiceBd;
import sae.http.handlers.DbRestosHandler;
import sae.http.handlers.EndpointHandler;
import sae.http.handlers.ProxyHandler;
import sae.proxyHttp.ServiceProxy;

/**
 * Serveur
 */
public class Serveur implements ServiceServeurHttp {

    private static final Map<String, String> proxy_endpoints = Map.ofEntries(
            entry("/incidents", "https://carto.g-ny.org/data/cifs/cifs_waze_v2.json")
    );

    public ServiceProxy proxy;
    public ServiceBd bd;

    private final HashMap<String, HttpContext> contexts = new HashMap<>();

    public HttpContext getContext(String key) {
        HttpContext context;
        if (contexts.containsKey(key)) {
            context = contexts.get(key);
        } else {
            context = server.createContext(key);
            contexts.put(key, context);
        }
        return context;
    }

    private final HttpServer server;


    public Serveur(int port) throws IOException {
        InetSocketAddress inet = new InetSocketAddress(port);
        server = HttpServer.create(inet, 0);

        getContext("/").setHandler(new EndpointHandler(contexts));
        getContext("/webetu").setHandler(new EndpointHandler(contexts));
    }

    public void start(String regip, int regport) throws RemoteException {
        server.start();
        Registry reg = LocateRegistry.getRegistry(regip, regport);
        reg.rebind(ServiceServeurHttp.SERVICE_NAME, (ServiceServeurHttp) UnicastRemoteObject.exportObject(this, 0));

        System.out.format("Service started : '%s' on registry %s:%d\n", ServiceServeurHttp.SERVICE_NAME, regip, regport);
        System.out.format("Server started, listening on port %d...\n", server.getAddress().getPort());
    }

    @Override
    public void enregisterServiceProxy(ServiceProxy service) throws RemoteException {
        this.proxy = service;
        registerProxyContexts();
    }

    private void registerProxyContexts() {
        for (Entry<String, String> e : proxy_endpoints.entrySet()) {
            getContext(e.getKey()).setHandler(new ProxyHandler(this, e.getValue()));
        }
    }

    @Override
    public void enregisterServiceBd(ServiceBd service) throws RemoteException {
        this.bd = service;
        registerDbContexts();
    }

    private void registerDbContexts() {
        getContext("/restos").setHandler(new DbRestosHandler(this));
    }

}
