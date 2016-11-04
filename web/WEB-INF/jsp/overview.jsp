<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="s" uri="/struts-tags" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Overview</title>
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
<% java.text.SimpleDateFormat df = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss"); %>
<h2>${project.name}</h2>
<table>
	<thead>
		<tr><th>Start</th><th>End</th><th>Description</th></tr>
	</thead>
	<tbody>
<s:iterator var="hour" value="hours">
<s:set var="hour" value="%{hour}" />
<jsp:useBean id="hour" class="bast1aan.hours.Hour" />
<tr>
	<td><%= hour.start != null ? df.format(hour.start) : "" %></td>
	<td><%= hour.end != null ? df.format(hour.end) : "" %></td>
	<td><%= hour.description%></td>
</tr>
</s:iterator>
	</tbody>
</table>
</s:if>
</body>
</html>
