import java.io.*;
import java.util.*;
import javax.servlet.*;
import javax.servlet.http.*;
import java.sql.*;

public class LoanServlet extends HttpServlet{

  public PrintWriter out;
  DBManagement dbm = new DBManagement();

  public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

    out = response.getWriter();
    dbm.openConnection();
    HttpSession session = request.getSession();

    response.setContentType("text/html");
    out.println("<!DOCTYPE html> <html> <head> <link type=\"text/css\" rel=\"stylesheet\" href=\"TransStyle.css\" /> </head>");
    out.println("<body> <div id=\"container\">");
    out.println("<form action=\"ConfirmLoanServlet\" method=\"GET\">");
    out.println("<p class=\"text\">User: "+(String)session.getAttribute("username")+" </p>");
    out.println("<input class=\"textbox\" type=\"text\" name=\"money\"placeholder=\"money\"/>");
    out.println("<input class=\"textbox\" type=\"text\" name=\"day\"placeholder=\"day\"/>");
    out.println("<input class=\"submit\" type=\"submit\" value=\"confirm\"/>");
    out.println("</form></div></body></html>");

  }
}
