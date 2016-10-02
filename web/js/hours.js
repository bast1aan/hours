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

"use strict";

var username;

var baseUrl;

var projectListHtml = loadTemplate("js/templates/projectlist.html");

var Project = Backbone.Model.extend( {
	defaults: {
		id: 0,
		name: ''
	}
});

var ProjectsCollection = Backbone.Collection.extend({model : Project});

var projects = new ProjectsCollection();

function retrieveProjects(username) {
	var url = "/projects/list?username=" + username;
	if (baseUrl) {
		url = baseUrl + url;
	}
	$.ajax({
		url : url,
		cache : false,
		type : 'GET',
		dataType : 'json',
		crossDomain : true,
		success : function(data){
			if (data.projects) {
					projects.reset(data.projects);
			}
		},
		error : function(jqHXR, textStatus, e) {
			throw "Error executing request: " + textStatus + " : " + e;
		}
	});
}

function deleteProject(id, view) {
	var url = "/projects/delete?username=" + username;
	if (baseUrl) {
		url = baseUrl + url;
	}
	$.ajax({
		url : url,
		cache : false,
		type : 'DELETE',
		dataType : 'json',
		crossDomain : true,
		data : JSON.stringify({username : username, project : { id : id }}),
		contentType : 'application/json',
		success : function() {
			if (view) {
				view.collection.remove(id);
				view.render();
			}
		}
	});
}

function renameProject(id, name, view) {
	var url = "/projects/update?username=" + username;
	if (baseUrl) {
		url = baseUrl + url;
	}
	$.ajax({
		url : url,
		cache : false,
		type : 'PUT',
		dataType : 'json',
		crossDomain : true,
		data : JSON.stringify({username : username, project : { id : id, name : name }}),
		contentType : 'application/json',
		success : function() {
			if (view) {
				view.collection.get(id).set('name', name);
				view.render();
			}
		}
	});

}

var ProjectListView = Backbone.View.extend({
	initialize : function() {this.listenTo(this.collection, "reset", this.render);},
	render : function() {
		this.$el.html(_.template(projectListHtml, {projects : this.collection}));
		return this;
	},
	events : {
		"click .delete" : "deleteProject",
		"dblclick .name" : "editName",
	},
	deleteProject : function(e) {
		var projectId = $(e.currentTarget).data('id');
		var projectName = this.collection.get(projectId).get('name');
		if (confirm('Are you sure you want to remove project "' + projectName + '"?')) {
			deleteProject(projectId, this);
		}
	},
	editName : function(e) {
		var view = this;
		var $el = $(e.currentTarget);
		var projectId = $el.data('id');
		var name = this.collection.get(projectId).get('name');
		var input = document.createElement("input");
		input.setAttribute('type', 'text');
		input.setAttribute('value', name);
		$(input).on('keydown', function(keyEvent) {
			if (keyEvent.keyCode == 13 /* enter key */) {
				var newName = keyEvent.currentTarget.value;
				if (confirm('Are you sure you want to rename project "' + name + '" to "'+ newName + '"?')) {
					renameProject(projectId, newName, view);
				}
			}

			if (keyEvent.keyCode == 27 /* esc key */) {
				e.currentTarget.removeChild(input);
				view.render();
			}
		})
		e.currentTarget.innerHTML = '';
		e.currentTarget.appendChild(input);
		input.focus();
	}
});

var listView = new ProjectListView({collection: projects});

$(document).ready(function() {
	username = Cookies.get('hours_username');
	baseUrl = Cookies.get('hours_base_url');
	if (username) {
		$('#loginbar').html('Logged in as: ' + username + ' <a href="' + baseUrl + '/logout.action">Logout</a>');
		$('#application').append(listView.$el);
		retrieveProjects(username);
	} else {
		$('#loginbar').html('Not logged in. <a href="login.jsp">Login</a>');
	}
});

function loadTemplate(url) {
	var tplString;
	$.ajax({
		url: url,
		method: 'GET',
		async: false,
		success: function(data) { tplString = data; }
	});
	return tplString;
}