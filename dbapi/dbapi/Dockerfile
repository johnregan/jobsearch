FROM openjdk:8-jdk-slim-stretch

LABEL MAINTAINER="John Regan <johnreganit@gmail.com>"

COPY target/scala-2.12/dbapi-assembly-0.0.1-SNAPSHOT.jar /bin/dbapi/dbapi.jar
WORKDIR /bin/dbapi

EXPOSE 8080

RUN chmod a+x dbapi.jar

ENTRYPOINT ["java", "-jar", "/bin/dbapi/dbapi.jar"]