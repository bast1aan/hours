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
import bast1aan.hours.Hour;
import bast1aan.hours.Project;
import static com.opensymphony.xwork2.Action.LOGIN;
import static com.opensymphony.xwork2.Action.SUCCESS;
import static com.opensymphony.xwork2.Action.ERROR;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

public class HourController extends BaseController {

	@Getter private List<Hour> hours;
	@Setter private Integer projectId;
	private final Dao dao = Dao.getInstance();
	
	public String listAction() throws Exception {
		if (!isValidUser()) {
			return LOGIN;
		}
		Project project = getProject();
		if (project == null) {
			error = "No project given, or no access to given project";
			return ERROR;
		}
		hours = dao.getHoursByProject(project, 30);
		return SUCCESS;
	}

	private Project getProject() {
		Project project = dao.getProject(projectId);
		if (project == null || project.username == null || !project.username.equals(user.username)) {
			return null;
		}
		return project;
	}
	
}
