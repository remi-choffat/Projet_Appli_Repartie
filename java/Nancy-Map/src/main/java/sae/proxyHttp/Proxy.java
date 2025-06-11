package sae.proxyHttp;

import java.net.InetSocketAddress;
import java.net.ProxySelector;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpClient.Redirect;
import java.net.http.HttpClient.Version;
import java.net.http.HttpResponse.BodyHandlers;
import java.rmi.RemoteException;
import java.time.Duration;

import org.json.JSONObject;
public class Proxy implements ServiceProxy{

	HttpClient client;

	public Proxy(){
		client = HttpClient.newBuilder()
			.version(Version.HTTP_1_1)
			.followRedirects(Redirect.NORMAL)
			.connectTimeout(Duration.ofSeconds(20))
			// .proxy(ProxySelector.of(new InetSocketAddress("www-cache", 3128)))
			.build();
	}

	@Override
	public String getJson(String uri) throws RemoteException{
		HttpRequest request = HttpRequest.newBuilder()
			.uri(URI.create(uri))
			.GET()
			.build();
		try {
			HttpResponse<String> response = client.send(request, BodyHandlers.ofString());
			if(response.statusCode() >= 200 && response.statusCode() < 300){
				JSONObject jo = new JSONObject(response.body());
				System.out.println("bon return");
				return jo.toString();
			}
			return null;
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
		return null;
	}
}
