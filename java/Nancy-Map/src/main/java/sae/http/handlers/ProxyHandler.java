package sae.http.handlers;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map.Entry;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import sae.http.Serveur;
import sae.proxyHttp.ServiceProxy;

/**
 * Handler pour les requêtes proxy HTTP.
 */
public class ProxyHandler implements HttpHandler {

    /**
     * Instance du serveur HTTP
     */
    Serveur serveur;

    /**
     * URL de la ressource à récupérer via le proxy.
     */
    String url;

    public ProxyHandler(Serveur serveur, String url) {
        this.serveur = serveur;
        this.url = url;
    }


    /**
     * Gère les requêtes HTTP pour récupérer des données via un proxy.
     *
     * @param exchange l'échange HTTP contenant la requête et la réponse.
     * @throws IOException si une erreur d'entrée/sortie se produit lors du traitement de la requête.
     */
    @Override
    public void handle(HttpExchange exchange) throws IOException {
        label:
        {
            if (serveur.proxy == null) {
                Utils.sendError(exchange);
                break label;
            }

            String json = serveur.proxy.getJson(url);
            Utils.sendJson(exchange, json);
        }

    }
}
