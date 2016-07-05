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

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

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
	
	/**
	 * Inserts new confirmation code for user.
	 * Removes the old one if exists
	 * 
	 * @param user
	 * @param code 
	 */
	public void insertNewCode(User user, String code) {
		
		final String query = "INSERT INTO users_newpassword (username, confirmcode) VALUES (?, ?)";
		
		final Connection connection = cm.getConnection();

		try {
			connection.setAutoCommit(false);

			try {
				deleteCode(user.username, connection);
				PreparedStatement stmt = connection.prepareStatement(query);
				stmt.setString(1, user.username);
				stmt.setString(2, code);
				stmt.executeUpdate();

				connection.commit();

			} catch (SQLException e) {
				connection.rollback();
				throw new HoursException("Error executing query", e);
			} finally {
				connection.setAutoCommit(true);
			}
		} catch (SQLException e) {
			throw new HoursException("Error while executing transaction in JDBC", e);
		}
	}
	
	/**
	 * Update the user to the database.
	 * Removes confirmation codes as well, if any
	 * 
	 * @param user 
	 */
	public void update(User user) {
		final String query = "UPDATE users SET password = ?, email = ?, fullname = ?, salt = ? WHERE username = ?";

		final Connection connection = cm.getConnection();

		try {
			connection.setAutoCommit(false);

			try {
				deleteCode(user.username, connection);
				
				PreparedStatement stmt = connection.prepareStatement(query);
				
				stmt.setString(1, user.password);
				stmt.setString(2, user.email);
				stmt.setString(3, user.fullname);
				stmt.setString(4, user.salt);
				
				stmt.setString(5, user.username); // primary key
				
				stmt.executeUpdate();

				connection.commit();

			} catch (SQLException e) {
				connection.rollback();
				throw new HoursException("Error executing query", e);
			} finally {
				connection.setAutoCommit(true);
			}
		} catch (SQLException e) {
			throw new HoursException("Error while executing transaction in JDBC", e);
		}
		
	}
	
	public void deleteCode(User user) {
		try {
			deleteCode(user.username, cm.getConnection());
		} catch (SQLException e) {
			throw new HoursException("Error executing query", e);
		}
	}
	
	public List<Project> getProjects(User user) {
		List<Project> projects = new ArrayList<Project>();
		final String query = "SELECT * FROM projects WHERE username = ?";
		try {
			PreparedStatement stmt = cm.getConnection().prepareStatement(query);
			stmt.setString(1, user.username);
			ResultSet result = stmt.executeQuery();
			while (result.next()) {
				Project project = new Project();
				project.id = result.getInt("project_id");
				project.name = result.getString("project_name");
				project.username = result.getString("username");
				project.user = user;
				projects.add(project);
			}
		} catch (SQLException e) {
			throw new HoursException("Error executing query", e);
		}
		return projects;
	}
	
	private void populateUser(User user, ResultSet result) throws SQLException {
		user.username = result.getString("username");
		user.password = result.getString("password");
		user.email = result.getString("email");
		user.fullname = result.getString("fullname");
		user.salt = result.getString("salt");	
	}

	private void deleteCode(String username, Connection connection) throws SQLException {
		final String queryDelete = "DELETE FROM users_newpassword WHERE username = ?";
		PreparedStatement stmtDelete = connection.prepareStatement(queryDelete);
		stmtDelete.setString(1, username);
		stmtDelete.executeUpdate();
	}

}
