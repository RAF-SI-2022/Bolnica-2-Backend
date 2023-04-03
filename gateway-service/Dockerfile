FROM maven:3.5-jdk-11 AS build
COPY src /usr/src/app/src
COPY pom.xml /usr/src/app
RUN mvn -f /usr/src/app/pom.xml clean package

FROM openjdk:11
COPY --from=build /usr/src/app/target/gateway-service-0.0.1-SNAPSHOT.jar /usr/app/gateway-service-0.0.1-SNAPSHOT.jar
EXPOSE 8761
CMD ["java","-Dspring.profiles.active=prod","-jar","/usr/app/gateway-service-0.0.1-SNAPSHOT.jar"]