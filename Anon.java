import java.io.*;
import jakarta.servlet.*; 
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.WebServlet;
import java.time.LocalDate;
import java.util.*;
import java.time.format.DateTimeFormatter;
import java.time.format.ResolverStyle;
import java.sql.*;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;


	/*
	  //Hosting Server MariaDB Database Details
	  //String dbUrl = "jdbc:mariadb://ogkapps.info:3306/ogkapp9_world";
      //String user = "ogkapp9_root";
	  //String pass = "Hn5aXU+AG4JCk(w~";
		*/
	

@WebServlet("/home")
public class Anon extends HttpServlet {
	private static HikariDataSource dataSource ;
	
	@Override
    public void init() throws ServletException{
	HikariConfig config = new HikariConfig();
        /*
		config.setJdbcUrl("jdbc:mariadb://localhost:3306/world");
        config.setUsername("root");
        config.setPassword("Qwerty@123");
        config.setDriverClassName("org.mariadb.jdbc.Driver");
		*/
		config.setJdbcUrl("jdbc:mariadb://localhost:3306/ogkapp9_world");
        config.setUsername("ogkapp9_root");
        config.setPassword("Hn5aXU+AG4JCk(w~");  // temporarily hardcoded password just for testing

        config.setDriverClassName("org.mariadb.jdbc.Driver");

        // Set pool sizing properties
        config.setMaximumPoolSize(20);
        config.setMinimumIdle(5);
        config.setIdleTimeout(300000); // 5 minutes
        config.setConnectionTimeout(30000); // 30 seconds

        // Initialize the pool
		dataSource = new HikariDataSource(config); 
        //config.copyStateTo(dataSource);
		// forces hikari to lock configuration immediately
		//try{dataSource.getLoginTimeout();}
		//catch(Exception e)
		//{System.out.println("Error: " + e.getMessage());}
		
	}
	static StringBuilder pullFromDatabase(String k, String d)
	{
	
		String sql = "SELECT dataMessage FROM datalist WHERE dataKey=? AND dataDate=?";
		 String tableHead = "<table border='1' cellpadding='10'><tr><th align='center' valign='top'>Message</th>";
		 String tableTail="</table>";
		 StringBuilder tableMessages = new StringBuilder("");
		 tableMessages.append(tableHead);
		 try{
         
            try (Connection con = dataSource.getConnection();
                 PreparedStatement pstmt = con.prepareStatement(sql)) {
					 
					 pstmt.setString(1,k);
				 pstmt.setString(2,d);
				 				 
		
		
		try(ResultSet rs = pstmt.executeQuery()){
		
                while (rs.next())
                    tableMessages.append(getHTMLTableRow(rs.getString("dataMessage")));
					
				tableMessages.append(tableTail);
		}
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

        String sql = "INSERT INTO datalist (dataMessage, dataKey, dataDate) VALUES (?, ?, ?)";
		
		try {
          	
            try (Connection con = dataSource.getConnection();
                 PreparedStatement pstmt = con.prepareStatement(sql)) {
                
              
                pstmt.setString(1, m);
                pstmt.setString(2, k);
                pstmt.setString(3, d);
				
				// Execute the update query
                int rowsInserted = pstmt.executeUpdate();
                if (rowsInserted > 0) System.out.println("message added successfully!");
                else System.out.println("failed");
				
				pstmt.close();
            }
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
			e.printStackTrace();
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
		pushToDatabase(message, key, dateISO);
	
		out.println("Message Added!");
		}
		else
		{
			System.out.println("inside else as a receving mode");
		out.println(pullFromDatabase(key, dateISO));

		
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
