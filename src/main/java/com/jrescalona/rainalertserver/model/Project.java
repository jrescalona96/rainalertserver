package com.jrescalona.rainalertserver.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public class Project {
    private UUID id;

    @NotNull
    private final String name;
    private final String description;
    @NotNull
    private final Address address;

    public Project(
            @JsonProperty("projectId") UUID id,
            @JsonProperty("projectName") String name,
            @JsonProperty("projectDescription") String description,
            @JsonProperty("projectAddress") Address address) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.address = address;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public Address getAddress() {
        return address;
    }

    /**
     * Checks equality of field values
     * @param project Project
     * @return true if all fields are the same, false otherwise
     */
    public boolean equals(Project project) {
        return name.equals(project.getName())
                && description.equals(project.getDescription())
                && address.equals(project.getAddress());
    }
}
