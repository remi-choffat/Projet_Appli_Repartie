package sae.bd;

import java.rmi.RemoteException;
import java.sql.*;
import java.time.LocalDateTime;

import org.json.*;

public class Bd implements ServiceBd{
	Connection con;
	PreparedStatement stmt;
	public Bd(String user, String password){
		String url = "jdbc:oracle:thin:@charlemagne.iutnc.univ-lorraine.fr:1521:infodb";
		try {
			Class.forName("oracle.jdbc.driver.OracleDriver");
			con = DriverManager.getConnection(url,user,password);
		} catch(Exception e) {
			System.out.println(e.getMessage());
		}
	}

	public String getRestos() throws RemoteException{
		try{
			stmt = con.prepareStatement("SELECT * FROM RESTAURANTS");
			ResultSet rs = stmt.executeQuery();
			return resToJson(rs, "restaurants").toString();

		}catch(SQLException e){
			System.out.println("Problème SQL : " + e.getMessage());
		}
		return null;
	}

	@Override
	public String getTablesLibres(int idResto, LocalDateTime heure) throws RemoteException{
		try{
			stmt = con.prepareStatement("SELECT TABLES_RESTO.NUMTABLE, TABLES_RESTO.IDRESTO, TABLES_RESTO.NOM FROM TABLES_RESTO INNER JOIN RESTAURANTS ON RESTAURANTS.ID = TABLES_RESTO.IDRESTO WHERE IDRESTO = ? AND TO_DATE('?:?', 'HH24:MI') between TO_DATE(HEUREOUVERTURE, 'HH24:MI') and TO_DATE(HEUREFERMETURE, 'HH24:MI') MINUS SELECT TABLES_RESTO.NUMTABLE, TABLES_RESTO.IDRESTO, TABLES_RESTO.NOM FROM TABLES_RESTO INNER JOIN RESERVATIONS ON RESERVATIONS.NUMTABLE = TABLES_RESTO.NUMTABLE WHERE ? between HEUREDEBUT and HEUREFIN");
			stmt.setInt(0, idResto);
			stmt.setInt(1, heure.getHour());
			stmt.setInt(2, heure.getMinute());
			stmt.setTimestamp(3, Timestamp.valueOf(heure));
			ResultSet rs = stmt.executeQuery();
			return resToJson(rs, "tables").toString();
		}catch(SQLException e){
			System.out.println("Problème SQL : " + e.getMessage());
		}
		return null;
	}

	private JSONObject resToJson(ResultSet rs, String name){
		try{
		JSONObject res = new JSONObject(); // objet JSON final
		JSONArray ja = new JSONArray(); // liste des restaurants

		ResultSetMetaData rsm = rs.getMetaData();

		while(rs.next()){
			JSONObject jo = new JSONObject(); // représente un restaurant
			for(int i = 1; i <= rsm.getColumnCount(); i++){
				jo.put(rsm.getColumnName(i).toLowerCase(), rs.getString(i));
			}
			ja.put(jo);
		}

		res.put(name, ja);
		return res;
		}catch(SQLException e){
			System.out.println("Problème SQL : " + e.getMessage());
		}
		return null;
	}
}
