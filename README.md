hours
========

Application to keep time registration of projects.

The application is devided into:

* login management; set of models, jsps and struts actions to
  login, logout and regain a new password by email.
* frontend javascript application build with BackboneJS models
  and views
* struts2 json backend, set of controllers serving request
  between the frontend and database backend.

(C) Copyright 2016 Bastiaan Welmers 

how to build and run
--------------------

Requirements:

* java-openjdk 6 or higher
* java-openjdk-devel
* tomcat 6 or higher to run the application  
  another servlet engine implementing servlet api 2.5 or higher
  should work as well (not tested)
* database engine working with JDBC. I use postgresql for this.
* apache-ant to build the application
* apache-ivy to resolve dependencies for the application

There are several dependencies that are retrieved by Ant Ivy. Please look at the 
ant resolve task in build.xml and ivy.xml

How to build:

* Create the tables in your database engine with the SQL scripts
  in the directory db/
* Find a proper JDBC connector for the database engine you use.
  For postgresql see http://jdbc.postgresql.org/download.html
  Place the jar in the lib/ subdirectory.
* create a new settings.properties in the src/ directory with
  information how to connect to the database. See 
  settings-example.properties what is need.

Then use ant to resolve dependencies and build the project:

* `$ ant resolve`
* `$ ant build`

Then place the generated war file in the tomcat webapps directory:

* `$ cp hours.war /var/lib/tomcat/webapps/`

The application should then be available under
 http://localhost:8080/hours/ !


