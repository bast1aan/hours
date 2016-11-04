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

package bast1aan.hours.action;

import bast1aan.hours.AuthUser;
import bast1aan.hours.Dao;
import bast1aan.hours.Hour;
import bast1aan.hours.Project;
import bast1aan.hours.SessionContainer;
import com.opensymphony.xwork2.ActionSupport;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import lombok.Getter;
import org.apache.struts2.interceptor.ServletRequestAware;
import java.text.SimpleDateFormat;

public class OverviewAction extends ActionSupport implements ServletRequestAware {	

	public static class View {
		
		private static final SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		
		/**
		 * String representation of time delta
		 * @param miliseconds
		 * @return 
		 */
		public String displayTimeDiff(long miliseconds) {
			StringBuilder result = new StringBuilder();
			long seconds = Math.round((double) miliseconds / 1000.0);
			long minutes = seconds / 60;
			long secondsRemain = seconds % 60;
			long hours = minutes / 60;
			long minutesRemain = minutes % 60;
			if (hours > 0) {
				result.append(Long.toString(hours));
				result.append('h');
			}
			if (minutesRemain > 0) {
				result.append(Long.toString(minutesRemain));
				result.append("min");
			}
			result.append(Long.toString(secondsRemain));
			result.append("sec");
			return result.toString();
			
		}
		
		public String displayTimeDiff(Date start, Date end) {
			if (start == null || end == null) return "";
			return displayTimeDiff(end.getTime() - start.getTime());
		}
		
		public String displayDate(Date date) {
			if (date == null) return "";
			return dateFormatter.format(date);
		}
		
		public long timeDiffToLong(Date start, Date end) {
			if (start == null || end == null) return 0L;
			return end.getTime() - start.getTime();
		}
	}
	
	private HttpServletRequest request;
	
	@Getter private Project project;
	@Getter private List<Project> projects = Collections.emptyList();
	@Getter private List<Hour> hours = Collections.emptyList();

	@Override
	public void setServletRequest(HttpServletRequest request) {
		this.request = request;
	}
	
	public String execute() throws Exception {
		// TODO similar code already exists in json.BaseController
		AuthUser authUser = SessionContainer.getUser(request.getSession());
		if (authUser == null) {
			return LOGIN;
		}
		
		Dao dao = Dao.getInstance();
		projects = dao.getProjects(authUser);
		
		String projectIdStr = request.getParameter("project_id");
		if (projectIdStr != null) {
			int projectId = Integer.parseInt(projectIdStr);
			for(Project prj : projects) {
				if (prj.id == projectId) {
					project = prj;
					hours = dao.getHoursByProject(project);
					break;
				} 
			}
		}
		return SUCCESS;
	}
	
	
}
