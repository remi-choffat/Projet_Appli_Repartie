package sae.proxyHttp;

import org.json.JSONObject;

public interface ServiceProxy {
	JSONObject getJson(String uri);
}
