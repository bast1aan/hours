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
	doRequest({
		url : "/projects/list?username=" + username,
		type : 'GET',
		success : function(data) {
			if (data.projects) {
					projects.reset(data.projects);
			}
		}
	});
}

function deleteProject(id, view) {
	doRequest({
		url : "/projects/delete?username=" + username,
		type : 'DELETE',
		data : { username : username, project : { id : id }},
		success : function() {
			if (view) {
				view.collection.remove(id);
				view.render();
			}
		}
	});
}

function renameProject(id, name, view) {
	doRequest({
		url : "/projects/update?username=" + username,
		type : 'PUT',
		data : { username : username, project : { id : id, name : name } },
		success : function() {
			if (view) {
				view.collection.get(id).set('name', name);
				view.render();
			}
		}
	});
}

function newProject(name, view) {
	doRequest({
		url : "/projects/save?username=" + username,
		type : 'POST',
		data : { username : username, project : { name : name } },
		success : function(data) {
			if (view && data.project) {
				view.collection.add(data.project);
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
		"click .add" : "addProject",
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
	},
	addProject : function(e) {
		var view = this;
		var el = e.currentTarget;
		var elParent = el.parentNode;
		var input = document.createElement("input");
		input.setAttribute('type', 'text');
		elParent.insertBefore(input, el);
		var addButton = document.createElement("button");
		addButton.appendChild(document.createTextNode('Add project'));
		elParent.insertBefore(addButton, el);
		$(el).hide();

		$(addButton).on('click', newProjectFromInput);
		$(input).on('keydown', function(keyEvent) {
			if (keyEvent.keyCode == 13 /* enter key */) {
				newProjectFromInput();
			}
			if (keyEvent.keyCode == 27 /* esc key */) {
				elParent.removeChild(input);
				elParent.removeChild(addButton);
				$(el).show();
			}
		});
		input.focus();

		function newProjectFromInput() {
			var name = input.value;
			if (!name) {
				alert('No name given. Press ESC in text field to cancel new project.')
			} else {
				newProject(name, view);
				elParent.removeChild(input);
				elParent.removeChild(addButton);
				$(el).show();
			}
		}

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

function doRequest(params) {

	if (baseUrl) {
		params['url'] = baseUrl + params['url'];
	}
	if (!params['type'])
		params['type'] = 'GET';

	params['cache'] = false;
	params['dataType'] = 'json';
	params['crossDomain'] = true;

	if (!!params['data'] && params['type'] != 'GET') {
		params['data'] = JSON.stringify(params['data']);
		params['contentType'] = 'application/json';
	}

	if (!params['error']) {
		params['error'] = function(jqHXR, textStatus, e) {
			var errorMessage = "Error executing request to backend:\nURL: "
				+ params['url'] + "\nMethod: " + params['type'] + "\n\n";

			if (jqHXR.status && jqHXR.statusText)
				errorMessage += "Status: " + jqHXR.status + " " + jqHXR.statusText + "\n";

			if (jqHXR.responseJSON && jqHXR.responseJSON['error'])
				errorMessage += "Message: " + jqHXR.responseJSON['error']+ "\n";

			alert(errorMessage);
		}
	}

	$.ajax(params);
}