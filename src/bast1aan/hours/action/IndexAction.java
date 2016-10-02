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
import bast1aan.hours.Settings;
import bast1aan.hours.User;
import bast1aan.hours.UserTools;
import com.opensymphony.xwork2.ActionSupport;
import javax.servlet.http.HttpServletRequest;
import lombok.Getter;
import org.apache.struts2.interceptor.ServletRequestAware;

/**
 * Action to be called when index request is made.
 * Makes sure there is a logged in user. See strus.xml and index.jsp
 */
public class IndexAction extends ActionSupport implements ServletRequestAware {

	private HttpServletRequest request;
	
	@Getter private User user;
	@Getter private String baseUrl;
	
	@Override
	public void setServletRequest(HttpServletRequest hsr) {
		this.request = hsr;
	}

	@Override
	public String execute() throws Exception {
		User fullUser = SessionContainer.getUser(request.getSession());
		if (fullUser == null) {
			return LOGIN;
		}
		user = UserTools.userWithoutPrivateData(fullUser);
	
		Settings settings = Settings.getInstance();
		baseUrl = settings.get(Settings.BASE_URL);
		
		return SUCCESS;
	}

}
