package sae.http.handlers;

import java.io.IOException;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONObject;

import com.sun.net.httpserver.HttpContext;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

/**
 * Handler pour lister les endpoints disponibles du serveur HTTP.
 */
public class EndpointHandler implements HttpHandler {

    /**
     * Map des endpoints disponibles.
     */
    private final HashMap<String, HttpContext> endpoints;

    /**
     * Constructeur de l'EndpointHandler.
     *
     * @param contexts Map des contextes HTTP, où chaque clé est un endpoint et la valeur est le contexte associé.
     */
    public EndpointHandler(HashMap<String, HttpContext> contexts) {
        this.endpoints = contexts;
    }


    /**
     * Gère les requêtes HTTP pour lister les endpoints disponibles.
     *
     * @param exchange l'échange HTTP contenant la requête et la réponse.
     * @throws IOException si une erreur d'entrée/sortie se produit lors du traitement de la requête.
     */
    @Override
    public void handle(HttpExchange exchange) throws IOException {
        JSONObject json = new JSONObject();
        JSONArray arr = new JSONArray();

        endpoints.keySet().forEach(arr::put);
        json.put("endpoints", arr);

        Utils.sendJson(exchange, json.toString());
    }

}
