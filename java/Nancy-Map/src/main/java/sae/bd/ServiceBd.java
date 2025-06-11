package sae.bd;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.sql.SQLException;
import java.sql.Timestamp;

public interface ServiceBd extends Remote{
	String getRestos() throws RemoteException;
	String getTablesLibres(int idResto, Timestamp heure) throws RemoteException, SQLException;
}
