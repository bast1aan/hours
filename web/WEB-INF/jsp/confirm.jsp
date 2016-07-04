<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="s" uri="/struts-tags" %>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Confirm</title>
</head>
<body>
<h1>Confirm</h1>
<p>${result}</p>
<p>Enter a new password to continue:</p>
<s:form action="userreset">
	<s:password name="password" label="New password:" />
	<s:password name="passwordconfirm" label="Confirm new password:" />
	<s:submit value="submit" />
</s:form>
</body>
</html>
