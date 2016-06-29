import java.io.*;
import java.util.*;
import javax.servlet.*;
import javax.servlet.http.*;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Statement extends HttpServlet {

  DBManagement dbm = new DBManagement();
  public PrintWriter out;

  public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

    dbm.openConnection();

    HttpSession session = request.getSession();

    String customerID = dbm.getCustomerIDFromUsername((String)session.getAttribute("username"));
    ResultSet result = dbm.selectStatemant(customerID);

    out = response.getWriter();
    response.setContentType("text/html");

    out.println("<table>");

    try {

        int i = 1;

        out.println("<tr bgcolor =\"#ccffcc\">");
        out.println("<td align=\"center\" width=\"5%\"> NO </td>");
        out.println("<td align=\"center\" width=\"25%\"> Date </td>");
        out.println("<td align=\"center\" width=\"25%\"> Amount </td>");
        out.println("<td align=\"center\" width=\"20%\"> Type </td>");
        out.println("</tr>");

        while (result.next()) {
          out.println("<tr>");
          out.println("<td>" + i+ "</td>");
          out.println("<td>" + result.getString("date")+ "</td>");
          out.println("<td>" + result.getFloat("stm_money")+ "    Baht"+ "</td>");
          out.println("<td>" + result.getString("type") + "</td>");
          out.println("</tr>");
          i++;

        }

        out.println("</table>");

        if (i==1) {
          out.println("\nThere is no data.");

        }// else {
        //   out.println("\nResult: "+i+" Books");
        //
        // }

      } catch (SQLException e) {
        e.printStackTrace();

      }


  }
}
