<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="s" uri="/struts-tags" %>
<jsp:useBean id="view" class="bast1aan.hours.action.OverviewAction$View" />
<!DOCTYPE html>
<html>
<head>
	<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
	<title>Hours - Overview</title>
	<link href="css/hours.css" rel="stylesheet" />
</head>
<body>
<h1>Overview</h1>
<h2>Projects</h2>
<ul>
<s:iterator var="prj" value="projects">
<li><a href="overview.action?project_id=${prj.id}">${prj.name}</a></li>
</s:iterator>
</ul>
<s:if test="%{project != null}">
<h2>${project.name}</h2>
<table class="overview">
	<thead>
		<tr><th>Start</th><th>End</th><th>Description</th><th>Time spent</th></tr>
	</thead>
	<tbody>
<% long timeSpent = 0L; %>
<s:iterator var="hour" value="hours">
<s:set var="hour" value="%{hour}" />
<jsp:useBean id="hour" class="bast1aan.hours.Hour" />
<% timeSpent += view.timeDiffToLong(hour.start, hour.end); %>
<tr>
	<td><%= view.displayDate(hour.start) %></td>
	<td><%= view.displayDate(hour.end) %></td>
	<td><%= hour.description%></td>
	<td class="spent"><%= view.displayTimeDiff(hour.start, hour.end) %></td>
</tr>
</s:iterator>
	<tr><th colspan="3">Total time spent</th><th class="spent"><%= view.displayTimeDiff(timeSpent) %></th></tr>
	</tbody>
</table>
</s:if>
</body>
</html>
