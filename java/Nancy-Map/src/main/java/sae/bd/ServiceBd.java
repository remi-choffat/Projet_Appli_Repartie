package sae.bd;

import java.rmi.Remote;

import org.json.JSONObject;

public interface ServiceBd extends Remote{
	JSONObject getRestos();
}
