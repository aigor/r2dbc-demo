FROM java:8-jre-alpine
VOLUME /tmp
ADD r2dbc-webapp-0.0.1-SNAPSHOT.jar app.jar
ENTRYPOINT ["java","-Djava.security.egd=file:/dev/./urandom","-jar","/app.jar"]