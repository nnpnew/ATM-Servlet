import java.io.*;
import java.util.*;
import javax.servlet.*;
import javax.servlet.http.*;
import java.sql.*;

public class LoginServlet extends HttpServlet {

  DBManagement dbm = new DBManagement();
  public PrintWriter out;

  public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

    int check=0;

    dbm.openConnection();
    check = dbm.authenLogin(request.getParameter("username"),request.getParameter("password"));
    out = response.getWriter();

    if (check==1) {

      HttpSession session = request.getSession();
      session.setAttribute("username",request.getParameter("username"));

      response.setContentType("text/html");
      out.println("<!DOCTYPE html>");
      out.println("<meta charset=\"utf-8\">");
      out.println("<html><head>");
      out.println("<link type=\"text/css\" rel=\"stylesheet\" href=\"MainStyle.css\" />");
      out.println("<title> </title></head> <body><div id=\"container\">");
      out.println("<form action=\"LoanServlet\" method=\"GET\">");
      out.println("<input class=\"submit\" type=\"submit\" value=\"Loan\"/></form> ");
      out.println("<form action=\"TransferServlet\" method=\"GET\">");
      out.println("<input class=\"submit\" type=\"submit\" value=\"Transfer\"/></form> ");
      out.println("<form action=\"Statement\" method=\"GET\">");
      out.println("<input class=\"submit\" type=\"submit\" value=\"Statement\"/></form> ");
      out.println(" </div> </body></html>");


    } else {

      RequestDispatcher rd = request.getRequestDispatcher("Login.html");
      rd.forward(request, response);

    }

  }
}
