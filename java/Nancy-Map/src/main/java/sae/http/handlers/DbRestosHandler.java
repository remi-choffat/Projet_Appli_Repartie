package sae.http.handlers;

import java.io.IOException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONObject;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import sae.http.Serveur;

/**
 * Handler pour les requêtes liées à la base de données des restaurants.
 */
public class DbRestosHandler implements HttpHandler {

    /**
     * Instance du serveur HTTP
     */
    Serveur serveur;

    public DbRestosHandler(Serveur serveur) {
        this.serveur = serveur;
    }


    /**
     * Gère les requêtes HTTP pour les opérations liées aux restaurants.
     *
     * @param exchange L'échange HTTP contenant la requête et la réponse.
     */
    @Override
    public void handle(HttpExchange exchange) throws IOException {

        try {

            if (serveur.bd == null) {
                Utils.sendError(exchange);
                return;
            }

            String path = exchange.getRequestURI().toString().split("\\?")[0].replaceAll(exchange.getHttpContext().getPath(), "");

            if (!path.isEmpty() && path.charAt(0) == '/') {
                path = path.substring(1);
            }

            if (path.isEmpty()) {
                String json = serveur.bd.getRestos();
                Utils.sendJson(exchange, json);
                return;
            }

            String[] s = path.split("/");
            String requete = s[s.length - 1];

            switch (requete) {

                // Récupération des tables libres pour un restaurant à une heure donnée
                case "tables":

                    if (!"GET".equalsIgnoreCase(exchange.getRequestMethod())) {
                        Utils.sendError(exchange);
                        return;
                    }

                    Map<String, String> queryparam = new HashMap<>();
                    int idtable = Integer.parseInt(s[0]);

                    for (String split : exchange.getRequestURI().getQuery().split("&")) {
                        String[] entry = split.split("=");
                        queryparam.put(URLDecoder.decode(entry[0], StandardCharsets.UTF_8), URLDecoder.decode(entry[1], StandardCharsets.UTF_8));
                    }
                    String date = queryparam.get("date");
                    String heure = queryparam.get("heure");
                    String[] datespl = date.split("-");
                    String[] heurspl = heure.split(":");

                    LocalDateTime d = LocalDateTime.of(
                            Integer.parseInt(datespl[0]),
                            Integer.parseInt(datespl[1]),
                            Integer.parseInt(datespl[2]),
                            Integer.parseInt(heurspl[0]),
                            Integer.parseInt(heurspl[1])
                    );

                    Utils.sendJson(exchange, serveur.bd.getTablesLibres(idtable, d));
                    break;


                // Réservation d'une table dans un restaurant
                case "reserver":

                    if (!"POST".equalsIgnoreCase(exchange.getRequestMethod())) {
                        Utils.sendError(exchange);
                        return;
                    }

                    String text = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
                    JSONObject json = new JSONObject(text);

                    String isoDate = (String) json.get("date");
                    DateTimeFormatter formatter = DateTimeFormatter.ISO_INSTANT;
                    Instant instant = Instant.from(formatter.parse(isoDate));
                    LocalDateTime dt = instant.atZone(ZoneId.systemDefault()).toLocalDateTime();

                    Object[] res = serveur.bd.reserver(
                            (String) json.get("nom"),
                            (String) json.get("prenom"),
                            Integer.parseInt((String) json.get("convives")),
                            (String) json.get("tel"),
                            dt,
                            Integer.parseInt((String) json.get("tableId"))
                    );

                    Utils.sendJson(exchange, String.valueOf(new JSONObject()
                            .put("message", res[0])
                            .put("status", res[1]))
                    );

                    break;

                default:
                    Utils.sendError(exchange);

            }

        } catch (Exception e) {
            System.err.println(e.getMessage());
            Utils.sendError(exchange, e);
        }

    }
}
