#!/bin/bash

cat run.pid | while read line 
do 
	kill "$line"
done
