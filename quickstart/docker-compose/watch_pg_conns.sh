#!/bin/bash


while :; 
do
	DOCKER_CON=`docker exec -u postgres tilkynnadb psql -U postgres -qtAX -d tilkynna -c "SELECT count(*) FROM pg_stat_activity where datname = 'tilkynna'"`
	DOCKER_CON2=`docker exec -u postgres tilkynnadb psql -U postgres -qtAX -d tilkynna -c "SELECT count(*) FROM pg_stat_activity"`
	
	CON=`netstat -nap 2>/dev/null |grep 5432 |grep java| grep -c EST` 
	sleep 4;
	echo `date +%Y-%m-%d:%H:%M:%S`  CON: $CON  DOCKER_CON:  $DOCKER_CON 	DOCKER_CON2:  $DOCKER_CON2;
	
	#what about the BIRT connection being made on the Docker image? 
done