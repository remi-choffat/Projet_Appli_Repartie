package sae.http;

import java.rmi.Remote;
import java.rmi.RemoteException;

import sae.proxyHttp.ServiceProxy;

/**
 * ServiceServeurHttp
 */
public interface ServiceServeurHttp extends Remote {

	public void enregisterService(ServiceProxy service) throws RemoteException;


	final String SERVICE_NAME = "LeServiceHttpServeurLuiMeme";
	
}
