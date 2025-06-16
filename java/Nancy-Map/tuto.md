git clone https://github.com/remi-choffat/Projet_Appli_Repartie.git
cd Projet_Appli_Repartie/java/Nancy-Map

# compiler avec les depandance incluse
mvn clean compile assembly:single

# lancer le rmi
rmiregistry -J-classpath -Jtarget/Nancy-Map-1-jar-with-dependencies.jar & 


# lancer le serveur http
java -cp target/Nancy-Map-1-jar-with-dependencies.jar sae.http.LancerServeur localhost 1099

# lancer le service base de donnée
java -cp target/Nancy-Map-1-jar-with-dependencies.jar sae.bd.LancerService localhost 1099 localhost 1099

# lancer le service proxy
java -cp target/Nancy-Map-1-jar-with-dependencies.jar sae.proxyHttp.LancerService localhost 1099 localhost 1099

