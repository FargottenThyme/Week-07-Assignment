package projects;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;
import java.util.Scanner;

import projects.entity.Project;
import projects.exception.DbException;
import projects.service.ProjectService;

public class ProjectsApp {
	// Creates the scanner object for user input.
	private Scanner scanner = new Scanner(System.in);
	// Creates the object used in tandem with ProjectService.java
	private ProjectService projectService = new ProjectService();
	// Creates the object used to store current Projects.
	private Project curProject;
	// Uses overrides to prevent the formatter from reformatting the text within.
	// @formatter:off
	private List<String> operations = List.of(
			"1) Add a project",
			"2) List projects",
			"3) Select a project",
			"4) Update project details",
			"5) Delete a project"
	);
	// @formatter:on

	public static void main(String[] args) {
		// Starts the program, using the method: "processUserSelection"
		new ProjectsApp().processUserSelection();

	}

	// Creates the method that processes the user input.
	private void processUserSelection() {
		// Declares the variable "done" to be used by method.
		boolean done = false;
		// Starts a while loop that loops until done = true.
		while (!done) {
			// A try/catch block.
			try {
				int selection = getUserSelection(); // Set the user input to a local variable of type int.
				// Creates a switch block to with cases linked to following methods.
				switch (selection) {
				case -1:
					done = exitMenu();
					break;

				case 1:
					createProject();
					break;

				case 2:
					listProjects();
					break;

				case 3:
					selectProject();
					break;

				case 4:
					updateProjectDetails();
					break;

				case 5:
					deleteProject();
					break;

				default:
					System.out.println("\n" + selection + " is not a valid selection. Try again.");
					break;

				}
			} catch (Exception e) {
				// If the user input is not an integer, an exception will result
				// and will print an appropriate message.
				System.out.println("\nError: " + e + " Try again.");
			}
		}
	}

	private void deleteProject() {
		listProjects();
		Integer projectId = getIntInput("Enter the ID of the project to be deleted");
		try {
			projectService.deleteProject(projectId);
			System.out.println("Project " + projectId + " has been successfully deleted.");
			
			if (projectId == curProject.getProjectId() && Objects.nonNull(curProject)) {
				curProject = null;
			}
			
		} catch (Exception e) {
			throw new DbException(e);
		}
	}

	private void updateProjectDetails() {
		if (Objects.isNull(curProject)) {
			System.out.println("\nPlease select a project.");
			return;
		}

		String projectName = getStringInput("Enter the project name [" + curProject.getProjectName() + "]");
		BigDecimal estimatedHours = getDecimalInput(
				"Enter the estimated hours [" + curProject.getEstimatedHours() + "]");
		BigDecimal actualHours = getDecimalInput("Enter the actual hours [" + curProject.getActualHours() + "]");
		Integer difficulty = getIntInput("Enter the difficulty (1-5) [" + curProject.getDifficulty() + "]");
		String notes = getStringInput("Enter the notes [" + curProject.getNotes() + "]");

		Project project = new Project();
		project.setProjectId(curProject.getProjectId());

		project.setProjectName(Objects.isNull(projectName) ? curProject.getProjectName() : projectName);
		project.setEstimatedHours(Objects.isNull(estimatedHours) ? curProject.getEstimatedHours() : estimatedHours);
		project.setActualHours(Objects.isNull(actualHours) ? curProject.getActualHours() : actualHours);
		project.setDifficulty(Objects.isNull(difficulty) ? curProject.getDifficulty() : difficulty);
		project.setNotes(Objects.isNull(notes) ? curProject.getNotes() : notes);

		projectService.modifyProjectDetails(project);

		curProject = projectService.fetchProjectById(curProject.getProjectId());

	}

	private void selectProject() {
		listProjects();
		Integer projectId = getIntInput("Enter a project ID to select a project");

		curProject = null;

		curProject = projectService.fetchProjectById(projectId);

		if (Objects.isNull(curProject)) {
			System.out.println("\nYou are not working with a project.");
		} else {
			System.out.println("\nYou are working with project: " + curProject);
		}
	}

	private void listProjects() {
		List<Project> projects = projectService.fetchAllProjects();

		System.out.println("\nProjects:");

		projects.forEach(
				project -> System.out.println("   " + project.getProjectId() + ": " + project.getProjectName()));
	}

	/*
	 * Create the method that creates the related Project within the console. Sets
	 * each object to a type and getter, sends a message asking for each object, and
	 * sets the input of each setter to the paired getter. Finally, creates a
	 * project within the dbProject database.
	 */
	private void createProject() {
		String projectName = getStringInput("Enter the project name");
		BigDecimal estimatedHours = getDecimalInput("Enter the estimated hours");
		BigDecimal actualHours = getDecimalInput("Enter the actual hours");
		Integer difficulty = getIntInput("Enter the project difficulty (1-5)");
		String notes = getStringInput("Enter the project notes");

		Project project = new Project();

		project.setProjectName(projectName);
		project.setEstimatedHours(estimatedHours);
		project.setActualHours(actualHours);
		project.setDifficulty(difficulty);
		project.setNotes(notes);

		Project dbProject = ProjectService.addProject(project);
		System.out.println("You have successfully created project: " + dbProject);
	}

	// Exits the program.
	private boolean exitMenu() {
		System.out.println("Exiting the menu.");
		return true;
	}

	// uses a getter method to return an input from the user.
	private int getUserSelection() {
		printOperations();

		Integer input = getIntInput("Enter a menu selection");
		// If the input is null, return -1, else return input.
		return Objects.isNull(input) ? -1 : input;
	}

	// Creates a getter for Integer type inputs.
	private Integer getIntInput(String prompt) {
		String input = getStringInput(prompt);

		if (Objects.isNull(input)) {
			return null;
		}
		// Uses a try/catch block to ensure the input is a valid integer.
		try {
			return Integer.valueOf(input);
		} catch (NumberFormatException e) {
			throw new DbException(input + " is not a valid number.");
		}
	}

	// Creates the method for getting the input of type BigDecimal.
	private BigDecimal getDecimalInput(String prompt) {
		String input = getStringInput(prompt);

		if (Objects.isNull(input)) {
			return null;
		}
		// Uses a try/catch block to ensure the input is a valid decimal.
		try {
			return new BigDecimal(input).setScale(2);
		} catch (NumberFormatException e) {
			throw new DbException(input + " is not a valid decimal number.");
		}
	}

	// Creates the method that takes in the initial data from the input.
	private String getStringInput(String prompt) {
		System.out.print(prompt + ": ");
		String input = scanner.nextLine();
		// If the input is blank, return null, else, trim the whitespace.
		return input.isBlank() ? null : input.trim();
	}

	// Prints the available options.
	private void printOperations() {
		System.out.println();
		System.out.println("\nThese are the available selections. Press the Enter key to quit:");

		operations.forEach(line -> System.out.println("  " + line));
	}

}
