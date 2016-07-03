<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="s" uri="/struts-tags" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Login</title>
</head>
<body>
<h1>Login</h1>
<p>${result}</p>
<s:form action="login">
	<s:textfield name="username" label="Username" />
	<s:password name="password" label="Password" />
	<s:submit value="submit" />
</s:form>
<p>Can't log on? <a href="newcode.jsp">Request a new access code</a></p>
</body>
</html>
