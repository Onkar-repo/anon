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
	/*
	static String allMessages[] = new String[50]; // tempraroy storage before implementing database structure.
	static String allKeys[] = new String[50];
	static String allDates[] = new String[50];
	static int i=-1;
	*/
	static StringBuilder pullFromDatabase(String k, String d)
	{
		
		String dbUrl = "jdbc:mysql://localhost:3306/world";
        String user = "root";
        String pass = "Qwerty@123";
		 String sql = "SELECT dataMessage FROM datalist WHERE dataKey=? AND dataDate=?";
		 String tableHead = "<table border='1' cellpadding='10'><tr><th align='center' valign='top'>Message</th>";
		 String tableTail="</table>";
		 StringBuilder tableMessages = new StringBuilder("");
		 
		 try{
            Class.forName("com.mysql.cj.jdbc.Driver");
            try (Connection con = DriverManager.getConnection(dbUrl, user, pass);
                 PreparedStatement pstmt = con.prepareStatement(sql)) {
					 pstmt.setString(1,k);
					 pstmt.setString(2,d);
					 ResultSet rs = pstmt.executeQuery();
					 tableMessages.append(tableHead);
		
                while (rs.next())
                    tableMessages.append(getHTMLTableRow(rs.getString("dataMessage")));
					
				tableMessages.append(tableTail);
				
            }
			catch (Exception e) {
           System.out.println("Error: " + e.getMessage());
        }
		 }catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
		 }
	return tableMessages;
	}
	static void pushToDatabase(String m, String k, String d)
	{
		String dbUrl = "jdbc:mysql://localhost:3306/world";
        String user = "root";
        String pass = "Qwerty@123";
        String sql = "INSERT INTO datalist (dataMessage, dataKey, dataDate) VALUES (?, ?, ?)";
		
		try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            try (Connection con = DriverManager.getConnection(dbUrl, user, pass);
                 PreparedStatement pstmt = con.prepareStatement(sql)) {
                
              
                pstmt.setString(1, m);
                pstmt.setString(2, k);
                pstmt.setString(3, d);
				
				// Execute the update query
                int rowsInserted = pstmt.executeUpdate();
                
                if (rowsInserted > 0) {
                    System.out.println("message added successfully!");
                } else {
                    System.out.println("failed");
                }
            }
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
		
		
	}
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
	//System.out.println(l);
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
	/*
	static List<String> getSelectedMessages(String k, String d)
	{
		//System.out.println(k + "&" + d + " entering getSelectedMessages");
		List<String> sm = new LinkedList<String>();
		for(int t=0;t<=i;++t)
		{
			if(allKeys[t].equals(k) && allDates[t].equals(d))
				sm.add(allMessages[t]);
		}
		System.out.println(k + "&" + d);
		return sm;
	}
	*/
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
    try (BufferedReader reader = request.getReader()) {
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
		pushToDatabase(message, key, dateISO);
		/*
		allMessages[++i]=message;
		allKeys[i]=key;
		allDates[i]=dateISO;
		*/
		out.println("Message Added!");
		}
		else
		{
			//System.out.println("inside else as a receving mode");
		out.println(pullFromDatabase(key, dateISO));
		//out.println(getFinalMessageTable(getSelectedMessages(key, dateISO)));
		
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
