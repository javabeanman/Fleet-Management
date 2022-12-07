FROM openjdk:17-oracle
ARG JAR_FILE=target/*.jar
COPY ${JAR_FILE} fleet-management.jar
ENTRYPOINT ["java", "-jar", "/fleet-management.jar"]