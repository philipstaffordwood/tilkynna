#!/bin/bash

#docker exec -u postgres tilkynnadb psql -U postgres -d tilkynna -c "select count(*) from _reports.generated_report WHERE report_status = 'STARTED'"

#https://stackoverflow.com/questions/28451598/how-to-return-a-value-from-psql-to-bash-and-use-it


FINISHED=0
EXPECTED=10000
while [ $FINISHED != $EXPECTED ]
do
	FINISHED_B4=$FINISHED
	FINISHED=`docker exec -u postgres tilkynnadb psql -U postgres -qtAX -d tilkynna -c "select count(*) from _reports.generated_report WHERE report_status = 'FINISHED'"`
	STARTED=`docker exec -u postgres tilkynnadb psql -U postgres -qtAX -d tilkynna -c "select count(*) from _reports.generated_report WHERE report_status = 'STARTED'"`
	
	X_MORE_FINISHED=$(($FINISHED - $FINISHED_B4))
	sleep 4;
	echo "`date +%Y-%m-%d:%H:%M:%S` STARTED: $STARTED  FINISHED:  $FINISHED  X_MORE_FINISHED: $X_MORE_FINISHED"
	
	# We need to be called with an service name specification
	#if [ $STARTED -gt 8 ]; then
	#  	echo "STARTED -gt 8 : $STARTED"
	#fi
done