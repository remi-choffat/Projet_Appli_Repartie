package sae.bd;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

import io.github.cdimascio.dotenv.Dotenv;
import sae.http.ServiceServeurHttp;

public class LancerService {

    public static void main(String[] args) throws RemoteException, NotBoundException {
        if (args.length != 4) {
            System.out.println("4 arguments requis : <ip registry local> <port local> <ip distant> <port distant>");
            return;
        }

        Dotenv dotenv = Dotenv.load();
        String dbUser = dotenv.get("DB_USER");
        String dbPassword = dotenv.get("DB_PASSWORD");
        String dbUrl = dotenv.get("DB_URL");
        if (dbUser == null || dbPassword == null || dbUrl == null) {
            System.err.println("Variables d'environnement DB_USER, DB_PASSWORD et DB_URL doivent être définies.");
            return;
        }

        String local_address = args[0];
        int local_port = Integer.parseInt(args[1]);
        String remote_address = args[2];
        int remote_port = Integer.parseInt(args[3]);

        Bd bd = new Bd(dbUrl, dbUser, dbPassword);
        Registry reg_local = LocateRegistry.getRegistry(local_address, local_port);
        ServiceBd sbd = (ServiceBd) UnicastRemoteObject.exportObject(bd, 0);
        reg_local.rebind("bd", sbd);

        Registry reg_remote = LocateRegistry.getRegistry(remote_address, remote_port);
        ServiceServeurHttp servhttp = (ServiceServeurHttp) reg_remote.lookup(ServiceServeurHttp.SERVICE_NAME);
        servhttp.enregisterServiceBd(sbd);
    }

}
