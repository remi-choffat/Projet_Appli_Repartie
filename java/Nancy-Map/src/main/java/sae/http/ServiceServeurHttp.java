package sae.http;

import java.rmi.Remote;
import java.rmi.RemoteException;

import sae.bd.ServiceBd;
import sae.proxyHttp.ServiceProxy;

/**
 * ServiceServeurHttp
 */
public interface ServiceServeurHttp extends Remote {

    void enregisterServiceProxy(ServiceProxy service) throws RemoteException;

    void enregisterServiceBd(ServiceBd service) throws RemoteException;

    String SERVICE_NAME = "LeServiceHttpServeurLuiMeme";

}
