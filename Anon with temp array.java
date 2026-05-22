import java.io.*;
import jakarta.servlet.*; 
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.WebServlet;
import java.time.LocalDate;
import java.util.*;
import java.time.format.DateTimeFormatter;
import java.time.format.ResolverStyle;
import java.sql.*;

@WebServlet("/home")
public class Anon extends HttpServlet {
	
	static String allMessages[] = new String[50]; // tempraroy storage before implementing database structure.
	static String allKeys[] = new String[50];
	static String allDates[] = new String[50];
	static int i=-1;
	
	static boolean isDate(String d)
	{
		final DateTimeFormatter ISO_DATE_FORMATTER = 
        DateTimeFormatter.ISO_LOCAL_DATE.withResolverStyle(ResolverStyle.STRICT);
		if (d == null || d.length() != 10) {
            return false;
        }
        try {
            LocalDate.parse(d, ISO_DATE_FORMATTER);
            return true;
        } catch (Exception e) {
            return false;
        }
		
	}
	static StringBuilder getFinalMessageTable(List<String> l)
	{
	System.out.println(l);
		String tableHead = "<table border='1' cellpadding='10'><tr><th align='center' valign='top'>Message</th>";
		StringBuilder tableMessages = new StringBuilder("");
		String tableTail="</table>";
		tableMessages.append(tableHead);
		
	for(String data:l)
		tableMessages.append(getHTMLTableRow(data));
	
	tableMessages.append(tableTail);
	System.out.println(tableMessages);
	return tableMessages;	
	}
	
	static List<String> getSelectedMessages(String k, String d)
	{
		System.out.println(k + "&" + d + " entering getSelectedMessages");
		List<String> sm = new LinkedList<String>();
		for(int t=0;t<=i;++t)
		{
			if(allKeys[t].equals(k) && allDates[t].equals(d))
				sm.add(allMessages[t]);
		}
		System.out.println(k + "&" + d);
		return sm;
	}
	
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
	String message, key,dateISO;
	System.out.println("enterd doPost");
    try (BufferedReader reader = request.getReader()) {
		System.out.println("started reading post request data.");
        message = reader.readLine();
        key = reader.readLine();
		dateISO = reader.readLine();
    }
		response.setContentType("text/html");
		response.setHeader("Access-Control-Allow-Origin", "*");
		PrintWriter out = response.getWriter();	
		
		System.out.println(message);
		if(message.equals("") || key.equals("") || !isDate(dateISO))
		{
			out.println("Incorrect input format.");
			return;
		}
		if(!message.equals("F"))
		{		
		allMessages[++i]=message;
		allKeys[i]=key;
		allDates[i]=dateISO;
		out.println("Message Added!");
		}
		else
		{
		System.out.println("inside else as a receving mode");
		//out.println(pullFromDatabase(key, dateISO));
		out.println(getFinalMessageTable(getSelectedMessages(key, dateISO)));
		
		}
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
