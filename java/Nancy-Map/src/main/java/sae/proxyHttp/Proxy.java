package sae.proxyHttp;

import java.net.InetSocketAddress;
import java.net.ProxySelector;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpClient.Redirect;
import java.net.http.HttpClient.Version;
import java.net.http.HttpResponse.BodyHandlers;
import java.rmi.RemoteException;
import java.time.Duration;

import org.json.JSONObject;

/**
 * Classe qui implémente le proxy de service HTTP.
 * Elle utilise HttpClient pour envoyer des requêtes HTTP et récupérer des réponses JSON.
 */
public class Proxy implements ServiceProxy {

    /**
     * Client HTTP utilisé pour envoyer des requêtes.
     */
    HttpClient client;

    public Proxy() {
        client = HttpClient.newBuilder()
                .version(Version.HTTP_1_1)
                .followRedirects(Redirect.NORMAL)
                .connectTimeout(Duration.ofSeconds(20))
                // .proxy(ProxySelector.of(new InetSocketAddress("www-cache", 3128)))
                .build();
    }


    /**
     * Récupère un JSON à partir d'une URI donnée.
     *
     * @param uri URI de la ressource à récupérer.
     * @return Le JSON sous forme de chaîne de caractères, ou null si la requête échoue.
     * @throws RemoteException si une erreur de communication se produit lors de la récupération du JSON.
     */
    @Override
    public String getJson(String uri) throws RemoteException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(uri))
                .GET()
                .build();
        try {
            HttpResponse<String> response = client.send(request, BodyHandlers.ofString());
            if (response.statusCode() >= 200 && response.statusCode() < 300) {
                JSONObject jo = new JSONObject(response.body());
                return jo.toString();
            }
            return null;
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
        return null;
    }

}
