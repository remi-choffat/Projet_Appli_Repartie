package sae.bd;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.time.LocalDateTime;

/**
 * Interface pour le service de base de données
 */
public interface ServiceBd extends Remote {

    /**
     * Récupère la liste des restaurants
     *
     * @return Liste des restaurants au format JSON
     * @throws RemoteException Si une erreur de communication se produit
     */
    String getRestos() throws RemoteException;

    /**
     * Récupère la liste des tables libres pour un restaurant à une heure donnée
     *
     * @param idResto ID du restaurant
     * @param heure   Heure pour laquelle on cherche les tables libres
     * @return Liste des tables libres au format JSON
     * @throws RemoteException Si une erreur de communication se produit
     */
    String getTablesLibres(int idResto, LocalDateTime heure) throws RemoteException;

    /**
     * Réserve une table dans un restaurant
     *
     * @param nom      nom du client
     * @param prenom   prénom du client
     * @param convives nombre de convives
     * @param num      numéro de téléphone du client
     * @param date     date et heure de la réservation
     * @param tableid  identifiant de la table à réserver
     * @return Un tableau contenant un message de confirmation et le statut HTTP
     * @throws RemoteException Si une erreur de communication se produit
     */
    Object[] reserver(String nom, String prenom, int convives, String num, LocalDateTime date, int tableid) throws RemoteException;
}
