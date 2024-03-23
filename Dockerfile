# Uporabite uradno OpenJDK sliko z željeno različico JDK
FROM openjdk:17

# Nastavite delovni imenik v kontejnerju za aplikacijo
WORKDIR /app

# Kopirajte artefakte gradnje iz ciljnega direktorija vaše Quarkus aplikacije
COPY target/quarkus-app/lib/ /app/lib/
COPY target/quarkus-app/*.jar /app/
COPY target/quarkus-app/app/ /app/app/
COPY target/quarkus-app/quarkus/ /app/quarkus/

# Izpostavite port, na katerem bo aplikacija dostopna
EXPOSE 8081

# Zagon aplikacije s pomočjo Java naredi, z opcijami, ki so potrebne za Quarkus
CMD ["java", "-Dquarkus.http.port=8081", "-jar", "/app/quarkus-run.jar"]
