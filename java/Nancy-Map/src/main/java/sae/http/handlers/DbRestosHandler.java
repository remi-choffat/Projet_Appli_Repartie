package sae.http.handlers;

import java.io.IOException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONObject;
import org.json.JSONTokener;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import sae.http.Serveur;

/**
 * DbRestosHandler
 */
public class DbRestosHandler implements HttpHandler {

    Serveur serveur;

    public DbRestosHandler(Serveur serveur) {
        this.serveur = serveur;
    }

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

            int idtable = Integer.parseInt(s[0]);

            Map<String, String> queryparam = new HashMap<>();
            for (String split : exchange.getRequestURI().getQuery().split("&")) {
                String[] entry = split.split("=");
                queryparam.put(URLDecoder.decode(entry[0], StandardCharsets.UTF_8), URLDecoder.decode(entry[1], StandardCharsets.UTF_8));
            }
            String query = s[1];

            switch (query) {
                case "tables":
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

                case "reserver":

                    JSONTokener tokener = new JSONTokener(exchange.getRequestBody());
                    JSONObject json = new JSONObject(tokener);

                    String res = serveur.bd.reserver(
                            (String) json.get("nom"),
                            (String) json.get("prenom"),
                            Integer.parseInt((String) json.get("convives")),
                            (String) json.get("tel"),
                            LocalDateTime.parse(
                                    (String) json.get("date"),
                                    DateTimeFormatter.ISO_INSTANT
                            ),
                            Integer.parseInt((String) json.get("tableId"))
                    );
                    Utils.sendText(exchange, res);

                    break;

                default:
                    Utils.sendError(exchange);

            }
        } catch (Exception e) {
            Utils.sendError(exchange, e);
        }

    }
}
