#!/bin/bash

# mvn -q package 
#
# mvn compiler:compile 
# cd target/classes
# rmiregistry 2000 & 
# sleep 1
# java sae/http/LancerServeur localhost 2000
#

mvn clean compile assembly:single
killall rmiregistry 
sleep 0.5
rmiregistry $1 -J-classpath -Jtarget/Nancy-Map-1-jar-with-dependencies.jar & 
sleep 0.5
java -cp target/Nancy-Map-1-jar-with-dependencies.jar sae.http.LancerServeur localhost $1 9090 & 
sleep 0.5
java -cp target/Nancy-Map-1-jar-with-dependencies.jar sae.bd.LancerService localhost $1 localhost $1 &
sleep 0.5
java -cp target/Nancy-Map-1-jar-with-dependencies.jar sae.proxyHttp.LancerService localhost $1 localhost $1 & 
