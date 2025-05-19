```shell script
docker-compose up -d
```
```shell script
$mvn  clean package
```
```shell script
java -jar backend/target/verteilte-anwendung-runner.jar
```


info zu
[liquibase-changelog.xml](backend/src/main/resources/META-INF/liquibase-changelog.xml)
Erklärung der Bestandteile:

<databaseChangeLog>
Start-Element für alle ChangeSets. Enthält XML-Namespaces und verweist auf das Liquibase-Schema.

<changeSet id="…" author="…">
Eine logische Gruppe von Änderungen, die einmalig ausgeführt wird.

id: Eindeutiger Bezeichner (muss pro Datei/Schritt einmalig sein).

author: Wer das ChangeSet erstellt hat.

<createTable tableName="…">
Legt eine neue Tabelle an, gefolgt von <column>-Definitionen.

<column name="…" type="…"> … </column>
Definiert je Spalte den Namen, den SQL-Datentyp und optionale Constraints.

<constraints …>
Innerhalb einer Spalte: nullable="false", primaryKey="true", etc.

<addUniqueConstraint>
Fügt einen Unique-Index auf eine oder mehrere Spalten hinzu.

<createIndex>
Legt einen (nicht-unique) Index an, um Abfragen zu beschleunigen.

<addPrimaryKey>
Fügt nachträglich einen zusammengesetzten Primärschlüssel hinzu (hier für Junction-Table).

<addForeignKeyConstraint>
Verknüpft zwei Tabellen über einen Foreign Key.

onDelete="CASCADE" sorgt dafür, dass beim Löschen des Parent-Datensatzes alle zugehörigen Junction-Zeilen mitgelöscht werden.


Aufgabe 3:
CREATE TABLE PROJECT (
ID CHAR(36) PRIMARY KEY,
NAME VARCHAR(255) NOT NULL,
DESCRIPTION TEXT
);

Aufgabe 4:
@Transactional sorgt dafür, dass die JPA-Session noch offen ist, während das Set initialisiert.
@JsonIgnore: Beim Erzeugen der JSON-Antwort wird UserEntity.projects nicht mehr beachtet.

