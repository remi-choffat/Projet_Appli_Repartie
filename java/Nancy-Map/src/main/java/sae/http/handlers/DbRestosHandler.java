package sae.http.handlers;

import java.io.IOException;
import java.net.URLDecoder;
import java.sql.Date;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import oracle.net.aso.h;
import sae.bd.ServiceBd;

/**
 * DbRestosHandler
 */
public class DbRestosHandler implements HttpHandler {


	ServiceBd bd;

	public DbRestosHandler(ServiceBd bd) {
		this.bd = bd;
	}

	@Override
	public void handle(HttpExchange exchange) throws IOException {
		try {
			// if (bd == null) {
			// 	Utils.sendError(exchange);
			// 	return;
			// }

			System.out.println("REQ");

			String path = exchange.getRequestURI().toString().split("\\?")[0].replaceAll(exchange.getHttpContext().getPath(), "");
			System.out.println(path);

			if (path.charAt(0) == '/') {
				path = path.substring(1);
			}


			System.out.println(path);

			if (path.isEmpty()) {
				String json = bd.getRestos();
				Utils.sendJson(exchange, json.toString());
				return;
			}

			String[] s = path.split("\\/");


			System.out.println(Arrays.toString(s));

			int idtable = Integer.parseInt(s[0]);

			Map<String, String> queryparam = new HashMap<>();
			for (String split : exchange.getRequestURI().getQuery().split("&")) {
				String[] entry = split.split("=");
				queryparam.put(URLDecoder.decode(entry[0], "UTF-8"), URLDecoder.decode(entry[1], "UTF-8"));
			}
			String query = s[1];

			switch (query) {
				case "tables":
					String date = queryparam.get("date");
					String heure = queryparam.get("heure");
					String[] datespl = date.split("\\-");
					String[] heurspl = heure.split("\\:");

					LocalDateTime d =  LocalDateTime.of(
							Integer.parseInt(datespl[0]),
							Integer.parseInt(datespl[1]),
							Integer.parseInt(datespl[2]),
							Integer.parseInt(heurspl[0]),
							Integer.parseInt(heurspl[0])
							);

					Utils.sendJson(exchange, bd.getTablesLibres(idtable, d));
					break;
				default:
					Utils.sendError(exchange);

			}
		} catch (Exception e) {
			Utils.sendError(exchange);
		}


	}
}
