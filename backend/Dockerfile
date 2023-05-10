# define base docker image
FROM openjdk:18
ARG JAR_FILE=target/*.jar
COPY ${JAR_FILE} spring-web-application-0.0.1-SNAPSHOT.jar
ENTRYPOINT ["java", "-jar", "/spring-web-application-0.0.1-SNAPSHOT.jar"]



