package sae.http;

import java.rmi.Remote;
import java.rmi.RemoteException;

import sae.bd.ServiceBd;
import sae.proxyHttp.ServiceProxy;

/**
 * ServiceServeurHttp
 */
public interface ServiceServeurHttp extends Remote {

	public void enregisterServiceProxy(ServiceProxy service) throws RemoteException;

	public void enregisterServiceBd(ServiceBd service) throws RemoteException;

	final String SERVICE_NAME = "LeServiceHttpServeurLuiMeme";
	
}
