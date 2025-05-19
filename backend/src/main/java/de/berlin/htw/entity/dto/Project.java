package de.berlin.htw.entity.dto;

import jakarta.persistence.*;
import java.util.*;

@Entity
@Table(name = "PROJECT")
public class Project {

    @Id
    @Column(name = "ID", length = 36, nullable = false, updatable = false)
    private String id;

    @Column(name = "NAME", nullable = false)
    private String name;

    @Column(name = "DESCRIPTION")
    private String description;

    @ManyToMany
    @JoinTable(name = "USER_PROJECT",
            joinColumns = @JoinColumn(name = "PROJECT_ID"),
            inverseJoinColumns = @JoinColumn(name = "USER_ID"))
    private Set<UserEntity> users = new HashSet<>();

    @PrePersist
    public void initId() {
        if (this.id == null) {
            this.id = UUID.randomUUID().toString();
        }
    }

    // --- Getter / Setter ---

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public Set<UserEntity> getUsers() { return users; }
    public void setUsers(Set<UserEntity> users) { this.users = users; }
}