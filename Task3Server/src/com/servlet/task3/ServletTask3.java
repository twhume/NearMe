package com.servlet.task3;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;


/**
 * Servlet implementation class ServletTask3
 */
public class ServletTask3 extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private ServletContext context;

	
	
	public void init(ServletConfig config) throws ServletException {
		  super.init(config);
		  context = getServletContext();
		  context.log("Data From Android Application"); 
		  }

    
    /**
     * @see HttpServlet#HttpServlet()
     */
    public ServletTask3() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		response.setContentType("text/html"); //to specify the HTTP response line and headers

		
	    PrintWriter out = response.getWriter(); // to send content to browser

	    String title = "Data From Android Application";
	    //how display the content 
	    out.println(ServletUtilities.headWithTitle(title) +
	                "<BODY>\n" +
	                "<H1 ALIGN=CENTER>" + title + "</H1>\n" +
	                "<UL>\n" +
	                "  <LI>Latitude: "
	                + request.getParameter("latitude") + "\n" +
	                "  <LI>Longitude: "
	                + request.getParameter("longitude") + "\n" +
	                "  <LI>Time: "
	                + request.getParameter("time") + "\n" +
	                "  <LI>Id: "
	                + request.getParameter("id") + "\n" +
	                "</UL>\n" +
	                "</BODY></HTML>");	
	    
	    String strHTTPParams;

	  //construct a string made from the params of the request object....
	  strHTTPParams = "Latitude: " + request.getParameter("latitude")

	  + ". Longitude: " + request.getParameter("longitude")

	  + ". Time: " + request.getParameter("time")

	  + ". ID: " + request.getParameter("id") + "\n";

	
	  context.log(strHTTPParams); // save the data
	  
	  
	  out.println("<ul>The data has been written into Log in ServletContext</ul>");

		  
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
	  doGet(request,response);
	}

}
