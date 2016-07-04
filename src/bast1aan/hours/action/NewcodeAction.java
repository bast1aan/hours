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

import bast1aan.hours.Dao;
import bast1aan.hours.User;
import bast1aan.hours.UserTools;
import com.opensymphony.xwork2.ActionSupport;
import javax.servlet.http.HttpServletRequest;
import lombok.Getter;
import lombok.Setter;
import org.apache.struts2.interceptor.ServletRequestAware;

public class NewcodeAction extends ActionSupport implements ServletRequestAware {

	@Setter private String usernameoremail;
	@Getter private String result;
	private HttpServletRequest request;
	
	@Override
	public String execute() throws Exception {
		if (usernameoremail != null && usernameoremail.length() > 0) {
			User user = null;
			Dao dao = Dao.getInstance();
			
			user = dao.findUser(usernameoremail);
			
			if (user == null)
				user = dao.findUserByEmail(usernameoremail);
			
			if (user != null) {
				String code = UserTools.generateNewCode();
				dao.insertNewCode(user, code);
				UserTools.sendCodeMail(
						user, 
						getHost(),
						code
					);	
			} // if user not found, do not provide information
			return SUCCESS;
		}
		result = "No username or email given";
		return ERROR;
	}

	@Override
	public void setServletRequest(HttpServletRequest hsr) {
		this.request = hsr;
	}
	
	private String getHost() {
		int port = request.getServerPort();
		boolean secure = request.isSecure();
		String portSpec = "";
		if ((secure && port != 443) || (!secure && port != 80)) {
			portSpec = String.format(":%d", port);
		}
		return String.format("%s://%s%s", 
			secure ? "https" : "http", 
			request.getServerName(),
			portSpec
		);
	}

}
