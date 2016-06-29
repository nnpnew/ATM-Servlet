import java.io.*;
import java.util.*;
import javax.servlet.*;
import javax.servlet.http.*;
import java.sql.*;

public class TransferServlet extends HttpServlet{

  public PrintWriter out;
  DBManagement dbm = new DBManagement();

  public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

    out = response.getWriter();
    dbm.openConnection();
    HttpSession session = request.getSession();
    ResultSet users = dbm.selectData("users","*","username",(String)session.getAttribute("username"));


    response.setContentType("text/html");
    out.println("<!DOCTYPE html>");
    out.println("<meta charset=\"utf-8\">");
    out.println("<html><head><link type=\"text/css\" rel=\"stylesheet\" href=\"TransStyle.css\"/>");
    out.println("<title></title></head><body>");
    out.println("<div id=\"container\"> sender: "+(String)session.getAttribute("username"));
    out.println("<form action=\"ConfirmTransferServlet\" method=\"GET\">");
    out.println("<select class=\"dropdown\" name=\"user\"> ");


    try {

      while(users.next()){
          String name = users.getString("username").replaceAll("[^A-Za-z0-9]","");
          out.println("<option value=\""+name+"\">"+users.getString("username")+"</option>");


        }
    } catch (SQLException e) {
			e.printStackTrace();

		}

    out.println("<input class=\"textbox\" type=\"text\" name=\"money\"placeholder=\"money\"/>");
    out.println("</select><input class=\"submit\" type=\"submit\" value=\"confirm\"/>");
    out.println(" </form></div></body></html>");


  }


}
