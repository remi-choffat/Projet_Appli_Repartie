package sae.bd;

import java.sql.*;

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

	public JSONObject getRestos() {
		try{
			JSONObject res = new JSONObject(); // objet JSON final
			JSONArray ja = new JSONArray(); // liste des restaurants
			stmt = con.prepareStatement("SELECT * FROM RESTAURANTS");
			ResultSet rs = stmt.executeQuery();
			ResultSetMetaData rsm = rs.getMetaData();

			while(rs.next()){
				JSONObject jo = new JSONObject(); // représente un restaurant
				for(int i = 1; i <= rsm.getColumnCount(); i++){
					jo.put(rsm.getColumnName(i).toLowerCase(), rs.getString(i));
				}
				ja.put(jo);
			}

			res.put("restaurants", ja);
			return res;

		}catch(SQLException e){
			System.out.println("Problème SQL : " + e.getMessage());
		}
		return null;
	}

	// debug
	public static void main(String[] args) throws SQLException, ClassNotFoundException{
		Connection con;
		PreparedStatement stmt;
		String url = "jdbc:oracle:thin:@charlemagne.iutnc.univ-lorraine.fr:1521:infodb";
		Class.forName("oracle.jdbc.driver.OracleDriver");
		con = DriverManager.getConnection(url,"choffat2u","NancyMapBD2025");

		stmt = con.prepareStatement("SELECT * FROM RESTAURANTS");
		ResultSet rs = stmt.executeQuery();
		while(rs.next()){
			ResultSetMetaData rsm = rs.getMetaData();
			for(int i = 1; i <= rsm.getColumnCount(); i++){
			System.out.println(rsm.getColumnName(i) + " " + rs.getString(i));
			}
		}

	}
}
