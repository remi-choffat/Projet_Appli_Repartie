package sae.http.handlers;

import java.io.IOException;
import java.io.OutputStream;

import com.sun.net.httpserver.HttpExchange;

/**
 * Classe utilitaire pour envoyer des réponses HTTP.
 */
public class Utils {

    /**
     * Envoie une réponse HTTP avec un texte brut.
     *
     * @param exchange l'échange HTTP contenant la requête et la réponse.
     * @param text     le texte à envoyer dans la réponse.
     * @throws IOException si une erreur d'entrée/sortie se produit lors de l'envoi de la réponse.
     */
    protected static void sendText(HttpExchange exchange, String text) throws IOException {
        OutputStream os = exchange.getResponseBody();
        if (text == null) {
            sendError(exchange);
            return;
        }
        exchange.getResponseHeaders().add("Access-Control-Allow-Origin", "*");

        byte[] response = text.getBytes();
        exchange.getResponseHeaders().set("Content-Type", "text/plain");
        exchange.sendResponseHeaders(200, response.length);
        os.write(response);
        os.close();
    }


    /**
     * Envoie une réponse HTTP avec un statut 200 OK sans contenu.
     *
     * @param exchange l'échange HTTP contenant la requête et la réponse.
     * @param json     le JSON à envoyer dans la réponse (peut être null).
     * @throws IOException si une erreur d'entrée/sortie se produit lors de l'envoi de la réponse.
     */
    protected static void sendOk(HttpExchange exchange, String json) throws IOException {
        exchange.sendResponseHeaders(200, -1);
    }


    /**
     * Envoie une réponse HTTP avec un JSON.
     *
     * @param exchange l'échange HTTP contenant la requête et la réponse.
     * @param json     le JSON à envoyer dans la réponse (peut être null).
     * @throws IOException si une erreur d'entrée/sortie se produit lors de l'envoi de la réponse.
     */
    protected static void sendJson(HttpExchange exchange, String json) throws IOException {
        OutputStream os = exchange.getResponseBody();
        if (json == null) {
            sendError(exchange);
            return;
        }

        // Ajoute les en-têtes CORS
        exchange.getResponseHeaders().set("Access-Control-Allow-Origin", "*");
        exchange.getResponseHeaders().add("Access-Control-Allow-Methods", "GET, POST, OPTIONS");
        exchange.getResponseHeaders().add("Access-Control-Allow-Headers", "Content-Type");
        exchange.getResponseHeaders().add("Access-Control-Allow-Credentials", "true");

        // Gère les requêtes OPTIONS pour le CORS
        if ("OPTIONS".equalsIgnoreCase(exchange.getRequestMethod())) {
            exchange.sendResponseHeaders(204, -1); // Pas de contenu
            return;
        }

        byte[] response = json.getBytes();
        exchange.getResponseHeaders().set("Content-Type", "application/json");
        exchange.sendResponseHeaders(200, response.length);
        os.write(response);
        os.close();
    }


    /**
     * Envoie une réponse d'erreur HTTP avec un statut 400 Bad Request.
     *
     * @param exchange l'échange HTTP contenant la requête et la réponse.
     * @param err      l'exception à enregistrer dans la console.
     * @throws IOException si une erreur d'entrée/sortie se produit lors de l'envoi de la réponse.
     */
    protected static void sendError(HttpExchange exchange, Throwable err) throws IOException {
        sendError(exchange);
        System.err.println(err.getMessage());
    }


    /**
     * Envoie une réponse d'erreur HTTP avec un statut 400 Bad Request.
     *
     * @param exchange l'échange HTTP contenant la requête et la réponse.
     * @throws IOException si une erreur d'entrée/sortie se produit lors de l'envoi de la réponse.
     */
    protected static void sendError(HttpExchange exchange) throws IOException {

        exchange.getResponseHeaders().set("Access-Control-Allow-Origin", "*");

        System.err.println(">> ERROR AT ");
        for (StackTraceElement stackTrace : Thread.currentThread().getStackTrace()) {
            System.err.println("\t" + stackTrace.toString());
        }

        exchange.sendResponseHeaders(400, -1);
    }
}
