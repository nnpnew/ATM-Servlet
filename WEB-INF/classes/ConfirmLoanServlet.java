import java.io.*;
import java.util.*;
import javax.servlet.*;
import javax.servlet.http.*;
import java.sql.*;
import java.util.Date;
import java.text.SimpleDateFormat;

public class ConfirmLoanServlet extends HttpServlet{

  public PrintWriter out;
  DBManagement dbm = new DBManagement();

  public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

    dbm.openConnection();
    out = response.getWriter();

    HttpSession session = request.getSession(false);
    session.setAttribute("loanMoney",request.getParameter("money"));
    session.setAttribute("loanDay",request.getParameter("day"));

    Date nowDate = new Date();
    SimpleDateFormat format = new SimpleDateFormat("yyyy/MM/dd hh:mm:ss.SSS");
    session.setAttribute("nowDate",format.format(nowDate));

    out.println("<!DOCTYPE html>");
    out.println("<meta charset=\"utf-8\">");
    out.println("<html><head><link type=\"text/css\" rel=\"stylesheet\" href=\"TransStyle.css\"/>");
    out.println("<title></title></head><body><div id=\"container\">");
    out.println("<form action=\"ConfirmLoanServlet\" method=\"POST\">");
    out.println("<p class=\"text\">From: "+ session.getAttribute("username")+"</p>");
    out.println("<p class=\"text\">Loan money: "+ session.getAttribute("loanMoney")+" baht</p>");
    out.println("<p class=\"text\">Loan day: "+ session.getAttribute("loanDay")+" day</p>");
    out.println("<p class=\"text\" name=\"date\">Date: "+session.getAttribute("nowDate")+"</p>");
    out.println("<input class=\"submit\" type=\"submit\" value=\"confirm\"/>");
    out.println("</form></div></body></html>");

  }

  public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

    //dbm.openConnection();
    HttpSession session = request.getSession();

    float loanMoney = Float.valueOf((String)session.getAttribute("loanMoney"));
    float loanDay = Float.valueOf((String)session.getAttribute("loanDay"));
    String username = (String)session.getAttribute("username");
    String currentDate = (String)session.getAttribute("nowDate");

    dbm.loanMoney(username,loanMoney,loanDay,currentDate);
    RequestDispatcher rd = request.getRequestDispatcher("LoginServlet");
    rd.forward(request, response);

  }
}
