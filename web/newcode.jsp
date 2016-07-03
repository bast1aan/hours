<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="s" uri="/struts-tags" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Request new login token</title>
</head>
<body>
<h1>Request new login token</h1>
<p>Enter your email or username to request a new login token.</p>
<p>It wil be send by email to you.</p>
<p>${result}</p>
<s:form action="newcode">
	<s:textfield name="usernameoremail" label="Username or email" />
	<s:submit value="submit" />
</s:form>
</body>
</html>
