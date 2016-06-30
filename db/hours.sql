CREATE TABLE users (
	username varchar(255) NOT NULL,
	password char(32) NOT NULL,
	salt char(32)  NOT NULL,
	fullname varchar(512) NOT NULL,
	email varchar(255) NOT NULL,
	CONSTRAINT users_pk PRIMARY KEY (username)
);

CREATE UNIQUE INDEX email_unique
	ON users (email);

CREATE TABLE projects (
	project_id SERIAL,
	project_name varchar(255),
	username varchar(255),
	CONSTRAINT projects_pk PRIMARY KEY (project_id)
);

ALTER TABLE projects
	ADD CONSTRAINT projects_users FOREIGN KEY (username) REFERENCES users (username)
	ON UPDATE RESTRICT ON DELETE RESTRICT;
CREATE INDEX fki_projects_users
	ON projects(username);

CREATE TABLE hours (
	hour_id SERIAL,
	description TEXT,
	project_id INTEGER,
	start timestamp without time zone,
	"end" timestamp without time zone,
	CONSTRAINT hours_pk PRIMARY KEY (hour_id)
);

ALTER TABLE hours
	ADD CONSTRAINT hours_projects FOREIGN KEY (project_id) REFERENCES projects (project_id)
	ON UPDATE RESTRICT ON DELETE RESTRICT;
CREATE INDEX fki_hours_projects
	ON hours(project_id);

