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
import java.time.Duration;

import org.json.JSONObject;

public class LancerService {
	public static void main(String[] args) {
		HttpClient client = HttpClient.newBuilder()
			.version(Version.HTTP_1_1)
			.followRedirects(Redirect.NORMAL)
			.connectTimeout(Duration.ofSeconds(20))
			.proxy(ProxySelector.of(new InetSocketAddress("www-cache", 3128)))
			.build();
		HttpRequest request = HttpRequest.newBuilder()
			.uri(URI.create("https://carto.g-ny.org/data/cifs/cifs_waze_v2.json"))
			.GET()
			.build();
		try {
			HttpResponse<String> response = client.send(request, BodyHandlers.ofString());
			System.out.println(response.statusCode());
			System.out.println(response.body());
			JSONObject jo = new JSONObject(response.body());
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
	}
}
