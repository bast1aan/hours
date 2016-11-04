<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="s" uri="/struts-tags" %>
<!DOCTYPE html>
<html>
<head>
	<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
	<title>Hours</title>
	<link href="css/hours.css" rel="stylesheet" />
</head>
<body>
	<div id="loginbar">Logged in as: ${user.username} <a href="logout.action">Logout</a></div>
	<div id="application"></div>
	<script src="js/jquery.js"></script>
	<script src="js/underscore.js"></script>
	<script src="js/backbone.js"></script>
	<script src="js/hours.js"></script>
	<script type="text/javascript">
		var secure = '';
		if (document.location.href.substr(0, 5) == 'https')
			secure = '; secure';
		document.cookie = 'hours_username=${user.username}; path=/' + secure;
		document.cookie = 'hours_fullname=${user.fullname}; path=/' + secure;
		document.cookie = 'hours_base_url=${baseUrl}; path=/' + secure;
		location.href = 'hours.html';
	</script>
</body>
</html>
