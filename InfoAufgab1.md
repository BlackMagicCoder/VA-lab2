Fehler bei Test Backend Quarkus, 
Quarkus konnte nicht gebaut werden, 
Datenbank fehlt - 
Docker Compose mit Daten aus application properties - 

Starten: 
docker compose up -d, 
docker ps
docker logs my-mysql, 
mvn clean install, 
java -jar ./backend/target/*-runner.jar

Die Daten im Container (unter /var/lib/mysql) werden im Volume db_data gespeichert. 
Das sorgt dafür, dass Daten beim Stoppen oder Neustarten des Containers erhalten 
bleiben.

