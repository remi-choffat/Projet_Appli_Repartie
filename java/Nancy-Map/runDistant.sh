#!/bin/bash


if [ -z "$1" ] || [ -z "$2" ]; then 
	echo "usage ./runDistant.sh <ssh user> <hostserveur> [portserver] [hostserviceproxy] [portserviceproxy] [hostservicedb] [portservicedb] [portapi]"
	exit 1;
fi

count=1
check_succes() 
{
	count=$((count+1))
	echo check $count : $2
	if [ $1 -ne 0 ]; then 
		echo fail;
		exit $count; 
		
	fi
}

user=$1
hostserveur=$2
portserver=1099
hostserviceproxy=$hostserveur
portserviceproxy=$portserver
hostservicedb=$hostserveur
portservicedb=$portserver
portapi=9090
if [ -n "$3" ]; then portserver=$3 ; fi
if [ -n "$4" ]; then hostserviceproxy=$4 ; fi
if [ -n "$5" ]; then portserviceproxy=$5 ; fi
if [ -n "$6" ]; then hostservicedb=$6 ; fi
if [ -n "$7" ]; then portservicedb=$7 ; fi
if [ -n "$8" ]; then portapi=$8 ; fi

# mvn clean compile assembly:single 
# check_succes $! "mvn"

dir=$PWD


ssh $user@$hostserveur "cd $dir && rmiregistry $portserver -J-classpath -Jtarget/Nancy-Map-1-jar-with-dependencies.jar > registry1.logs 2>&1"
check_succes $! "rmiregistry server"


if [ "$hostserveur" != "$hostserviceproxy" ] && [ "$portserver" != "$portserviceproxy" ] ; then 
	ssh $user@$hostserviceproxy "cd $dir && rmiregistry $portserviceproxy -J-classpath -Jtarget/Nancy-Map-1-jar-with-dependencies.jar > registry2.logs 2>&1"
check_succes $! "rmiregistry service proxyHttp"
fi

if [ "$hostserveur" != "$hostservicedb" ] && [ "$portserver" != "$portservicedb" ] ; then 
	ssh $user@$hostservicedb "cd $dir && rmiregistry $portservicedb -J-classpath -Jtarget/Nancy-Map-1-jar-with-dependencies.jar > registry3.logs 2>&1"
check_succes $! "rmiregistry server servicebd"
fi



ssh $user@$hostserveur "cd $dir && java -cp target/Nancy-Map-1-jar-with-dependencies.jar sae.http.LancerServeur localhost $portserver $portapi > server.logs 2>&1 &"
check_succes $! "server"
ssh $user@$hostserviceproxy "cd $dir && java -cp target/Nancy-Map-1-jar-with-dependencies.jar sae.bd.LancerService localhost $portserviceproxy $hostserveur $portserver $ > servicebd.logs 2>&1 &"
check_succes $! "serviceproxy"
ssh $user@$hostservicedb "cd $dir && java -cp target/Nancy-Map-1-jar-with-dependencies.jar sae.proxyHttp.LancerService localhost $portservicedb $hostserveur $portserver > serviceproxy.logs 2>&1  &"
check_succes $! "servicebd"
