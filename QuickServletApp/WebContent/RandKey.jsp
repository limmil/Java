<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Random Key</title>
</head>
<link rel = "stylesheet" type = "text/css" href = "myStyle.css" />
<font color="white">

    <%@page import="java.util.*" %>
	<%
	Random rand = new Random();
	int randomKey = rand.nextInt(10000-1000) + 1000;
	out.println("Todays Attendance Key is " + randomKey);
	%>

</body>
</html>