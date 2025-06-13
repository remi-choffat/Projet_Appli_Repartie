package sae.bd;

import java.rmi.RemoteException;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.Objects;

import org.json.*;


/**
 * Gestion de la base de données
 */
public class Bd implements ServiceBd {

    /**
     * Connexion à la base de données
     */
    Connection con;

    /**
     * Préparation de la requête SQL
     */
    PreparedStatement stmt;


    /**
     * Constructeur de la classe Bd
     *
     * @param url      URL de la base de données
     * @param user     Nom d'utilisateur de la base de données
     * @param password Mot de passe de la base de données
     */
    public Bd(String url, String user, String password) {
        try {
            Class.forName("oracle.jdbc.driver.OracleDriver");
            con = DriverManager.getConnection(url, user, password);
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
    }


    /**
     * Récupère la liste des restaurants
     *
     * @return Liste des restaurants au format JSON
     * @throws RemoteException Si une erreur de communication se produit
     */
    public String getRestos() throws RemoteException {
        try {
            stmt = con.prepareStatement("SELECT * FROM RESTAURANTS");
            ResultSet rs = stmt.executeQuery();
            return Objects.requireNonNull(resToJson(rs, "restaurants")).toString();

        } catch (SQLException e) {
            System.err.println("Problème SQL : " + e.getMessage());
        }
        return null;
    }


    /**
     * Récupère les tables libres d'un restaurant à une heure donnée
     *
     * @param idResto Identifiant du restaurant
     * @param heure   Heure à laquelle on cherche les tables libres
     * @return Liste des tables libres au format JSON
     * @throws RemoteException Si une erreur de communication se produit
     */
    @Override
    public String getTablesLibres(int idResto, LocalDateTime heure) throws RemoteException {
        try {
            stmt = con.prepareStatement(
					"""
					SELECT t.NUMTABLE, t.IDRESTO, t.NOM 
					FROM TABLES_RESTO t 
					INNER JOIN RESTAURANTS r ON r.ID = t.IDRESTO 
					WHERE t.IDRESTO = ? 
					AND (
						(
						TO_DATE(r.HEUREOUVERTURE, 'HH24:MI') > TO_DATE(r.HEUREFERMETURE, 'HH24:MI') AND 
							(
								TO_DATE(?, 'HH24:MI') < TO_DATE(r.HEUREFERMETURE, 'HH24:MI')
							OR 
								TO_DATE(?, 'HH24:MI') > TO_DATE(r.HEUREOUVERTURE, 'HH24:MI')
							)
						)
						OR 	
							(
							TO_DATE(r.HEUREOUVERTURE, 'HH24:MI') <= TO_DATE(r.HEUREFERMETURE, 'HH24:MI')  
							AND
								(TO_DATE(?, 'HH24:MI') BETWEEN 
									TO_DATE(r.HEUREOUVERTURE, 'HH24:MI') AND 
									(CASE WHEN r.HEUREFERMETURE = '00:00' THEN TO_DATE('23:59', 'HH24:MI') ELSE TO_DATE(r.HEUREFERMETURE, 'HH24:MI') END)
								)
							)
						)
					MINUS 
					SELECT t.NUMTABLE, t.IDRESTO, t.NOM 
					FROM TABLES_RESTO t 
					INNER JOIN RESERVATIONS res ON res.NUMTABLE = t.NUMTABLE 
					WHERE ? BETWEEN res.HEUREDEBUT AND res.HEUREFIN
					"""
            );
            stmt.setInt(1, idResto);
            stmt.setString(2, heure.getHour() + ":" + heure.getMinute());
            stmt.setString(3, heure.getHour() + ":" + heure.getMinute());
            stmt.setString(4, heure.getHour() + ":" + heure.getMinute());
            stmt.setTimestamp(5, Timestamp.valueOf(heure));
            ResultSet rs = stmt.executeQuery();
            return Objects.requireNonNull(resToJson(rs, "tables")).toString();
        } catch (SQLException e) {
            System.err.println("Problème SQL : " + e.getMessage());
        }
        return null;
    }


    /**
     * Réserve une table dans un restaurant
     *
     * @param nom      Nom du client
     * @param prenom   Prénom du client
     * @param convives Nombre de convives
     * @param num      Numéro de téléphone du client
     * @param date     Date et heure de la réservation
     * @param tableid  Identifiant de la table à réserver
     * @return Un tableau contenant un message de confirmation et le statut HTTP
     */
    @Override
    public Object[] reserver(String nom, String prenom, int convives, String num, LocalDateTime date, int tableid) {
        String message = "Erreur lors de la réservation.";
        int httpStatus = 500;
        try {
            CallableStatement cstmt = con.prepareCall("{call reserver_table(?, ?, ?, ?, ?, ?, ?, ?, ?) }");
            cstmt.registerOutParameter(8, Types.VARCHAR);
            cstmt.setInt(1, tableid);
            cstmt.setString(2, nom);
            cstmt.setString(3, prenom);
            cstmt.setInt(4, convives);
            cstmt.setString(5, num);
            cstmt.setTimestamp(6, Timestamp.valueOf(date));
            cstmt.setTimestamp(7, Timestamp.valueOf(date.plusHours(2)));
            cstmt.registerOutParameter(8, Types.VARCHAR);
            cstmt.registerOutParameter(9, Types.INTEGER);
            cstmt.execute();
            message = cstmt.getString(8);
            httpStatus = cstmt.getInt(9);
        } catch (SQLException e) {
            System.err.println("Problème SQL : " + e.getMessage());
        }
        return new Object[]{message, httpStatus};
    }


    /**
     * Convertit un ResultSet en objet JSON
     *
     * @param rs   ResultSet à convertir
     * @param name Nom de l'objet JSON à créer
     * @return Objet JSON contenant les données du ResultSet
     */
    private JSONObject resToJson(ResultSet rs, String name) {
        try {
            JSONObject res = new JSONObject(); // objet JSON final
            JSONArray ja = new JSONArray(); // liste des restaurants

            ResultSetMetaData rsm = rs.getMetaData();

            while (rs.next()) {
                JSONObject jo = new JSONObject(); // représente un restaurant
                for (int i = 1; i <= rsm.getColumnCount(); i++) {
                    jo.put(rsm.getColumnName(i).toLowerCase(), rs.getString(i));
                }
                ja.put(jo);
            }

            res.put(name, ja);
            return res;
        } catch (SQLException e) {
            System.err.println("Problème SQL : " + e.getMessage());
        }
        return null;
    }

}
