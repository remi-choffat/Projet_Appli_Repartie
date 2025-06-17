#!/bin/bash

if [ -z "$1" ] ; then 
	echo "usage : ./stopDistant.sh <ssh user>"
	exit 1
fi

user=$1 

kill_process()
{
	read host pid < "$1"
	ssh $user@$host "kill $pid"

	if [ $? -eq 0 ] ; then 
		echo process $pid killed on $host
		rm "$1"
	fi

}

if [ -f "registry.pid" ] ; then
	kill_process "registry.pid"
fi
if [ -f "registry_serviceproxy.pid" ] ; then
	kill_process "registry_serviceproxy.pid"
fi
if [ -f "registry_servicebd.pid" ] ; then
	kill_process "registry_servicebd.pid"
fi
if [ -f "serveur.pid" ] ; then
	kill_process "serveur.pid"
fi
if [ -f "serviceproxy.pid" ] ; then
	kill_process "serviceproxy.pid"
fi
if [ -f "servicebd.pid" ] ; then
	kill_process "servicebd.pid"
fi

