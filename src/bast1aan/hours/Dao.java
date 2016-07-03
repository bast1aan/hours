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

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Dao {
	private static Dao instance;

	public static Dao getInstance() {
		if (instance == null) {
			instance = new Dao();
		}
		return instance;
	}
	
	private final ConnectionManager cm = new ConnectionManager();

	public User findUser(String username) {
		User user = null;
		String query = "SELECT * FROM users WHERE username = ?";
		try {
			PreparedStatement stmt = cm.getConnection().prepareStatement(query);
			stmt.setString(1, username);
			ResultSet result = stmt.executeQuery();
			if (result.next()) {
				user = new User();
				populateUser(user, result);
			}
		} catch (SQLException e) {
			throw new HoursException(String.format("Error executing query: %s", query), e);
		}
		return user;
	}

	public User findUserByEmail(String email) {
		User user = null;
		String query = "SELECT * FROM users WHERE email = ?";
		try {
			PreparedStatement stmt = cm.getConnection().prepareStatement(query);
			stmt.setString(1, email);
			ResultSet result = stmt.executeQuery();
			if (result.next()) {
				user = new User();
				populateUser(user, result);
			}
		} catch (SQLException e) {
			throw new HoursException(String.format("Error executing query: %s", query), e);
		}
		return user;
	}

	public User findUserByCode(String code) {
		User user = null;
		String query = "SELECT u.* FROM users_newpassword un LEFT JOIN users u ON un.username = u.username WHERE un.confirmcode = ?";
		try {
			PreparedStatement stmt = cm.getConnection().prepareStatement(query);
			stmt.setString(1, code);
			ResultSet result = stmt.executeQuery();
			if (result.next()) {
				user = new User();
				populateUser(user, result);
			}
		} catch (SQLException e) {
			throw new HoursException(String.format("Error executing query: %s", query), e);
		}
		return user;
	}
	
	private void populateUser(User user, ResultSet result) throws SQLException {
		user.username = result.getString("username");
		user.password = result.getString("password");
		user.email = result.getString("email");
		user.fullname = result.getString("fullname");
		user.salt = result.getString("salt");	
	}
	
}
