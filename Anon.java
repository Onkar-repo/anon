import java.io.*;
import jakarta.servlet.*; // Use javax.servlet for Tomcat 9 and earlier
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.WebServlet;
@WebServlet("/home")
public class Anon extends HttpServlet {

	static String getHTMLTableRow(String sb)
	{
		return "</tr><tr><td align='center' valign='top'>"+ sb +"</td></tr>";
		
	}
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
				
				response.setContentType("text/html");
		response.addHeader("Access-Control-Allow-Origin", "*");
				System.out.println(request.getRequestURI());
String path = "/index.html";

if (request.getRequestDispatcher(path) == null) {
    response.getWriter().println("Error: File not found at " + path);
} else {
    request.getRequestDispatcher(path).forward(request, response);
}

	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {			
	String message, key;
    try (BufferedReader reader = request.getReader()) {
        message = reader.readLine();
        key = reader.readLine();   
    }
		
		
		String tableHead = "<table border='1' cellpadding='10'><tr><th align='center' valign='top'>Message</th>";
		//String tableTail="</table>";
		
		response.setContentType("text/html");
		response.setHeader("Access-Control-Allow-Origin", "*");
		PrintWriter out = response.getWriter();	
		out.println(tableHead+getHTMLTableRow(message));
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	protected void doOptions(HttpServletRequest req, HttpServletResponse resp) 
            throws ServletException, IOException {
        // Set CORS headers to allow browser preflight requests
        resp.setHeader("Access-Control-Allow-Origin", "*");
        resp.setHeader("Access-Control-Allow-Methods", "GET, POST, OPTIONS");
        resp.setHeader("Access-Control-Allow-Headers", "Access-Control-Allow-Origin, Content-Type, Authorization");
        
        // Max Age tells the browser to cache this OPTIONS response for 24 hours
        resp.setHeader("Access-Control-Max-Age", "86400");
        
        // Standard "Allow" header listing supported methods
        resp.setHeader("Allow", "GET, POST, OPTIONS");
        
        // Ensure the response status is 200 (OK) or 204 (No Content)
        resp.setStatus(HttpServletResponse.SC_OK);
    }
}
