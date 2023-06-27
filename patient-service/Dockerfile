FROM harbor.k8s.elab.rs/bolnica-2/maven:3.5-jdk-11 AS build
COPY src /usr/src/app/src
COPY pom.xml /usr/src/app
RUN mvn -f /usr/src/app/pom.xml clean package -DskipTests

FROM harbor.k8s.elab.rs/bolnica-2/openjdk:11
COPY --from=build /usr/src/app/target/patient-service-0.0.1-SNAPSHOT.jar /usr/app/patient-service-0.0.1-SNAPSHOT.jar
EXPOSE 8082
RUN apt-get update &&\
    apt-get install -y cabextract &&\
    apt-get install -y xfonts-utils &&\
    wget http://ftp.de.debian.org/debian/pool/contrib/m/msttcorefonts/ttf-mscorefonts-installer_3.8_all.deb &&\
    dpkg -i ttf-mscorefonts-installer_3.8_all.deb
CMD ["java","-Dspring.profiles.active=prod","-jar","/usr/app/patient-service-0.0.1-SNAPSHOT.jar"]
