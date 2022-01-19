FROM openjdk:11.0.13-oracle

COPY /target/SupplyChainMonitor-1.0-SNAPSHOT-shaded.jar /opt/app/SupplyChainMonitor-1.0-SNAPSHOT-shaded.jar
WORKDIR /opt/app

ENTRYPOINT ["java", "-jar", "SupplyChainMonitor-1.0-SNAPSHOT-shaded.jar"]