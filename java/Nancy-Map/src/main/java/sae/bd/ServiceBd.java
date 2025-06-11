package sae.bd;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.time.LocalDateTime;

public interface ServiceBd extends Remote{
	String getRestos() throws RemoteException;
	String getTablesLibres(int idResto, LocalDateTime heure) throws RemoteException;
}
