[![CircleCI](https://circleci.com/gh/GrindrodBank/tilkynna.svg?style=svg)](https://circleci.com/gh/GrindrodBank/tilkynna)
[![FOSSA Status](https://app.fossa.com/api/projects/git%2Bgithub.com%2FGrindrodBank%2Ftilkynna.svg?type=shield)](https://app.fossa.com/projects/git%2Bgithub.com%2FGrindrodBank%2Ftilkynna?ref=badge_shield)
[![Quality Gate Status](https://sonarcloud.io/api/project_badges/measure?project=GrindrodBank_tilkynna&metric=alert_status)](https://sonarcloud.io/dashboard?id=GrindrodBank_tilkynna)

# Tilkynna
Tilkynna is an enterprise grade, utility-style report server wrapper written in Java.

## Quickstart

Taking it out for a spin in 5-10 minutes:

### Pre-requisites

> This example uses:
> * [curl](https://github.com/curl/curl) (a handy command line client to do HTTP requests) and 
> * [jq](https://stedolan.github.io/jq/) (a nice command line JSON processor)
> * sftp: sftp cli must be installed on the host to obtain the generated report from the SFTP destination server.
> * [docker](https://www.docker.com): docker must be installed on the host.
> * [docker-compose](https://docs.docker.com/compose/): docker-compose must be installed on the host.


* Clone Github repo:
```bash
git clone git@github.com:GrindrodBank/tilkynna.git
```

* Run using docker-compose:
```bash
cd tilkynna
cd quickstart/docker-compose
docker-compose -p tilkynna up
```

4 services are installed:
* `Web_1` - The actual Tilkynna reporting service installed on port 9981
* `PostgreSQL` Database installed on port 5432
* `Keycloak` Authentication service installed on port 9191 and can be accessed by going to http://localhost:9191
* `SFTP` - An SFTP server running on port 2222

### Credentials for PostgreSQL database
* Password: postgres
* Database: tilkynna

### Credentials for Keycloak
* Username: admin
* Password: admin

### Credentials for SFTP
* Username: foo
* Password: pass


* Get an authentication token in order to get API access:

```bash
TOKEN=`curl \
-s \
-X POST http://localhost:9191/auth/realms/Tilkynna/protocol/openid-connect/token \
-H 'Content-Type: application/x-www-form-urlencoded' \
-H 'Bearer-Token: 575c92bb-33fe-45b8-85ef-7cc5710e62eb' \
-H 'cache-control: no-cache' \
-d 'grant_type=password&username=test-user&password=test-user&client_id=tilkynna&client_secret=ee5c1c57-bf2f-43e6-9025-49344113c88d' \
| jq '.access_token' -r` \
&& echo "TOKEN is: $TOKEN"
```

* Create a data source 
We use the Tilkynna database for this sample report.
TilkynnaDB_Datasource

```bash
POSTGRES_HOST=`docker inspect -f '{{ range .NetworkSettings.Networks}}{{.IPAddress}}{{end}}' tilkynna_postgresql_1` \
&& DATASOURCE=`curl -s -X POST \
  http://localhost:9981/datasources \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -H "cache-control: no-cache" \
  -d '{
  "name": "stores",
  "description": "stores",
  "connection": {
    "driver": "org.postgresql.Driver",
    "url": "'"jdbc:postgresql://$POSTGRES_HOST:5432/tilkynna"'",
    "username": "postgres",
    "password": "postgres"
  }
}' | jq '.header.id' -r`  \
&& echo "DATASOURCE is :$DATASOURCE"
```

* Validate the data source
```bash
curl -s -v -X PUT \
  http://localhost:9981/datasources/$DATASOURCE/validate \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -H "cache-control: no-cache" \
  -d '{
    "driver": "org.postgresql.Driver",
    "url": "'"jdbc:postgresql://$POSTGRES_HOST:5432/tilkynna"'",
    "username": "postgres",
    "password": "postgres"
}'
```

* Upload a report template:
This uses the sample report available in the postman testing collection:

```bash
postman/tilkynna_sample_report.rptdesign
```

```bash
TEMPLATE=`curl -s -X POST \
  http://localhost:9981/templates \
  -H "Authorization: Bearer $TOKEN" \
  -H "cache-control: no-cache" \
  -H "content-type: multipart/form-data" \
  -F file=@../../postman/tilkynna_sample_report.rptdesign \
  -F "templateName=Sample" \
  -F "tags=Sample, NoParams" \
  -F "datasourceIds=$DATASOURCE" | jq '.templateId' -r` \
&& echo "TEMPLATE is: $TEMPLATE"
```

* Create an SFTP report destination

Note: The SFTP server is running as a container. Run the following command to determine it's host.

```bash
SFTP_HOST=`docker inspect -f '{{ range .NetworkSettings.Networks}}{{.IPAddress}}{{end}}' tilkynna_sftp_1` \
&& DESTINATION_ID=`curl -s -X POST \
  http://localhost:9981/destinations \
  -H 'Content-Type: application/json' \
  -H "Authorization: Bearer $TOKEN" \
  -H 'cache-control: no-cache' \
-d '{
  "destinationType": "SFTP",
  "host": "'"$SFTP_HOST"'",
  "port": "22",
  "path" : "/upload",
  "user": "foo",
  "password": "pass",
  "header": {
    "name": "SFTP server",
    "description": "our shared sftp server",
    "securityProtocol": "ssl",
    "timeout": "100000",
    "downloadable": "false"
  }
}' | jq '.header.id' -r` \
&& echo "DESTINATION is: $DESTINATION_ID"
```

* Generate a report

```bash
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
    "destinationId": "'"$DESTINATION_ID"'",
    "destinationParameters": [
      {
        "name": "path",
        "value": "sub_folder"
      }
    ]
  }
}' | jq '.correlationId' -r` \
&& echo "CORRELATION_ID: $CORRELATION_ID"
```

* Check Status of Report Generation

```bash
curl -X GET \
  http://localhost:9981/reports/$CORRELATION_ID/status \
  -H "Authorization: Bearer $TOKEN" \
  -H 'cache-control: no-cache'
```

* The report can be retrieved using SFTP

If you have `sshpass` installed you can use
```bash
sshpass -p pass sftp -P 2222 -oStrictHostKeyChecking=no foo@localhost:/upload/sub_folder/$CORRELATION_ID.PDF
```

otherwise
```
sftp -P 2222 foo@localhost:/upload/sub_folder/
```
Login credential will be `pass`.
then in the SFTP prompt tpe the following to list files:
```
ls
```
Then download the desired file using
```
get <file-name>
```
Exit the SFTP session using:
```
bye
```


* To undeploy everything:

```bash
docker-compose -p tilkynna down
```

# Postman Collection

A `Tilkynna Quickstart.postman_collection.json` Postman collection has been included within the `postman` folder. This collection is intended for exploring the Tilkynna API. There is also a `Tilkynna.postman_environment.json` Postman environment that can be used to interact with the
Tilkynna instance deployed using the `docker-compose` method in the Quickstart above.

# Project Documentation

All project documentation is currently available within the `/doc` folder.

## License
[![FOSSA Status](https://app.fossa.io/api/projects/git%2Bgithub.com%2FGrindrodBank%2Ftilkynna.svg?type=large)](https://app.fossa.io/projects/git%2Bgithub.com%2FGrindrodBank%2Ftilkynna?ref=badge_large)