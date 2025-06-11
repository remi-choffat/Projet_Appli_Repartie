package sae.bd;

import java.rmi.Remote;
import java.rmi.RemoteException;

import org.json.JSONObject;

public interface ServiceBd extends Remote{
	String getRestos() throws RemoteException;
}
