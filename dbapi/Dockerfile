FROM openjdk:8-jdk-slim-stretch

LABEL MAINTAINER="John Regan <johnreganit@gmail.com>"

COPY target/scala-2.12/dbapi-assembly-0.0.1-SNAPSHOT.jar /bin/dbapi/dbapi.jar
WORKDIR /bin/dbapi

EXPOSE 8080

RUN chmod a+x dbapi.jar

RUN export CLOUDSQL_USERNAME_URL=$(cat /etc/secretvolume/username.txt)
RUN export CLOUDSQL_PASSWORD_URL=$(cat /etc/secretvolume/password.txt)
RUN export CLOUDSQL_JDBC_URL=$(cat /etc/secretvolume/jdbc.txt)


ENTRYPOINT ["java", "-jar", "/bin/dbapi/dbapi.jar"]