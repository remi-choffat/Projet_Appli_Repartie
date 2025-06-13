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
 * Serveur HTTP
 */
public class Serveur implements ServiceServeurHttp {

    /**
     * Map des endpoints proxy disponibles.
     */
    private static final Map<String, String> proxy_endpoints = Map.ofEntries(
            entry("/incidents", "https://carto.g-ny.org/data/cifs/cifs_waze_v2.json")
    );

    /**
     * Proxy pour les requêtes HTTP.
     */
    public ServiceProxy proxy;

    /**
     * Service de base de données.
     */
    public ServiceBd bd;

    /**
     * Map des contextes HTTP créés pour chaque endpoint.
     */
    private final HashMap<String, HttpContext> contexts = new HashMap<>();

    /**
     * Serveur HTTP.
     */
    private final HttpServer server;


    /**
     * Récupère ou crée un contexte HTTP pour un endpoint donné.
     *
     * @param key L'endpoint pour lequel on veut obtenir le contexte.
     * @return Le contexte HTTP associé à l'endpoint.
     */
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


    public Serveur(int port) throws IOException {
        InetSocketAddress inet = new InetSocketAddress(port);
        server = HttpServer.create(inet, 0);

        getContext("/").setHandler(new EndpointHandler(contexts));
    }


    /**
     * Démarre le serveur HTTP et l'enregistre dans le registre RMI.
     *
     * @param regip   l'adresse IP du registre RMI
     * @param regport le port du registre RMI
     * @throws RemoteException si une erreur de communication RMI se produit
     */
    public void start(String regip, int regport) throws RemoteException {
        server.start();
        Registry reg = LocateRegistry.getRegistry(regip, regport);
        reg.rebind(ServiceServeurHttp.SERVICE_NAME, (ServiceServeurHttp) UnicastRemoteObject.exportObject(this, 0));

        System.out.format("Service started : '%s' on registry %s:%d\n", ServiceServeurHttp.SERVICE_NAME, regip, regport);
        System.out.format("Server started, listening on port %d...\n", server.getAddress().getPort());
    }


    /**
     * Enregistre un service proxy HTTP.
     *
     * @param service le service proxy à enregistrer
     * @throws RemoteException si une erreur de communication RMI se produit
     */
    @Override
    public void enregisterServiceProxy(ServiceProxy service) throws RemoteException {
        this.proxy = service;
        registerProxyContexts();
    }


    /**
     * Enregistre les contextes HTTP pour les endpoints proxy.
     */
    private void registerProxyContexts() {
        for (Entry<String, String> e : proxy_endpoints.entrySet()) {
            getContext(e.getKey()).setHandler(new ProxyHandler(this, e.getValue()));
        }
    }


    /**
     * Enregistre un service de base de données.
     *
     * @param service le service de base de données à enregistrer
     * @throws RemoteException si une erreur de communication RMI se produit
     */
    @Override
    public void enregisterServiceBd(ServiceBd service) throws RemoteException {
        this.bd = service;
        registerDbContexts();
    }


    /**
     * Enregistre les contextes HTTP pour les endpoints de la base de données.
     */
    private void registerDbContexts() {
        getContext("/restos").setHandler(new DbRestosHandler(this));
    }

}
