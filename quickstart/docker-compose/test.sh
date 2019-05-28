#!/bin/bash

bold=$(tput bold)
normal=$(tput sgr0)
redbold=$(tput bold&&tput setaf 1)

echoBold(){
   echo "${bold}$1${normal}"
}
echoRedBold(){
   echo "${redbold}$1${normal}"
}



#NUM_REPORTS=$1
RANDOM_NUM=$(( ( RANDOM % 10000 )  + 1 ))

main () {
	TOKEN=`curl \
	-s \
	-X POST http://localhost:9191/auth/realms/Tilkynna/protocol/openid-connect/token \
	-H 'Content-Type: application/x-www-form-urlencoded' \
	-H 'Bearer-Token: 575c92bb-33fe-45b8-85ef-7cc5710e62eb' \
	-H 'cache-control: no-cache' \
	-d 'grant_type=password&username=test-user&password=test-user&client_id=tilkynna&client_secret=ee5c1c57-bf2f-43e6-9025-49344113c88d' \
	| jq '.access_token' -r` \
	&& echo "TOKEN is: $TOKEN"
	echo ""
	
	echo "NUM_REPORTS is: $NUM_REPORTS"
	echo "RANDOM_NUM is: $RANDOM_NUM"
	
	setup
	generate
	showResults
}

setup() {
	echo ""
	echoBold "****************** START SETUP "
		DESTINATION_ID=`curl -s -X GET \
		  http://localhost:9981/destinations \
		  -H "Authorization: Bearer $TOKEN" \
		  -H "Content-Type: application/json" \
		  -H "cache-control: no-cache" | jq '.[0].id' -r`
		
		POSTGRES_HOST=`docker inspect -f '{{ range .NetworkSettings.Networks}}{{.IPAddress}}{{end}}' tilkynnadb` \
		&& DATASOURCE=`curl -s -X POST \
		  http://localhost:9981/datasources \
		  -H "Authorization: Bearer $TOKEN" \
		  -H "Content-Type: application/json" \
		  -H "cache-control: no-cache" \
		  -d '{
		  "name": "'"tilkynna $RANDOM_NUM"'",
		  "description": "'"tilkynna $RANDOM_NUM"'",
		  "connection": {
		    "driver": "org.postgresql.Driver",
		    "url": "'"jdbc:postgresql://$POSTGRES_HOST:5432/tilkynna"'",
		    "username": "postgres",
		    "password": "postgres"
		  }
		}' | jq '.header.id' -r`
		
		
		TEMPLATE=`curl -s -X POST \
		  http://localhost:9981/templates \
		  -H "Authorization: Bearer $TOKEN" \
		  -H "cache-control: no-cache" \
		  -H "content-type: multipart/form-data" \
		  -F file=@../../postman/tilkynna_sample_report.rptdesign \
		  -F "templateName=Sample $RANDOM_NUM" \
		  -F "tags=Sample, NoParams" \
		  -F "datasourceIds=$DATASOURCE" | jq '.templateId' -r`		
		
		echo "POSTGRES_HOST is   : $POSTGRES_HOST"
		echo "DATASOURCE is      : $DATASOURCE"
		echo "TEMPLATE is        : $TEMPLATE"		
	    echo "DESTINATION is     : $DESTINATION_ID"

	echoBold "****************** ENDED SETUP"
	echo ""
}

callGenerateEndpoint() {	
	CORRELATION_ID=`curl -s -X POST \
	  http://localhost:9981/templates/$TEMPLATE/generate \
	  -H "Authorization: Bearer $TOKEN" \
	  -H "Content-Type: application/json" \
	  -H "cache-control: no-cache" \
	  -d '{
	  "callbackUrl": "https://myserver.com/notification/callback/here",
	  "doNotRetry": "false",
	  "exportFormat": "PDF",
	  "reportParameters": [
	    {
	      "name": "DestinationType",
	      "value": "SFTP"
	    }
	  ],
	  "destinationOptions": {
	    "destinationId": "'"$DESTINATION_ID"'"
	  }
	}' | jq '.correlationId' -r` 
	
	echo "Request report: $1  CORRELATION_ID:  $CORRELATION_ID"
}

generate() {
	echo ""
	echoBold "****************** START GENERATE"
	
	
	for (( c=1; c<=$NUM_REPORTS; c++ ))
	do  
	   callGenerateEndpoint $c & # Put a function in the background
	done
	
	## Put all callGenerateEndpoint in the background and wait until those are completed b4 displaying all done message
	wait
	echoBold "****************** ENDED GENERATE"
	echo ""
}

showResults() {
	echo ""
	echoBold "****************** START RESULTS"
	
	echoBold "****************** ENDED RESULTS"
	echo ""
}

#insert into _reports.generated_report  (requested_at, generated_at, requested_by, destination_id, template_id, retry_count, report_status, request_body, export_format_id) select now(), now(), requested_by, destination_id, template_id, retry_count, report_status, request_body, export_format_id from _reports.generated_report;


#docker exec -u postgres tilkynnadb pg_dump -c tilkynna > dump_`date +%d-%m-%Y"_"%H_%M_%S`.sql
#update _reports.generated_report set report_status = 'PENDING', requested_at = now(), generated_at=now();

# select report_status, count(*) from _reports.generated_report GROUP BY report_status;
#select  (sum(age(generated_at, requested_at))/count(*)) as avg, count(*)  from _reports.generated_report WHERE report_status = 'FINISHED';
#select min(requested_at), max(generated_at), age(max(generated_at), min(requested_at)), count(*)   from _reports.generated_report WHERE report_status = 'FINISHED';
#select requested_at, generated_at, age(generated_at, requested_at) as time_taken, report_status, retry_count  from _reports.generated_report WHERE report_status = 'FINISHED' ORDER BY generated_at;
# -Xmx3g -Xms3g 

############## Main

# We need to be called with an service name specification
if [ -n "$1" ]; then
  	NUM_REPORTS="$1"
  	main
else
  	echoRedBold "[ERROR] - Mandatory 1st argument 'NUM_REPORTS' not supplied."
  	echo ""
  	exit 1
fi