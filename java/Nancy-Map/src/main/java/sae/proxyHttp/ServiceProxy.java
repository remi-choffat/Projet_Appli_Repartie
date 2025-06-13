package sae.proxyHttp;

import java.rmi.Remote;
import java.rmi.RemoteException;

import org.json.JSONObject;

/**
 * Interface pour le proxy de service HTTP.
 */
public interface ServiceProxy extends Remote {

    /**
     * Enregistre un service de base de données.
     *
     * @param uri L'URI du service à enregistrer.
     * @return Le JSON sous forme de chaîne de caractères, ou null si la requête échoue.
     * @throws RemoteException Si une erreur de communication se produit lors de la récupération du JSON.
     */
    String getJson(String uri) throws RemoteException;

}
