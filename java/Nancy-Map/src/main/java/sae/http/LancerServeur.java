package sae.http;

/**
 * Lance le serveur HTTP.
 */
public class LancerServeur {

    public static void main(String[] args) throws Exception {
        Serveur serv = new Serveur(Integer.parseInt(args[2]));

        serv.start(args[0], Integer.parseInt(args[1]));
    }

}
