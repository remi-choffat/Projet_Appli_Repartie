package sae.proxyHttp;

import java.rmi.Remote;
import java.rmi.RemoteException;

import org.json.JSONObject;

public interface ServiceProxy extends Remote {
    String getJson(String uri) throws RemoteException;
}
