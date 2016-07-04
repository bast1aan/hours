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
package bast1aan.hours;

import javax.servlet.http.HttpSession;

/**
 * Session container acts as Session data provider and
 * maintains definitions of data to be stored in the Session
 * and provides in type-safe way.
 */
public class SessionContainer {
	
	private static final String USER = "user"; 
	
	public static User getUser(HttpSession session) {
		Object user = session.getAttribute(USER);
		if (user instanceof User) {
			return (User)user;
		}
		return null;
	}
	
	public static void setUser(HttpSession session, User user) {
		session.setAttribute(USER, user);
	}

	public static void clearUser(HttpSession session) {
		session.removeAttribute(USER);
	}

}
