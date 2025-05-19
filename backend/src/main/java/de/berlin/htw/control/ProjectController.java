package de.berlin.htw.control;

import de.berlin.htw.entity.dto.Project;
import de.berlin.htw.entity.dto.UserEntity;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.NotFoundException;

import java.util.List;
import java.util.Set;
import java.util.UUID;

@ApplicationScoped
public class ProjectController {

    @Inject
    EntityManager em;

    /**
     * Legt ein neues Projekt an. Erzeugt bei Bedarf eine String-UUID.
     */
    @Transactional
    public Project create(Project project) {
        if (project.getId() == null || project.getId().isEmpty()) {
            project.setId(UUID.randomUUID().toString());
        }
        em.persist(project);
        return project;
    }

    /**
     * Liefert alle Projekte.
     */
    public List<Project> findAll() {
        return em.createQuery("SELECT p FROM Project p", Project.class)
                .getResultList();
    }

    /**
     * Updated ein bestehendes Projekt nach seiner String-ID.
     */
    @Transactional
    public Project update(String id, Project data) {
        Project existing = findById(id);
        existing.setName(data.getName());
        existing.setDescription(data.getDescription());
        return existing;
    }

    /**
     * Löscht ein Projekt nach seiner String-ID.
     */
    @Transactional
    public void delete(String id) {
        Project existing = em.find(Project.class, id);
        if (existing != null) {
            em.remove(existing);
        }
    }

    /**
     * Ordnet einen vorhandenen User (String-ID) einem Projekt (String-ID) zu.
     */
    @Transactional
    public void assignUserToProject(String projectId, String userId) {
        Project project = em.find(Project.class, projectId);
        if (project == null) {
            throw new IllegalArgumentException("Projekt nicht gefunden: " + projectId);
        }
        UserEntity user = em.find(UserEntity.class, userId);
        if (user == null) {
            throw new IllegalArgumentException("Benutzer nicht gefunden: " + userId);
        }
        project.getUsers().add(user);
        user.getProjects().add(project);
    }

    /**
     * Entfernt die Zuordnung eines Users (String-ID) von einem Projekt (String-ID).
     */
    @Transactional
    public void removeUserFromProject(String projectId, String userId) {
        Project project = em.find(Project.class, projectId);
        if (project == null) {
            throw new IllegalArgumentException("Projekt nicht gefunden: " + projectId);
        }
        UserEntity user = em.find(UserEntity.class, userId);
        if (user == null) {
            throw new IllegalArgumentException("Benutzer nicht gefunden: " + userId);
        }
        project.getUsers().remove(user);
        user.getProjects().remove(project);
    }

    /**
     * Liefert alle User, die einem Projekt (String-ID) zugeordnet sind.
     */
    @Transactional
    public Set<UserEntity> getUsers(String projectId) {
        Project project = em.find(Project.class, projectId);
        if (project == null) {
            throw new IllegalArgumentException("Projekt nicht gefunden: " + projectId);
        }
        return project.getUsers();
    }
    /**
     * Findet ein Projekt oder wirft NotFoundException.
     */
    public Project findById(String id) {
        Project existing = em.find(Project.class, id);
        if (existing == null) {
            throw new NotFoundException("Projekt nicht gefunden: " + id);
        }
        return existing;
    }
}