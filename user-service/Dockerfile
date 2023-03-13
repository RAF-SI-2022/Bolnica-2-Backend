FROM maven:3.5-jdk-11 AS build
COPY src /usr/src/app/src
COPY pom.xml /usr/src/app
RUN mvn -f /usr/src/app/pom.xml clean package -DskipTests

FROM openjdk:11
COPY --from=build /usr/src/app/target/user-service-0.0.1-SNAPSHOT.jar /usr/app/user-service-0.0.1-SNAPSHOT.jar
EXPOSE 8081
CMD ["java","-jar","/usr/app/user-service-0.0.1-SNAPSHOT.jar"]
