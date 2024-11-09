# Verteilte Anwendungen Übungsaufgabe 2

Mögliche Punktzahl: 20 Punkte

## Deadlines

- 1. Zug: 28.11.2024
- 2. Zug: 28.11.2024 

## Aufgabenstellung
In dieser Aufgabe erhalten Sie ein vorkonfiguriertes Projekt, das aus drei Komponenten besteht:

- **Frontend:** Eine Single-Page-Application programmiert mit Vue.js
- **Library:** Eine Bibliothek programmiert mit Jakarta EE Spezifikationen
- **Backend:** Eine Quarkus Applikation, die die Bibliothek implementiert und das Frontend ausliefert

Die bereits vorhandene Funktion umfasst das Anlegen, Ändern, Löschen und Abrufen von Benutzern.
Das Ziel der Übungsaufgabe 2 ist die Erweiterung dieser Applikation. Nach erfolgreicher Implementierung der nachfolgenden Aufgaben, sollte die Backend Applikation zum Einen das Anlegen, Ändern, Löschen und Abrufen von Projekten unterstützen und zum Anderen die Möglichkeit der Verknüpfung von Benutzern zu Projekten bereitstellen.

1.  **(4P)** Wie Sie sicherlich bereits festgestellt haben, lässt sich das Projekt nicht so einfach mittels ``$ mvn package`` bauen und mit ``$ java -jar ./backend/target/verteilte-anwendung-runner.jar`` starten. Dies liegt an der fehlenden Datenbank! Schreiben Sie daher ein Docker Compose file mit dem Sie das [offizielle Image der MySQL aus Docker Hub](https://hub.docker.com/_/mysql) starten. Beachten Sie bitte die bereits vorhandenen Konfigurationen in der 
[application.properties](backend/src/main/resources/application.properties). Wenn Sie die MySQL korrekt gestartet haben, dann sollten Sie das Projekt bauen und die Integrationstests ausführen können.
Nach dem Start der Applikation sollten Sie auf die Single-Page-Applikation über [http://localhost:8080/](http://localhost:8080/) zugreifen können.
2.  **(4P)** Um zusätzlich zu den Benutzern auch noch Projekte in der MySQL speichern zu können, muss das bestehende Schema um zwei Tabellen erweitert werden. Erstellen Sie dafür ein neues 
[Liquibase ChangeSet](https://docs.liquibase.com/concepts/changelogs/xml-format.html) in 
[liquibase-changelog.xml](backend/src/main/resources/META-INF/liquibase-changelog.xml), das zum Einen die Speicherung von Projekten ermöglicht und zum Anderen eine Many-to-Many Relation zwischen User und Projekt über eine Junction Table realisiert.
3.  **(4P)** Erweitern Sie den Enitity Layer um eine Projekt Entität. Diese Projekt Entität soll ein entsprechendes Mapping mittels [@ManyToMany](https://www.baeldung.com/jpa-many-to-many) implementieren. Entwickeln Sie außerdem einen Controller, der die Business Logic implementiert und die Transaktionen für den Data Layer kontrolliert. Stellen Sie durch entsprechende Integrationstests (mindestens 5) eine ausreichende Funktionalität sicher.
4.  **(8P)** Designen und implementieren Sie einen REST Endpunkt zum Anlegen, Ändern, Verknüpfen, Löschen und Abrufen von Projekten. Dieser REST Endpunkt soll über den Controller aus Aufgabe 3 die entsprechenden Daten persistieren. Stellen Sie durch entsprechende REST Assured Integrationstests (mindestens 5) eine ausreichende Funktionalität sicher.


# Quarkus Get Started

This project uses Quarkus, the Supersonic Subatomic Java Framework.

If you want to learn more about Quarkus, please visit its website: https://quarkus.io/ .

## Running the application in dev mode

You can run your application in dev mode that enables live coding using:
```shell script
$mvn clean install
$mvn compile quarkus:dev -pl backend
```

> **_NOTE:_**  Quarkus ships with a Dev UI, which is available in dev mode only at http://localhost:8080/q/dev/.

## Packaging and running the application

The application can be packaged using:
```shell script
$mvn package
```
It produces the `verteilte-anwendung-runner.jar` file in the `backend/target/` directory.

The application is now runnable using `$java -jar backend/target/verteilte-anwendung-runner.jar`.
