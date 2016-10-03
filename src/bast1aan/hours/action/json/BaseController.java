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

import bast1aan.hours.AuthUser;
import bast1aan.hours.SessionContainer;
import bast1aan.hours.User;
import bast1aan.hours.UserTools;
import javax.servlet.http.HttpServletRequest;
import lombok.Setter;
import org.apache.struts2.interceptor.ServletRequestAware;

abstract public class BaseController implements ServletRequestAware {

	protected HttpServletRequest request;
	protected User user;
	@Setter protected String username;
	@Getter protected String error;

	protected boolean isValidUser() {
		AuthUser authUser = SessionContainer.getUser(request.getSession());
		if (authUser == null || username == null || ! authUser.username.equals(username)) {
			error = "Invalid user";
			return false;
		}
		user = UserTools.userWithoutPrivateData(authUser);
		return true;
	}
	
	@Override
	public void setServletRequest(HttpServletRequest request) {
		this.request = request;
	}
	
}
