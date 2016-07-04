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

import bast1aan.hours.SessionContainer;
import bast1aan.hours.User;
import com.opensymphony.xwork2.ActionSupport;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import lombok.Getter;
import org.apache.struts2.interceptor.ServletRequestAware;

public class LogoutAction extends ActionSupport implements ServletRequestAware {

	private HttpServletRequest request;
	
	@Getter private String result;
	
	@Override
	public void setServletRequest(HttpServletRequest hsr) {
		this.request = hsr;
	}

	@Override
	public String execute() throws Exception {
		HttpSession session = request.getSession();
		User user = SessionContainer.getUser(session);
		if (user == null) {
			result = "Not logged in";
			return ERROR;
		}
		SessionContainer.clearUser(session);
		
		result = "Successfully logged out";
		return SUCCESS;
	}

}