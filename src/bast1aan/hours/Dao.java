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
import java.sql.Statement;
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

	public AuthUser findUser(String username) {
		AuthUser user = null;
		String query = "SELECT * FROM users WHERE username = ?";
		try {
			PreparedStatement stmt = cm.getConnection().prepareStatement(query);
			stmt.setString(1, username);
			ResultSet result = stmt.executeQuery();
			if (result.next()) {
				user = new AuthUser();
				populateUser(user, result);
			}
		} catch (SQLException e) {
			throw new HoursException(String.format("Error executing query: %s", query), e);
		}
		return user;
	}

	public User findUserByEmail(String email) {
		AuthUser user = null;
		String query = "SELECT * FROM users WHERE email = ?";
		try {
			PreparedStatement stmt = cm.getConnection().prepareStatement(query);
			stmt.setString(1, email);
			ResultSet result = stmt.executeQuery();
			if (result.next()) {
				user = new AuthUser();
				populateUser(user, result);
			}
		} catch (SQLException e) {
			throw new HoursException(String.format("Error executing query: %s", query), e);
		}
		return user;
	}

	public AuthUser findUserByCode(String code) {
		AuthUser user = null;
		String query = "SELECT u.* FROM users_newpassword un LEFT JOIN users u ON un.username = u.username WHERE un.confirmcode = ?";
		try {
			PreparedStatement stmt = cm.getConnection().prepareStatement(query);
			stmt.setString(1, code);
			ResultSet result = stmt.executeQuery();
			if (result.next()) {
				user = new AuthUser();
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
	public void update(AuthUser user) {
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
				populate(project, result);
				project.user = user;
				projects.add(project);
			}
		} catch (SQLException e) {
			throw new HoursException("Error executing query", e);
		}
		return projects;
	}
	
	public void addProject(Project project) {
		final String query = "INSERT INTO projects (project_name, username, is_active) VALUES (?, ?, ?)";
		try {
			String username = project.username;
			if (username == null || username.length() == 0) {
				if (project.user == null || project.user.username == null || project.user.username.length() == 0) {
					throw new HoursException("In Dao.addProject() : both username and user are not set");
				}
				username = project.user.username;
			}
			PreparedStatement stmt = cm.getConnection().prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
			stmt.setString(1, project.name);
			stmt.setString(2, username);
			stmt.setBoolean(3, project.isActive);
			int affected = stmt.executeUpdate();
			
			if (affected != 0) {
				// set genereated primary key on project object
				ResultSet keys = stmt.getGeneratedKeys();
				if (keys.next()) {
					project.id = keys.getInt(1);
				}
			}
			
		} catch (SQLException e) {
			throw new HoursException("Error executing query", e);
		}
	}
	
	public void update(Project project) {
		if (project.id <= 0) {
			throw new HoursException("id must be set");
		}
		final String query = "UPDATE projects SET project_name = ?, is_active = ? WHERE project_id = ?";
		try {
			PreparedStatement stmt = cm.getConnection().prepareStatement(query);
			stmt.setString(1, project.name);
			stmt.setBoolean(2, project.isActive);
			stmt.setInt(3, project.id);
			stmt.executeUpdate();
		} catch (SQLException e) {
			throw new HoursException("Error executing query", e);
		}
	}
	
	public Project getProject(int id) {
		Project project = null;
		final String query = "SELECT * FROM projects WHERE project_id = ?";
		try {
			PreparedStatement stmt = cm.getConnection().prepareStatement(query);
			stmt.setInt(1, id);
			ResultSet result = stmt.executeQuery();
			while (result.next()) {
				project = new Project();
				populate(project, result);
			}
		} catch (SQLException e) {
			throw new HoursException("Error executing query", e);
		}
		return project;
	}
	
	public void deleteProject(Project project) {
		deleteProject(project.id);
	}
	
	public void deleteProject(int id) {
		final String queryDelete = "DELETE FROM projects WHERE project_id = ?";
		try {
			PreparedStatement stmtDelete = cm.getConnection().prepareStatement(queryDelete);
			stmtDelete.setInt(1, id);
			stmtDelete.executeUpdate();
		} catch (SQLException e) {
			throw new HoursException("Error executing query", e);
		}

	}

	public List<Hour> getHoursByProject(Project project) {
		return getHoursByProject(project, null);
	}
	
	public List<Hour> getHoursByProject(Project project, Integer limit) {
		
		List<Hour> hours = new ArrayList<Hour>();
		String query = "SELECT * FROM hours WHERE project_id = ? ORDER BY hour_id DESC";
		if (limit != null) {
			query += " LIMIT " + limit;
		}
		try {
			PreparedStatement stmt = cm.getConnection().prepareStatement(query);
			stmt.setInt(1, project.id);
			ResultSet result = stmt.executeQuery();
			while (result.next()) {
				Hour hour = new Hour();
				populate(hour, result);
				hour.project = project;
				hours.add(hour);
			}
		} catch (SQLException e) {
			throw new HoursException("Error executing query", e);
		}
		return hours;
	}
	
	public void create(Hour hour) {
		final String query = "INSERT INTO hours (description, project_id, start, \"end\") VALUES (?, ?, ?, ?)";
		try {
			Integer projectId = hour.projectId;
			if (projectId == null || projectId <= 0) {
				if (hour.project == null || hour.project.id == null || hour.project.id <= 0 ) {
					throw new HoursException("In Dao.create(Hour) : both projectId and project not set");
				}
				projectId = hour.project.id;
			}
			PreparedStatement stmt = cm.getConnection().prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
			stmt.setString(1, hour.description);
			stmt.setInt(2, projectId);
			stmt.setTimestamp(3, hour.start != null ? new java.sql.Timestamp(hour.start.getTime()) : null);
			stmt.setTimestamp(4, hour.end != null ? new java.sql.Timestamp(hour.end.getTime()) : null);
			
			int affected = stmt.executeUpdate();
			
			if (affected != 0) {
				// set genereated primary key on project object
				ResultSet keys = stmt.getGeneratedKeys();
				if (keys.next()) {
					hour.id = keys.getInt(1);
				}
			}
			
		} catch (SQLException e) {
			throw new HoursException("Error executing query", e);
		}
		
	}

	public void update(Hour hour) {
		if (hour.id <= 0) {
			throw new HoursException("id must be set");
		}
		final String query = "UPDATE hours SET description = ?, \"end\" = ? WHERE hour_id = ?";
		try {
			PreparedStatement stmt = cm.getConnection().prepareStatement(query);
			stmt.setString(1, hour.description);
			stmt.setTimestamp(2, hour.end != null ? new java.sql.Timestamp(hour.end.getTime()) : null);
			stmt.setInt(3, hour.id);
			stmt.executeUpdate();
		} catch (SQLException e) {
			throw new HoursException("Error executing query", e);
		}
	}
	
	public Hour getHour(int id) {
		Hour hour = null;
		final String query = "SELECT * FROM hours WHERE hour_id = ?";
		try {
			PreparedStatement stmt = cm.getConnection().prepareStatement(query);
			stmt.setInt(1, id);
			ResultSet result = stmt.executeQuery();
			while (result.next()) {
				hour = new Hour();
				populate(hour, result);
			}
		} catch (SQLException e) {
			throw new HoursException("Error executing query", e);
		}
		return hour;
		
	}
	
	private void populateUser(AuthUser user, ResultSet result) throws SQLException {
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
	
	private void populate(Project project, ResultSet result) throws SQLException {
		project.id = result.getInt("project_id");
		project.name = result.getString("project_name");
		project.username = result.getString("username");
		project.isActive = result.getBoolean("is_active");
	}
	
	private void populate(Hour hour, ResultSet result) throws SQLException {
		hour.id = result.getInt("hour_id");
		hour.description = result.getString("description");
		hour.projectId = result.getInt("project_id");
		hour.start = result.getTimestamp("start");
		hour.end = result.getTimestamp("end");
	}

}
