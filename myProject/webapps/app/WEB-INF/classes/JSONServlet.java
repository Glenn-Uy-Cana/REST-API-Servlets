import java.io.*;
import java.sql.*;
import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.annotation.*;
import java.lang.Object;
import org.json.JSONObject;
import org.json.JSONArray;
@WebServlet("/json")   // Configure the request URL for this servlet (Tomcat 7/Servlet 3.0 upwards)
public class JSONServlet extends HttpServlet {
    // The doGet() runs once per HTTP GET request to this servlet.
    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // Set the MIME type for the response message
        response.setContentType("text/html");
        // Get a output writer to write the response message into the network socket
        PrintWriter out = response.getWriter();
        // Print an HTML page as the output of the query
        out.println("<html>");
        out.println("<head><title>Query Response</title></head>");
        out.println("<body>");
        try (
                // Step 1: Allocate a database 'Connection' object
                Connection conn = DriverManager.getConnection(
                        "jdbc:mysql://localhost:3306/ebookshop?allowPublicKeyRetrieval=true&useSSL=false&serverTimezone=UTC",
                        "root", "password");   // For MySQL
                // The format is: "jdbc:mysql://hostname:port/databaseName", "username", "password"
                // Step 2: Allocate a 'Statement' object in the Connection
                Statement stmt = conn.createStatement();
        ) {
            // Step 3: Execute a SQL SELECT query
            String sqlStr = "select * from books where author = "
                    + "'" + request.getParameter("author") + "'"   // Single-quote SQL string
                    + " and qty > 0 order by price desc";
            ResultSet rset = stmt.executeQuery(sqlStr);  // Send the query to the server
            // Step 4: Process the query result set
            while(rset.next()) {
                // Print a paragraph <p>...</p> for each record
               JSONObject jsonObj = new JSONObject();
               JSONArray jsonArray = new JSONArray();
               jsonObj.put("author", rset.getString("author"));
               jsonObj.put("title", rset.getString("title"));
               jsonObj.put("price", rset.getString("price"));
               jsonArray.put(jsonObj);
               out.println(jsonArray);
            }
        } catch(Exception ex) {
            out.println("<p>Error: " + ex.getMessage() + "</p>");
            out.println("<p>Check Tomcat console for details.</p>");
            ex.printStackTrace();
        }  // Step 5: Close conn and stmt - Done automatically by try-with-resources (JDK 7)

        out.close();
    }
}