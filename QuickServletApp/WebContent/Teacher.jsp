<html>
<head>
<title>Teacher Sign In</title>
</head>
<font color="white">
<body background="C:\Users\p-chandra\Documents\Workspace\QuickServletApp\WebContent\temp.jpg">

<% 
	if (session.getAttribute("user") == null){
		response.sendRedirect("Login.jsp");
	}

%>

<h1>
	<font size="14">CSUS Attendance</font></h1>
	<font size="4">Please Sign In</font>
    <fieldset>
    <p>
    	<form action="QuickServlet" method="post">
        	User Name: <input type="text" size="10" name="userTeacher"/>
        	Password:  <input type="text" size="10" name="passTeacher"/>
        </form>
        </p>
        <form action="${pageContext.request.contextPath}/Course.jsp" method="post">
        	<button type="submit" name="button" value="button1">Submit</button>	
    	</form>
    </fieldset>
	</font>
</body>
</html>