package projects.dao;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import projects.ProjectsApp;
import projects.entity.Project;
import projects.exception.DbException;
import provided.util.DaoBase;

public class ProjectDao extends DaoBase {
	// Extends the DaoBase to allow changes to be made and to use the included data.
	// Creates each Table as a permanent object.
	private static final String CATEGORY_TABLE = "category";
	private static final String MATERIAL_TABLE = "material";
	private static final String PROJECT_TABLE = "project";
	private static final String PROJECT_CATEGORY_TABLE = "project_category";
	private static final String STEP_TABLE = "step";
	// Creates a method to insert sql code into the mysql database.
	public Project insertProject(Project project) {
		// Uses the override to prevent formatting of the included text.
		// @formatter:off
		String sql = "" + "INSERT INTO " + PROJECT_TABLE
				+ "(project_name, estimated_hours, actual_hours, difficulty, notes) "
				+ "VALUES "
				+ "(?, ?, ?, ?, ?)";
		// @formatter:on
		// First Try, starts the connection.
		try(Connection conn = DbConnection.getConnection()) {
			startTransaction(conn);
			// Second Try, sets parameters to for each input point.
			try(PreparedStatement stmt = conn.prepareStatement(sql)) {
				setParameter(stmt, 1, project.getProjectName(), String.class);
				setParameter(stmt, 2, project.getEstimatedHours(), BigDecimal.class);
				setParameter(stmt, 3, project.getActualHours(), BigDecimal.class);
				setParameter(stmt, 4, project.getDifficulty(), Integer.class);
				setParameter(stmt, 5, project.getNotes(), String.class);
				// Updates the database.
				stmt.executeUpdate();
				
				Integer projectId = getLastInsertId(conn, PROJECT_TABLE);
				commitTransaction(conn); // commits the changes.
				
				project.setProjectId(projectId);
				return project;
			} catch(Exception e) {
				// Second Catch, rolls back the connection, and throws an Exception.
				rollbackTransaction(conn);
				throw new DbException(e);
			}
		} catch(SQLException e) {
			// First Catch, throws an Exception.
			throw new DbException(e);
		}
	}

}
