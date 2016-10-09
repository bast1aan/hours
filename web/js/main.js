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
 * javascript for the main page
 */

var mainHtml = loadTemplate("js/templates/main.html");
var dialogProjectStartHtml = loadTemplate("js/templates/dialog-project-start.html");

var MainView = Backbone.View.extend({
	initialize: function () {
		this.listenTo(this.collection, "reset", this.render);
	},
	render: function () {
		this.$el.html(_.template(mainHtml, {projects: this.collection}));
		return this;
	},
});

var DialogProjectStartView = Backbone.View.extend({
	render: function () {
		var view = this;
		var $el = this.$el;
		$el.html(_.template(dialogProjectStartHtml, {project: this.model.project, hour: this.model.hour}));
		$el.attr('title', 'Start project');
		$el.ready(function() {
			$el.dialog({
				modal: true,
				buttons: {
					Start: function () {
						$el.find('form').submit();
					},
					Cancel: function () {
						$(this).dialog("close");
						view.remove()
					}
				}
			});
		});
		return this;
	},
	events : {
		'submit #dialog-project-start-form' : 'submit'
	},
	submit : function(event) {
		var $descriptionElement = this.$el.find('form input[name="description"]');
		var $timestampElement = this.$el.find('form input[name="timestamp"]');
		var startDate = parseStrToDate($timestampElement.val());
		if (!startDate) {
			alert('Invalid timestamp given');
			return false;
		}
		this.model.hour.set('description', $descriptionElement.val());
		this.model.hour.set('start', startDate);
		this.model.submit();
		this.$el.dialog("close");
		this.$el.remove();
		return false;

	}

});

$(document).ready(function() {
	var projects = new ProjectsCollection();

	var mainView = new MainView({collection: projects});

	$('#application').append(mainView.$el);

	retrieveProjects(username, mainView);

	mainView.collection.on("reset", function() {
		mainView.collection.each(function(project) {
			getHours(project);
			// TODO should actually be done only once after all projects are rendered
			project.on('change', function() {mainView.render();});
		});
	});
});