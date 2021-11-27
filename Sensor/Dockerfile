FROM openjdk:11.0.13-oracle

COPY /target/Sensor-1.0-SNAPSHOT-shaded.jar /opt/app/Sensor-1.0-SNAPSHOT-shaded.jar
WORKDIR /opt/app

ENTRYPOINT ["java", "-jar", "Sensor-1.0-SNAPSHOT-shaded.jar"]