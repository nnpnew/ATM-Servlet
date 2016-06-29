import java.io.*;
import java.util.*;
import javax.servlet.*;
import javax.servlet.http.*;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ConfirmTransferServlet extends HttpServlet{

  public PrintWriter out;
  DBManagement dbm = new DBManagement();

  public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

    dbm.openConnection();

    out = response.getWriter();
    response.setContentType("text/html");

    HttpSession session = request.getSession();
    session.setAttribute("receiver",request.getParameter("user"));
    session.setAttribute("money",request.getParameter("money"));

    Date date = new Date();
    SimpleDateFormat format = new SimpleDateFormat("yyyy/MM/dd hh:mm:ss.SSS");
    session.setAttribute("currentDate",format.format(date));

    out.println("<!DOCTYPE html>");
    out.println("<meta charset=\"utf-8\">");
    out.println("<html><head><link type=\"text/css\" rel=\"stylesheet\" href=\"TransStyle.css\"/>");
    out.println("<title></title></head><body><div id=\"container\">");
    out.println("<form action=\"ConfirmTransferServlet\" method=\"POST\">");
    out.println("<p class=\"text\">From: "+session.getAttribute("username")+"</p>");
    out.println("<p class=\"text\">To: "+session.getAttribute("receiver")+"</p>");
    out.println("<p class=\"text\">Money: "+request.getParameter("money")+" baht</p>");
    out.println("<p class=\"text\" name=\"date\">Date: "+session.getAttribute("currentDate")+"</p>");
    out.println("<input class=\"submit\" type=\"submit\" value=\"confirm\"/>");
    out.println("</form></div></body></html>");

  }

  public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

    HttpSession session = request.getSession();

    float money = Float.valueOf((String)session.getAttribute("money"));
    String username = (String)session.getAttribute("username");
    String receiver = (String)session.getAttribute("receiver");
    String date = (String)session.getAttribute("currentDate");

    dbm.openConnection();
    dbm.transferMoney(username,receiver,money,date);
    RequestDispatcher rd = request.getRequestDispatcher("LoginServlet");
    rd.forward(request, response);

   }

}
