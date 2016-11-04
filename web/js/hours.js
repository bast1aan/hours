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

/**
 * General javascript code for this application.
 * Should be loaded before the page-specific javascript
 */


"use strict";

var username;

var baseUrl;

var errorGoToLoginHtml = loadTemplate("js/templates/error-go-to-login.html");
var errorGoToLoginElement;

var Project = Backbone.Model.extend( {
	defaults: {
		id: 0,
		name: '',
		hours: []
	}
});

var ProjectsCollection = Backbone.Collection.extend({
	model : Project,
	getRunning : function() {
		var running = new ProjectsCollection();
		for (var i = 0; i < this.length; ++i) {
			var project = this.at(i);
			var hours = project.get('hours');
			var open = false;
			for (var j = 0; j < hours.length; ++j) {
				var hour = hours.at(j);
				if (hour.get('end') == null) {
					open = true;
					break;
				}
			}
			if (open) {
				running.add(project);
			}
		}
		return running;
	},
	getOther : function() {
		var other = new ProjectsCollection();
		for(var i = 0; i < this.length; ++i) {
			var project = this.at(i);
			var hours = project.get('hours');
			var closed = true;
			for (var j = 0; j < hours.length; ++j) {
				var hour = hours.at(j);
				if (hour.get('end') == null) {
					closed = false;
					break;
				}
			}
			if (closed) {
				other.add(project);
			}
		}
		
		return other;
	},
});

var Hour = Backbone.Model.extend({
	defaults: {
		id: 0,
		description: '',
		projectId: 0,
		start: null,
		end: null
	},
	set : function(key, val, options) {
		if (typeof key === 'object') {
			for (var property in key) {
				if ((property == 'start' || property == 'end') && typeof key[property] == 'string') {
					// TODO check if timestamp is in UTC. It is currently the case with the struts2 json backend.
					key[property] = new Date(key[property] + 'Z');
				}
			}
		} else {
			if ((key == 'start' || key == 'end') && typeof val == 'string') {
				// TODO check if timestamp is in UTC. It is currently the case with the struts2 json backend.
				val = new Date(val + 'Z');
			}
		}
		return Backbone.Model.prototype.set.call(this, key, val, options);
	},
	/* initialize doesn't work properly.
	   we want the start field only be set if it's not set from the construct call

	initialize : function() {
		if (!this.has('start'))
			this.set('start', new Date());
	}
	*/
});

var HoursCollection = Backbone.Collection.extend({model : Hour});

function retrieveProjects(username, view) {
	doRequest({
		url : "/projects/list?username=" + username,
		type : 'GET',
		success : function(data) {
			if (data.projects) {
					view.collection.reset(data.projects);
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

function getHours(project) {
	doRequest({
		url : "/hours/list?username=" + username + "&projectId=" + project.id,
		type : 'GET',
		success : function(data) {
			if (data.hours) {
				project.set({hours : new HoursCollection(data.hours)});
			}
		}
	});
}

function newHour(hour) {
	doRequest({
		url : "/hours/create?username=" + username + "&projectId=" + hour.get('projectId'),
		type : 'POST',
		data : { username : username, hour : hour },
		success : function(data) {
			if (data.hour) {
				hour.set(data.hour);
			}
		}
	});
}

function updateHour(hour, success) {
	doRequest({
		url : "/hours/update?username=" + username + "&projectId=" + hour.get('projectId'),
		type : 'PUT',
		data : { username : username, hour : hour },
		success : function(data) {
			if (data.hour) {
				hour.set(data.hour);
				if (typeof success == 'function')
					success();
			}
		}
	});
}


$(document).ready(function() {
	username = Cookies.get('hours_username');
	baseUrl = Cookies.get('hours_base_url');

	// preload error dialogs into dom
	var errorDiv = document.createElement('div');
	$(errorDiv).html(_.template(errorGoToLoginHtml, {loginUrl : baseUrl + "/login.jsp"}));
	$('#application').append(errorDiv);
	$(errorDiv).ready(function() {
		errorGoToLoginElement = errorDiv.childNodes[0];
		$(errorGoToLoginElement).dialog({
			autoOpen: false,
			modal: true,
			buttons: {
				Ok: function () {
					$(this).dialog("close");
				}
			}
		});
	});

	if (username) {
		$('#loginbar').html('Logged in as: ' + username + ' <a href="' + baseUrl + '/logout.action">Logout</a> | <a href="hours.html">Main</a> | <a href="projects.html">Manage projects…</a> | <a href="overview.action">Overview of spent hours</a>');
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
			if (jqHXR.status && jqHXR.status == 403) {
				$(errorGoToLoginElement).dialog('open');
			} else {

				var errorMessage = "Error executing request to backend:\nURL: "
					+ params['url'] + "\nMethod: " + params['type'] + "\n\n";

				if (jqHXR.status && jqHXR.statusText)
					errorMessage += "Status: " + jqHXR.status + " " + jqHXR.statusText + "\n";

				if (jqHXR.responseJSON && jqHXR.responseJSON['error'])
					errorMessage += "Message: " + jqHXR.responseJSON['error'] + "\n";

				alert(errorMessage);
			}
		}
	}

	$.ajax(params);
}