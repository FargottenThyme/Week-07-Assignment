package projects.service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import projects.dao.ProjectDao;
import projects.entity.Project;

public class ProjectService {
	// Creates the private (within class) object and sets it to a type ProjectDao.
	private static ProjectDao projectDao = new ProjectDao();
	// Creates a method to add a project to the database.
	public static Project addProject(Project project) {
		return projectDao.insertProject(project);
	}
	public List<Project> fetchAllProjects() {
		return projectDao.fetchAllProjects();
	}
	public Project fetchProjectById(Integer projectId) {
		return projectDao.fetchProjectById(projectId).orElseThrow(() -> new NoSuchElementException("Project with project ID=" + projectId + " does not exist."));
	}

}
