#!/bin/bash

reg=1099
port=8080

if [ -n "$1" ]; then
  reg="$1"
fi

if [ -n "$2" ]; then
  port="$2"
fi


cat run.pid | while read line 
do 
	kill "$line"
done
mvn clean compile assembly:single
if [ $? -ne 0 ]; then exit 1; fi
mkdir -p logs
sleep 0.5
rmiregistry $reg -J-classpath -Jtarget/Nancy-Map-1-jar-with-dependencies.jar > logs/registry.logs 2>&1 &
if [ $? -ne 0 ]; then exit 2; fi
echo $! > run.pid
sleep 0.5
java -cp target/Nancy-Map-1-jar-with-dependencies.jar sae.http.LancerServeur localhost $reg $port > logs/server.logs 2>&1  & 
if [ $? -ne 0 ]; then exit 3; fi
echo $! >> run.pid
sleep 0.5
java -cp target/Nancy-Map-1-jar-with-dependencies.jar sae.bd.LancerService localhost $reg localhost $reg > logs/servicebd.logs 2>&1 &
if [ $? -ne 0 ]; then exit 4; fi
echo $! >> run.pid
sleep 0.5
java -cp target/Nancy-Map-1-jar-with-dependencies.jar sae.proxyHttp.LancerService localhost $reg localhost $reg > logs/serviceproxy.logs 2>&1  & 
if [ $? -ne 0 ]; then exit 5; fi
echo $! >> run.pid
sleep 0.5
echo "App lancée 'localhost:$port'"

