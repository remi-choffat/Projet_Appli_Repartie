package sae.http;

/**
 * LancerServeur
 */
public class LancerServeur {

	public static void main(String[] args) throws Exception {
		Serveur serv = new Serveur(8080);

		serv.start(args[0], Integer.parseInt(args[1]));
	}
}
