package sae.http;

import java.rmi.Remote;
import java.rmi.RemoteException;

import sae.bd.ServiceBd;
import sae.proxyHttp.ServiceProxy;

/**
 * Interface pour le service HTTP du serveur.
 */
public interface ServiceServeurHttp extends Remote {

    /**
     * Enregistre un proxy de service.
     *
     * @param service Le proxy de service à enregistrer.
     * @throws RemoteException Si une erreur de communication RMI se produit.
     */
    void enregisterServiceProxy(ServiceProxy service) throws RemoteException;

    /**
     * Enregistre un service de base de données.
     *
     * @param service Le service de base de données à enregistrer.
     * @throws RemoteException Si une erreur de communication RMI se produit.
     */
    void enregisterServiceBd(ServiceBd service) throws RemoteException;

    /**
     * Nom du service HTTP du serveur.
     */
    String SERVICE_NAME = "LeServiceHttpServeurLuiMeme";

}
