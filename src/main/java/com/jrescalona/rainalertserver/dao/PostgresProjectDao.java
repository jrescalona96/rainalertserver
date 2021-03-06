package com.jrescalona.rainalertserver.dao;

import com.jrescalona.rainalertserver.mapper.ProjectMapper;
import com.jrescalona.rainalertserver.model.Address;
import com.jrescalona.rainalertserver.model.Project;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository("PostgresProjectDao")
public class PostgresProjectDao implements IProjectsDao {

    private final JdbcTemplate jdbcTemplate;
    private final IAddressDao addressDao;

    @Autowired
    public PostgresProjectDao(JdbcTemplate jdbcTemplate, @Qualifier("PostgresAddressDao") IAddressDao addressDoa) {
        this.jdbcTemplate = jdbcTemplate;
        this.addressDao = addressDoa;
    }

    /**
     * Insert new project to db
     * @param id project id
     * @param project new project to insert
     * @return 0 if successful, 1 otherwise
     */
    @Override
    public void insertProject(UUID id, Project project) throws RuntimeException {
        try {
            // create address id
            // TODO: store new address to database if new
            UUID addressId = UUID.randomUUID();
            addressDao.insertAddress(addressId, project.getAddress());
            final String INSERT_SQL = "INSERT INTO project(" +
                        "id, " +
                        "user_id, " +
                        "name, " +
                        "description, " +
                        "address_id) " +
                    "VALUES(?, ?, ?, ?, ?)";

            jdbcTemplate.update(
                    INSERT_SQL,
                    id,
                    project.getUserId(),
                    project.getName(),
                    project.getDescription(),
                    addressId );

        } catch (RuntimeException e) {
            System.err.println(e.getMessage());
            throw e;
        }
    }

    /**
     * Select project by project id
     * @param id project id
     * @return Project if project exists, null otherwise
     */
    @Override
    public Optional<Project> selectProjectById(UUID id) throws EmptyResultDataAccessException{
        final String SQL = "SELECT " +
                            "p.id as project_id," +
                            "p.name," +
                            "p.description," +
                            "a.id as address_id," +
                            "a.address_line1," +
                            "a.address_line2," +
                            "a.city," +
                            "a.state," +
                            "a.postal_code" +
                        "\nFROM project p" +
                        "\nJOIN address a" +
                        "\nON a.id = p.address_id" +
                        "\nWHERE p.id = ?";

        Project foundProject = jdbcTemplate.queryForObject(SQL, new ProjectMapper(), id);
        return Optional.ofNullable(foundProject);
    }

    /**
     * Returns all projects in the database
     * Do not expose to the public
     * Used for testing purposes
     * @return List<Projects>
     */
    @Override
    public List<Project> selectAllProjects() {
        final String SQL = "SELECT p.id as project_id," +
                        "p.name," +
                        "p.description," +
                        "a.id as address_id," +
                        "a.address_line1," +
                        "a.address_line2," +
                        "a.city," +
                        "a.state," +
                        "a.postal_code" +
                    "\nFROM project p" +
                    "\nJOIN address a" +
                    "\nON a.id = p.address_id";
        return jdbcTemplate.query(SQL, new ProjectMapper());
    }

    /**
     * Selects all projects associated with user
     * @param id user's id
     * @return List<Projects> list of all projects by user
     */
    @Override
    public List<Project> selectAllProjectsByUserId(UUID id) {
        // Get list of project ids associated with user
        final String SQL = "SELECT id FROM project WHERE user_id = ?";
        List<UUID> projectIds = jdbcTemplate.query(SQL, (resultSet, i) -> UUID.fromString(resultSet.getString("id")) ,id);

        // Add projects
        List<Project> projects = new ArrayList<>();
        for (UUID uuid : projectIds) {
            selectProjectById(uuid).ifPresent(projects::add);
        }
        return projects;
    }

    /**
     * Update a project using project id
     * @param id project id
     * @param updateProject new project details
     * @return 0
     */
    @Override
    public int updateProjectById (UUID id, Project updateProject) {
        Optional<Project> project = selectProjectById(id);
        Optional<Address> address = addressDao.selectAddressById( updateProject.getAddress().getId() );
        if(project.isPresent()) {
           final String UPDATE_SQL = "UPDATE project" +
                    "SET name = ?" +
                    "description = ?" +
                    "WHERE id = ?";
            jdbcTemplate.update(
                    UPDATE_SQL,
                    updateProject.getDescription(),
                    updateProject.getName(),
                    id );
            return 0;
        }
        return 1;
    }

    /**
     * Delete one project
     * @param id project id
     * @return 0
     */
    @Override
    public int deleteProjectById (UUID id) {
        String sql = "DELETE FROM project CASCADE WHERE id = ?";
        jdbcTemplate.update(sql, id);
        return 0;
    }

}
