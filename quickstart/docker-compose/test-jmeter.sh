#!/bin/bash

JVM_ARGS="-Xms1g -Xmx1g -XX:MaxMetaspaceSize=256m"

~/apps/apache-jmeter-5.1.1/bin/jmeter -Jhost=$TILKYNNA_HOST \
		-Jport=9981 \
		-Jkeycloak_host=localhost \
		-Jkeycloak_port=9191 \
		-JPOSTGRES_HOST=172.17.0.2 -JPOSTGRES_PORT=5432 \
		-JSFTP_HOST=localhost -JSFTP_PORT=22 \
		-Jthreads=10000 -Jrampup=10 -Jloop=1