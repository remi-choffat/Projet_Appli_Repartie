#!/bin/bash

# mvn -q package 
#
# mvn compiler:compile 
# cd target/classes
# rmiregistry 2000 & 
# sleep 1
# java sae/http/LancerServeur localhost 2000
#


killall rmiregistry 
sleep 0.5
rmiregistry $1 -J-classpath -Jtarget/Nancy-Map-1-jar-with-dependencies.jar & 
mvn clean compile assembly:single
java -cp target/Nancy-Map-1-jar-with-dependencies.jar sae.http.LancerServeur localhost $1
