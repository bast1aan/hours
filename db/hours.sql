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

CREATE TABLE users_newpassword
(
	username character varying(255), 
	salt character(32), 
	confirmcode character(32), 
	CONSTRAINT users_newpassword_pk PRIMARY KEY (confirmcode), 
	CONSTRAINT users_newpassword_users FOREIGN KEY (username) REFERENCES users (username) ON UPDATE CASCADE ON DELETE CASCADE
);

CREATE OR REPLACE FUNCTION one_hour_open_per_project()
	RETURNS trigger AS
$$
DECLARE
	cnt integer;
BEGIN
	IF NEW."end" IS NULL THEN
		SELECT COUNT(*) INTO cnt
			FROM hours h
			WHERE h.project_id = NEW.project_id AND h."end" IS NULL;
		IF cnt > 1 THEN
			RAISE EXCEPTION 'Only one hour may be open per project';
		END IF;
	END IF;
	RETURN NULL;
END;
$$
	LANGUAGE plpgsql;

CREATE CONSTRAINT TRIGGER hours_one_open_per_project
	AFTER INSERT OR UPDATE
	ON hours
	FOR EACH ROW
	EXECUTE PROCEDURE one_hour_open_per_project();
