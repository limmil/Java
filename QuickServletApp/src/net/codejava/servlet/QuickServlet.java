package net.codejava.servlet;
 
import java.io.IOException;
import java.util.Random;
import java.io.PrintWriter;
import SheetPackageTest.SheetsQuickstart;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;


public class QuickServlet extends HttpServlet {
    /**
     * this life-cycle method is invoked when this servlet is first accessed
     * by the client
     */

    public void init(ServletConfig config) {
        System.out.println("Servlet is being initialized");
    }
 
    /**
     * handles HTTP GET request
     */
    public void doGet(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        
    	//PrintWriter writer = response.getWriter();
        //SheetsQuickstart.updateSheet();
        
        //writer.println("<html>Hello, I am a Java servlet!</html>");
        //writer.flush();
    }
 
    /**
     * handles HTTP POST request
     * @throws ServletException 
     */
    public void doPost(HttpServletRequest request, HttpServletResponse response)
            throws IOException, ServletException, NullPointerException {
    	
    	//action listener======================================================
    	
    	String action = request.getParameter("action");
    	switch (action) {
	    	case "studentlogin":
	    		
	    		break;
	    	case "teacherlogin":
	        	String uname = request.getParameter("uname");
	        	String pword = request.getParameter("pword");
	        	
	        	if(uname.equals("teacher") && pword.equals("123")) {
	        		
	        		HttpSession session = request.getSession();
	        		session.setAttribute("user", uname);
	        		response.sendRedirect("Teacher.jsp");
	        	}
	        	else
	        	{
	        		response.sendRedirect("Login.jsp");
	        	}
	    		break;
	    	default:
			    String paramKey = request.getParameter("Student ID");
		        int ID = Integer.parseInt(paramKey);
		        String paramID = request.getParameter("Key");
		        String Key = paramID;
		        
		        String userTeacher = request.getParameter("userTeacher");
		        String username= userTeacher;
		        String passTeacher = request.getParameter("passTeacher");
		        String password=passTeacher;
		 
		        String keyMain = SheetsQuickstart.getKey();
		        String hash = SheetsQuickstart.makeHash256("password");
		        
	    		break;
    	}
    	
    	//end of action listener===============================================
        
    }
 
    /**
     * this life-cycle method is invoked when the application or the server
     * is shutting down
     */
    public void destroy() {
        System.out.println("Servlet is being destroyed");
    }
}