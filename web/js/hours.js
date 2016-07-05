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
	$.ajax({
		url : "getprojects.action?username=" + username,
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

var ProjectListView = Backbone.View.extend({
	initialize : function() {this.listenTo(this.collection, "reset", this.render);},
	render : function() {
		this.$el.html(_.template(projectListHtml, {projects : this.collection}));
		return this;
	}
});

var listView = new ProjectListView({collection: projects});
$(document).ready(function() {
	$('#projectList').append(listView.$el);
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