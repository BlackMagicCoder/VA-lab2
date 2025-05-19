package de.berlin.htw.boundary;

import de.berlin.htw.control.ProjectController;
import de.berlin.htw.control.UserController;
import de.berlin.htw.entity.dto.Project;
import de.berlin.htw.entity.dto.UserEntity;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

// Definiert die Basis-URL für alle Projekt-Operationen
@Path("/api/v2/projects")
// Alle Antworten werden als JSON gesendet
@Produces(MediaType.APPLICATION_JSON)
// Alle Anfragen werden als JSON erwartet
@Consumes(MediaType.APPLICATION_JSON)
public class ProjectResource {

    // Injektion des Controllers für Projekte (Business-Logik)
    @Inject
    ProjectController projectController;

    // Injektion des Controllers für User-Management
    @Inject
    UserController userController;

    /**
     * GET /api/v2/projects
     * Liefert eine Liste aller Projekte zurück.
     */
    @GET
    public List<Project> findAll() {
        // Aufruf an die Service-Schicht
        return projectController.findAll();
    }

    /**
     * GET /api/v2/projects/{id}
     * Liefert ein einzelnes Projekt basierend auf seiner ID.
     */
    @GET
    @Path("{id}")
    public Project findById(@PathParam("id") String id) {
        return projectController.findById(id);
    }

    /**
     * POST /api/v2/projects
     * Erstellt ein neues Projekt.
     * @param project DTO mit Name und Beschreibung
     * @return HTTP 201 und das erstellte Projekt im Body
     */
    @POST
    public Response create(Project project) {
        Project created = projectController.create(project);
        return Response.status(Response.Status.CREATED)
                .entity(created)
                .build();
    }

    /**
     * PUT /api/v2/projects/{id}
     * Aktualisiert ein existierendes Projekt.
     * @param id   ID des Projekts, das aktualisiert wird
     * @param data Neue Projektdaten
     * @return Projekt mit aktualisierten Daten
     */
    @PUT
    @Path("{id}")
    public Project update(@PathParam("id") String id, Project data) {
        return projectController.update(id, data);
    }

    /**
     * DELETE /api/v2/projects/{id}
     * Löscht ein Projekt anhand seiner ID.
     */
    @DELETE
    @Path("{id}")
    public void delete(@PathParam("id") String id) {
        projectController.delete(id);
    }

    /**
     * POST /api/v2/projects/{projectId}/users/{userId}
     * Ordnet einen User einem Projekt zu.
     * @Consumes(MediaType.WILDCARD) erlaubt leere Bodies
     */
    @POST
    @Path("{projectId}/users/{userId}")
    @Consumes(MediaType.WILDCARD)
    public void assignUser(@PathParam("projectId") String projectId,
                           @PathParam("userId")    String userId) {
        projectController.assignUserToProject(projectId, userId);
    }

    /**
     * DELETE /api/v2/projects/{projectId}/users/{userId}
     * Entfernt einen User aus einem Projekt.
     */
    @DELETE
    @Path("{projectId}/users/{userId}")
    @Consumes(MediaType.WILDCARD)
    public void removeUser(@PathParam("projectId") String projectId,
                           @PathParam("userId")    String userId) {
        projectController.removeUserFromProject(projectId, userId);
    }

    /**
     * GET /api/v2/projects/{projectId}/users
     * Listet alle User eines Projekts auf.
     * @Transactional sorgt dafür, dass Hibernate-Session offen bleibt,
     * damit Lazy-Collections initialisiert werden können.
     */
    @GET
    @Path("{projectId}/users")
    @Transactional
    public List<UserEntity> getUsers(@PathParam("projectId") String projectId) {
        // Holt das Set aller zugeordneten User
        Set<UserEntity> users = projectController.getUsers(projectId);
        // Erzwingt Initialisierung der lazy-Collection innerhalb der Transaktion
        users.size();
        // Wandelt das Set in eine List um, Jackson kann diese problemlos serialisieren
        return new ArrayList<>(users);
    }
}