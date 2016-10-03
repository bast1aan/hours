/*
 * Hours
 * Copyright (C) 2016 Bastiaan Welmers, bastiaan@welmers.net
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License version 2 as
 * published by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * version 2 along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 */
package bast1aan.hours.action.json;

import bast1aan.hours.Dao;
import bast1aan.hours.Project;
import static com.opensymphony.xwork2.Action.ERROR;
import static com.opensymphony.xwork2.Action.LOGIN;
import static com.opensymphony.xwork2.Action.SUCCESS;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

public class ProjectController extends BaseController {

	@Getter private List<Project> projects;
	@Setter @Getter private Project project;

	public String listAction() throws Exception {
		if (!isValidUser()) {
			return LOGIN;
		}
		Dao dao = Dao.getInstance();
		projects = dao.getProjects(user);
		return SUCCESS;
	}
	
	public String saveAction() throws Exception {
		if (!isValidUser()) {
			return LOGIN;
		}
		if (project == null) {
			error = "No project given";
			return ERROR;
		}
		project.username = user.username;
		project.user = user;
		Dao dao = Dao.getInstance();
		dao.addProject(project);
		
		return SUCCESS;
		
	}
	
	public String updateAction() throws Exception {
		if (!isValidUser()) {
			return LOGIN;
		}
		String method = request.getMethod();
		if (!method.equals("PUT") && !method.equals("PATCH")) {
			error = "Invalid HTTP method";
			return ERROR;
		}
		if (project == null) {
			error = "No project given";
			return ERROR;
		}
		if (project.id == null || project.id <= 0) {
			error = "No project id given";
			return ERROR;
		}

		Dao dao = Dao.getInstance();
		// check if project exists and logged in user is its owner
		Project dbProject = dao.getProject(project.id);
		if (dbProject == null) {
			error = "Project does not exist";
			return ERROR;
		}	
		if (!dbProject.username.equals(user.username)) {
			error = "No access to this project";
			return LOGIN;
		}
		dbProject.name = project.name;
		
		dao.update(dbProject);
		
		project = dbProject;
		
		return SUCCESS;
		
	}
	
	public String deleteAction() throws Exception {
		if (!isValidUser()) {
			return LOGIN;
		}
		if (!request.getMethod().equals("DELETE")) {
			error = "Invalid HTTP method";
			return ERROR;
		}
		if (project == null) {
			error = "No project given";
			return ERROR;
		}
		if (project.id == null || project.id <= 0) {
			error = "No project id given";
			return ERROR;
		}

		Dao dao = Dao.getInstance();
		// check if project exists and logged in user is its owner
		Project dbProject = dao.getProject(project.id);
		if (dbProject == null) {
			error = "Project does not exist";
			return ERROR;
		}	
		if (!dbProject.username.equals(user.username)) {
			error = "No access to this project";
			return LOGIN;
		}
		dao.deleteProject(dbProject);
		return SUCCESS;
	}

	@Override
	public String getError() {
		return error;
	}
}
