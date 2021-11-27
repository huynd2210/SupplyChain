FROM openjdk:11.0.13-oracle

COPY /target/Laden-1.0-SNAPSHOT-shaded.jar /opt/app/Laden-1.0-SNAPSHOT-shaded.jar
EXPOSE 6543/udp
WORKDIR /opt/app

ENTRYPOINT ["java", "-jar", "Laden-1.0-SNAPSHOT-shaded.jar"]