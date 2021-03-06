package com.jrescalona.rainalertserver.service;

import com.jrescalona.rainalertserver.dao.IProjectsDao;
import com.jrescalona.rainalertserver.model.Project;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class ProjectsService {

    private final IProjectsDao projectsDoa;

    @Autowired
    public ProjectsService(@Qualifier("PostgresProjectDao") IProjectsDao projectsDoa) {
        this.projectsDoa = projectsDoa;
    }

    /**
     * Adds a new project
     * @param project Project
     */
    public void addProject(Project project) throws RuntimeException {
      projectsDoa.insertProject(project);
    }

    /**
     * Gets all available projects
     * @return List<Project>
     */
    public List<Project> getAllProjects() {
        return projectsDoa.selectAllProjects();
    }

    /**
     * Gets project with given id
     * @param id UUID
     * @return Project if found, null otherwise
     */
    public Project getProjectById(UUID id) {
        return projectsDoa.selectProjectById(id).orElse(null);
    }

    /**
     * Gets all projects associated with a user
     * @param userId UUID
     * @return List<Project>
     */
    public List<Project> getAllProjectByUserId(UUID userId) {
        return projectsDoa.selectAllProjectsByUserId(userId);
    }

    /**
     * Update a project with new project instance using id
     * @param id UUID
     * @param project Project
     * @return 0 if successful, 1 otherwise
     */
    public int updateProjectById(UUID id, Project project) {
        return projectsDoa.updateProjectById(id, project);
    }

    /**
     * Delete a project using id
     * @param id UUID
     * @return 0 if successful, 1 otherwise
     */
    public int deleteProjectById(UUID id) {
        return projectsDoa.deleteProjectById(id);
    }

}
