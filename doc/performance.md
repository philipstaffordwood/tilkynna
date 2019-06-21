# Performance Testing
These are the results from our initial performance testing of Tilkynna. These tests demonstrated that the Tilkynna Reporting service is able to  generate reports at a rate of 96 reports per second.

## Environment
We used an [Amazon Web Service m5a.4xlarge](https://aws.amazon.com/ec2/instance-types/) for these tests.

```bash
$ cat /proc/meminfo | grep MemTotal
MemTotal:       64807752 kB
```
ie:  64Gb ram

The [DockerCompose](https://github.com/GrindrodBank/tilkynna/tree/master/quickstart/docker-compose)  file was used to deploy the service into the AWS server.

### Tilkynna Version
"The latest version of [Tilkynna](https://hub.docker.com/r/grindrodbank/tilkynna) was used for testing.
#### Postgres Version
The [postgres:10.7-alpine](https://hub.docker.com/_/postgres) docker image was used for both the Tilkynna database and the reporting database.

### Keycloak
The Keycloak used was [jboss/keycloak:4.7.0.Final](https://hub.docker.com/r/jboss/keycloak/)

### SFTP Server
[The atmoz/sftp:alpine Server](https://hub.docker.com/r/atmoz/sftp/) was deployed with Docker Compose, but was not used in the performance testing.

### pgbouncer
Some of our testing utilized postgres connection pooling in front of the database from which the reports were drawn. We used [the pgbouncer](https://hub.docker.com/r/pgbouncer/pgbouncer) docker image for this. Only test runs 1 and 2 below were done using pgbouncer so that we could compare the results with and without the connection pooling. The results did not show any significant performance improvements

### Docker 
```bash
$ docker --version docker --version
Docker version 18.09.6, build 481bc77
$
```

### Docker Compose
```bash
$ docker-compose --version
docker-compose version 1.24.0, build 0aa59064
$
```

# Test Case
The Tilkynna database was pre-populated with 10000 generate report requests. These were set to a PENDING state before starting the test run. 
The scope of this test case was to run the generate report portion. This test case made no HTTP requests through the API whilst the report generation was running.  
Reports were written to local disk. While the SFTP was deployed with the docker compose, it was not used for the testing.

## Results

### Running with 2 Containers: changed include pgbouncer with all the default settings
| Run | #reports | #Tilkynna Containers | Time Taken | Log File for Connections | JAVA_OPTS |
|-------|-------|-------|-------|-------|-------|
| 1 | 10000    | 2 | 00:01:23.640093 | RUN11_num_finished.txt, RUN11_pg_conms.txt |  -Xmx3g -Xms3g -Dcom.sun.management.jmxremote -Dcom.sun.management.jmxremote.port=6002 -Dcom.sun.management.jmxremote.authenticate=false -Dcom.sun.management.jmxremote.ssl=false -Djava.security.egd=file:/dev/./urandom -Dloader.path=drivers,libs-no-repo -Duser.timezone=UTC -jar /app.jar | 
| 2 | 10000    | 2 | 00:01:22.974135 | RUN12_num_finished.txt, RUN12_pg_conms.txt | -Xmx3g -Xms3g -Dcom.sun.management.jmxremote -Dcom.sun.management.jmxremote.port=6002 -Dcom.sun.management.jmxremote.authenticate=false -Dcom.sun.management.jmxremote.ssl=false -Djava.security.egd=file:/dev/./urandom -Dloader.path=drivers,libs-no-repo -Duser.timezone=UTC -jar /app.jar | 

**Running with 2 Containers: with BIRT engine call again... but now also monitoring the CPU, Mem, Disk of the machine itself**
| Run | #reports | #Tilkynna Containers | Time Taken | Log File for Connections | JAVA_OPTS |
|-------|-------|-------|-------|-------|-------|
| 3 | 10000    | 2 | 00:01:23.753612 | RUN19_num_finished.txt, RUN19_pg_conms.txt, RUN19_machine_stats.txt |  -Xmx3g -Xms3g -Dcom.sun.management.jmxremote -Dcom.sun.management.jmxremote.port=6002 -Dcom.sun.management.jmxremote.authenticate=false -Dcom.sun.management.jmxremote.ssl=false -Djava.security.egd=file:/dev/./urandom -Dloader.path=drivers,libs-no-repo -Duser.timezone=UTC -jar /app.jar | 
| 4 | 10000    | 2 | 00:01:44.040355 | RUN20_num_finished.txt, RUN20_pg_conms.txt RUN20_machine_stats.txt | -Xmx3g -Xms3g -Dcom.sun.management.jmxremote -Dcom.sun.management.jmxremote.port=6002 -Dcom.sun.management.jmxremote.authenticate=false -Dcom.sun.management.jmxremote.ssl=false -Djava.security.egd=file:/dev/./urandom -Dloader.path=drivers,libs-no-repo -Duser.timezone=UTC -jar /app.jar | 


