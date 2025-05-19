package de.berlin.htw.control;

import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import static org.junit.jupiter.api.Assertions.*;
import de.berlin.htw.entity.dto.Project;
import de.berlin.htw.entity.dto.UserEntity;

@QuarkusTest
public class ProjectControllerTest {

    @Inject ProjectController controller;
    @Inject EntityManager em;

    @BeforeEach
    @Transactional
    public void cleanup() {
        em.createQuery("DELETE FROM Project").executeUpdate();
        em.createQuery("DELETE FROM UserEntity").executeUpdate();
    }

    // Test1: findAll() liefert initial keine Projekte zurück
    @Test
    public void testFindEmptyInitially() {
        List<Project> list = controller.findAll();       // alle Projekte abfragen
        assertTrue(list.isEmpty());                      // Erwartung: leer
    }

    // Test2: create() erzeugt ein Projekt, findAll() liefert es zurück
    @Test
    public void testCreateAndFindAll() {
        Project p = new Project(); p.setName("NewProj"); // Projekt anlegen & Name setzen
        controller.create(p);                             // speichern
        assertEquals(1, controller.findAll().size());    // genau 1 Projekt vorhanden
    }

    // Test3: update() ändert den Namen eines bestehenden Projekts
    @Test
    public void testUpdate() {
        Project p = new Project(); p.setName("OldName");             // altes Projekt
        p = controller.create(p);                                    // speichern
        p.setName("NewName");                                        // neuen Namen setzen
        Project updated = controller.update(p.getId(), p);
        assertEquals("NewName", updated.getName());                  // Name wurde geändert
    }

    // Test4: delete() löscht ein Projekt, findAll() ist danach leer
    @Test
    public void testDelete() {
        Project p = new Project(); p.setName("ToDelete");            // Projekt anlegen
        p = controller.create(p);                                    // speichern
        controller.delete(p.getId());                                // löschen per String-ID
        assertTrue(controller.findAll().isEmpty());                  // nun leer
    }

    // Test5: update() wirft IllegalArgumentException bei nicht existierendem Projekt
    @Test
    public void testUpdateNonexistentThrows() {
        String rnd = UUID.randomUUID().toString();       // zufällige ID als String
        Project bogus = new Project();
        bogus.setName("None");

        assertThrows(
                IllegalArgumentException.class,
                () -> controller.update(rnd, bogus)
        );
    }

    // Test6: assignUserToProject() verknüpft Nutzer und Projekt (ManyToMany)
    @Test @Transactional
    public void testAssignUserToProject() {
        Project p = new Project(); p.setName("MapTest");            // Projekt erzeugen
        p = controller.create(p);                                   // speichern
        UserEntity u = new UserEntity();                            // User erzeugen
        u.setId(UUID.randomUUID().toString()); u.setName("Max"); u.setEmail("max@ex.com");
        em.persist(u); em.flush();                                  // in DB persistieren
        controller.assignUserToProject(p.getId(), u.getId());       // String-ID verwenden
        Set<UserEntity> users = controller.getUsers(p.getId());     // Abfrage per String-ID
        assertEquals(1, users.size());                              // genau 1 Zuordnung
    }

    // Test7: removeUserFromProject() entfernt die Verknüpfung wieder
    @Test @Transactional
    public void testRemoveUserFromProject() {
        Project p = new Project(); p.setName("RemTest");            // Projekt erzeugen
        p = controller.create(p);                                   // speichern
        UserEntity u = new UserEntity();                            // User erzeugen
        u.setId(UUID.randomUUID().toString()); u.setName("Erika"); u.setEmail("eri@ex.com");
        em.persist(u); em.flush();                                  // in DB persistieren
        controller.assignUserToProject(p.getId(), u.getId());       // zuordnen
        controller.removeUserFromProject(p.getId(), u.getId());     // wieder entfernen
        Set<UserEntity> users = controller.getUsers(p.getId());     // Abfrage per String-ID
        assertTrue(users.isEmpty());                                // Verknüpfung entfernt
    }
}
