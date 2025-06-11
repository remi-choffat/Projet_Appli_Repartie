package sae.proxyHttp;

import java.rmi.Remote;

import org.json.JSONObject;

public interface ServiceProxy extends Remote{
	JSONObject getJson(String uri);
}
