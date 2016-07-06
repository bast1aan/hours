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
import bast1aan.hours.SessionContainer;
import bast1aan.hours.User;
import bast1aan.hours.UserTools;
import static com.opensymphony.xwork2.Action.ERROR;
import static com.opensymphony.xwork2.Action.LOGIN;
import static com.opensymphony.xwork2.Action.SUCCESS;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import lombok.Getter;
import lombok.Setter;
import org.apache.struts2.interceptor.ServletRequestAware;

public class ProjectController implements ServletRequestAware {

	@Setter private String username;
	@Getter private List<Project> projects;
	@Getter private String error;
	private HttpServletRequest request;
	
	public String listAction() throws Exception {
		User user = SessionContainer.getUser(request.getSession());
		if (user != null && user.username.equals(username)) {
			Dao dao = Dao.getInstance();
			projects = dao.getProjects(UserTools.userWithoutPrivateData(user));
			return SUCCESS;
		} else {
			error = "Invalid user";
			return LOGIN;
		}
	}
	
	@Override
	public void setServletRequest(HttpServletRequest hsr) {
		this.request = hsr;
	}

}
