<html>
<head>
    <title>Quick Servlet Demo</title>
</head>
<body background="C:\Users\p-chandra\Documents\Workspace\QuickServletApp\WebContent\temp.jpg">
	<font color="white">
	<h1>CSUS Attendance</h1>
	<p>Welcome Professor</p>
	<p>Please enter the Attendance Key for today</p>

   <!-- <a href="/QuickServlet">Click here to send GET request</a>-->
    <br/><br/>
     
    <form action="QuickServlet" method="post">
        KEY <input type="text" size="10" name="Key"/>
        &nbsp;&nbsp;
        <input type="submit" value="Submit" />
    </form>
    </font>
</body>
</html>