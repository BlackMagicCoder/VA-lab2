package de.berlin.htw.control;

import java.util.List;
import java.util.UUID;

import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityExistsException;
import jakarta.transaction.Transactional;
import jakarta.transaction.UserTransaction;
import jakarta.ws.rs.InternalServerErrorException;
import jakarta.ws.rs.NotFoundException;

import org.jboss.logging.Logger;

import de.berlin.htw.boundary.AlreadyExistsException;
import de.berlin.htw.entity.dao.UserRepository;
import de.berlin.htw.entity.dto.UserEntity;
import de.berlin.htw.lib.model.UserModel;

/**
 * Controller-Klasse für User-bezogene Geschäftslogik.
 * Diese Klasse wird pro HTTP-Request instanziert (@RequestScoped).
 */
@RequestScoped
public class UserController {

    // Logger für Monitoring und Debug-Ausgaben
    @Inject
    Logger logger;

    // Repository für Datenbank-Operationen auf User-Entitäten
    @Inject
    UserRepository repository;

    // Manuelle Transaktionssteuerung bei komplexeren Methoden
    @Inject
    UserTransaction transaction;

    /**
     * Gibt alle vorhandenen User-Modelle zurück.
     * @return Liste aller User im System
     */
    public List<? extends UserModel> getUsers() {
        return repository.getAll();
    }

    /**
     * Erstellt einen neuen User basierend auf dem übergebenen Modell.
     * Verwaltet die Transaktion manuell, um Rollback bei Fehlern sicherzustellen.
     *
     * @param user Eingabedaten für den neuen User (Name, Email)
     * @return Die generierte ID des neu angelegten Users
     * @throws AlreadyExistsException wenn ein User mit gleicher E-Mail existiert
     * @throws InternalServerErrorException bei anderen Fehlern
     */
    public String createUser(final UserModel user) {
        // Log-Ausgabe mit Platzhaltern
        logger.infov("Creating a new user (name={0}, email={1})", user.getName(), user.getEmail());

        // DTO → Entity
        final UserEntity entity = new UserEntity();
        entity.setId(UUID.randomUUID());              // Zufällige UUID als Primärschlüssel
        entity.setName(user.getName());               // Feldzuweisung Name
        entity.setEmail(user.getEmail());             // Feldzuweisung Email

        try {
            transaction.begin();                       // Transaktion starten
            String userId = repository.add(entity);    // Entity persistieren
            transaction.commit();                      // Transaktion erfolgreich beenden
            return userId;
        } catch (EntityExistsException e) {
            // E-Mail bereits vergeben → spezifische Exception für REST-Response
            throw new AlreadyExistsException("Unable to create user", e);
        } catch (Exception e) {
            // Allgemeiner Fehler → Rollback und InternalServerError
            try {
                transaction.rollback();
            } catch (Exception ex) {
                throw new InternalServerErrorException("Unable to rollback on create user", ex);
            }
            throw new InternalServerErrorException("Unable to create user", e);
        }
    }

    /**
     * Sucht einen User anhand der ID.
     * @param userId Die ID des gesuchten Users
     * @return Gefundenes UserModel
     * @throws NotFoundException wenn kein User vorhanden ist
     */
    public UserModel getUser(final String userId) {
        logger.infov("Retrieving an existing user (id = {0})", userId);

        final UserModel user = repository.get(userId);
        if (user == null) {
            // Kein Treffer → HTTP 404
            throw new NotFoundException("User not exist");
        }
        return user;
    }

    /**
     * Aktualisiert einen bestehenden User.
     * Diese Methode läuft in einer container-managed Transaktion (@Transactional).
     * @param user Model mit neuen Daten und bestehender ID
     * @return Aktualisiertes UserModel
     */
    @Transactional
    public UserModel updateUser(final UserModel user) {
        logger.infov("Updating an existing user ({0})", user);

        // Bestehende Entity laden
        final UserEntity entity = repository.get(user.getId());
        // Nur neue Werte setzen, falls nicht null
        if (user.getName() != null) {
            entity.setName(user.getName());
        }
        if (user.getEmail() != null) {
            entity.setEmail(user.getEmail());
        }

        // Änderungen speichern und zurückliefern
        return repository.set(entity);
    }

    /**
     * Löscht einen User anhand der ID.
     * Transaktion wird container-managed durch @Transactional übernommen.
     * @param userId ID des zu löschenden Users
     * @return true, falls Löschung erfolgreich war, sonst false
     */
    @Transactional
    public boolean deleteUser(final String userId) {
        logger.infov("Deleting an existing user (id = {0})", userId);
        return repository.remove(userId);
    }
}