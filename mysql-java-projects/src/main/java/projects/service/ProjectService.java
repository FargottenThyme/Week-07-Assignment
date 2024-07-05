package projects.service;

import projects.dao.ProjectDao;
import projects.entity.Project;

public class ProjectService {
	// Creates the private (within class) object and sets it to a type ProjectDao.
	private static ProjectDao projectDao = new ProjectDao();
	// Creates a method to add a project to the database.
	public static Project addProject(Project project) {
		return projectDao.insertProject(project);
	}

}
