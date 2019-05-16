#
# *************************************************
# Copyright (c) 2019, Grindrod Bank Limited
# License MIT: https://opensource.org/licenses/MIT
# **************************************************
#

# Step : Build image
FROM maven:3.5.3-jdk-8-alpine as BUILD
WORKDIR /build
COPY /libs-no-repo /build/libs-no-repo
COPY pom.xml .
# get all the downloads out of the way
RUN mvn clean
RUN mvn compiler:help jar:help resources:help surefire:help clean:help install:help deploy:help site:help dependency:help javadoc:help spring-boot:help
RUN mvn dependency:go-offline -Dorg.slf4j.simpleLogger.log.org.apache.maven.cli.transfer.Slf4jMavenTransferListener=warn

COPY src/ /build/src/
COPY drivers/ /build/drivers/
COPY doc/tilkynna_api.yml /build/doc/tilkynna_api.yml

RUN mvn package -Dmaven.test.skip=true
# mvn clean run own as step to get 'maven-install-plugin' to add org.eclipse.birt.report.engine.emitter.csv_1.0.0.201110121016.jar to local repo
# mvn <maven-plugin-name>:help caches maven specific dependencies to image
# mvn dependency:go-offline caches build depencencies to image


ARG buildtime_drivers_path=drivers
ENV JDBC_DRIVERS_PATH=$buildtime_drivers_path

# Step : Package image
FROM openjdk:8-jre-alpine as APP
EXPOSE 9981
COPY --from=BUILD /build/target/tilkynna.jar app.jar
COPY --from=BUILD /build/drivers/ drivers
COPY --from=BUILD  /build/libs-no-repo/ libs-no-repo

# To reduce Tomcat startup time we added a system property pointing to "/dev/urandom" as a source of entropy.
ENTRYPOINT exec java $JAVA_OPTS -Djava.security.egd=file:/dev/./urandom -Dloader.path=drivers,libs-no-repo -Duser.timezone=UTC -jar /app.jar
