import javax.servlet.*;
import javax.servlet.http.*;
import java.io.*;
import java.util.*;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import java.util.regex.Pattern;
import java.util.regex.Matcher;

import java.io.PrintWriter;

public class DBManagement {

  public Connection connect;
  public PreparedStatement stmt;
  public Properties prop;
  public InputStream input;

  public PropertiesFile readPropertiesFile(String filePath) {

    PropertiesFile pf = new PropertiesFile();

    try {

      prop = new Properties();
      input = new FileInputStream(filePath);
		  prop.load(input);

      pf.setUrl(prop.getProperty("url"));
      pf.setUsername(prop.getProperty("username"));
      pf.setPassword(prop.getProperty("password"));

  	} catch (IOException ex) {
  		ex.printStackTrace();
  	} finally {
  		if (input != null) {
  			try {
  				input.close();
  			} catch (IOException e) {
  				e.printStackTrace();
  			}
  		}
  	}
    return pf;

  }


  public void openConnection() {

    PropertiesFile props = readPropertiesFile("C:\\apache-tomcat-8.0.35\\webapps\\bank\\WEB-INF\\classes\\db.properties");

    try {

      Class.forName("com.mysql.jdbc.Driver");
      connect = DriverManager.getConnection(props.getUrl(), props.getUsername(), props.getPassword());

      if (connect.isClosed()!=true) {
        System.out.println("\nDatabase connected...");

      }

    } catch (ClassNotFoundException e) {

        System.out.println("ClassNotFound,Please Check your file");

    } catch (SQLException e) {
      e.printStackTrace();

    }

  }


  public void closeConnection() {

    try {
      connect.close();

      if (connect.isClosed()==true) {
        System.out.println("\nDatabase closed...");

      }

    } catch (SQLException e) {
      e.printStackTrace();

    }

  }


  public ResultSet selectAllData(String table) {
		ResultSet result = null;

		try {
			stmt = connect.prepareStatement("SELECT * FROM ?");
      stmt.setString(1,table);
			result = stmt.executeQuery();

		} catch (SQLException e) {
			e.printStackTrace();

		}

		return result;

	}


  public ResultSet selectData(String table,String select,String field,String values) {
    ResultSet result = null;

    try {
      stmt = connect.prepareStatement("SELECT "+select+" FROM "+table+" WHERE "+field+" NOT LIKE ?");
      stmt.setString(1,values);
      result = stmt.executeQuery();

    } catch (SQLException e) {
        System.out.println("\nWarning");

    }

    return result;

  }


  public int authenLogin(String username, String password) {

    ResultSet result = null;

    try {
      stmt = connect.prepareStatement("SELECT * FROM users WHERE username=? AND password=?");
      stmt.setString(1,username);
      stmt.setString(2,password);
      result = stmt.executeQuery();

      if(result.next()){
        return 1;
      } else {
        return 0;
      }

    } catch (SQLException e) {
        System.out.println("\nWarning");
        return 0;
    }

  }


  public String getCustomerIDFromUsername(String username) {

    String customerID = "";

    try {

      stmt = connect.prepareStatement("SELECT * FROM customer WHERE username =?");
      stmt.setString(1,username);
      ResultSet user = stmt.executeQuery();

      while (user.next()){

        customerID = user.getString("customer_id");

        }

    } catch (SQLException e) {
        System.out.println("\nWarning");

    }

    return customerID.replaceAll("[^0-9]","");

  }


  public void editData(String table, String editField, String newValues, String atField, String values) {


		try {
			stmt = connect.prepareStatement("UPDATE "+table+" SET "+editField+"=? WHERE "+atField+"=?");
      stmt.setString(1,newValues);
      stmt.setString(2,values);

			int check = stmt.executeUpdate();

			if (check==1) {
				System.out.println("\n## Edit successful. ##");

			} else {
				System.out.println("\nWarning : Cannot edit this data.");

			}

		}catch (SQLException e) {
				System.out.println("\nPlease try again...");

		}

	}


  public float getMoneyFromCustomerID(String customerID) {

    float money = 0;

    try {
      stmt = connect.prepareStatement("SELECT * FROM account WHERE customer_id =?");
      stmt.setString(1,customerID);
      ResultSet customer = stmt.executeQuery();

      while (customer.next()){

        money = customer.getFloat("money");

        }

    } catch (SQLException e) {
        System.out.println("\nWarning");

    }

    return money;

  }


  public void loanMoney(String borrower, float loanMoney, float loanDay, String date) {

    float bankMoney = 0;
    float interest = 0;

    String borrowerID = getCustomerIDFromUsername(borrower);
    float borrowersMoney = getMoneyFromCustomerID(borrowerID);

    try {
      stmt = connect.prepareStatement("SELECT * FROM bank WHERE bank_id = 1");
      ResultSet brrw = stmt.executeQuery();

      while (brrw.next()){

        bankMoney = brrw.getFloat("bank_money");

        }

    } catch (SQLException e) {
        System.out.println("\nWarning");

    }

    if (bankMoney-loanMoney >= 0) {

      borrowersMoney = borrowersMoney + loanMoney;
      bankMoney = bankMoney - loanMoney;

      interest = (float)(loanMoney+((loanMoney*0.1)*loanDay));

      editData("bank", "bank_money", bankMoney+"", "bank_id", "1");
      editData("account", "money", borrowersMoney+"", "customer_id", borrowerID);

      try {

        stmt = connect.prepareStatement("INSERT INTO loan(loan_money,date,interest,customer_id) VALUES (?, ?, ?, ?)");
        stmt.setFloat(1,loanMoney);
        stmt.setString(2,date);
        stmt.setFloat(3,interest);
        stmt.setString(4,borrowerID);
        stmt.executeUpdate();

  		} catch (SQLException e) {
  				e.printStackTrace();

  		}

      try {

  				stmt = connect.prepareStatement("INSERT INTO statement(date,stm_money,type,customer_id) VALUES (?, ?, 'loan', ?)");
          stmt.setString(1,date);
          stmt.setFloat(2,loanMoney);
          stmt.setString(3,borrowerID);
  			  stmt.executeUpdate();


  		} catch (SQLException e) {
  				e.printStackTrace();

  		}


  } else {

    System.out.println("nahhhhhhhhhhhhhhh");

  }


}


  public void transferMoney(String from, String to, float money, String date) {

    ResultSet sender = null;
    ResultSet receiver = null;


    String senderID = getCustomerIDFromUsername(from);
    String receiverID = getCustomerIDFromUsername(to);

    float moneySender = getMoneyFromCustomerID(senderID);
    float moneyReceiver = getMoneyFromCustomerID(receiverID);

    if (moneySender - money >= 0) {

      moneySender = moneySender - money;
      moneyReceiver = moneyReceiver + money;


      editData("account", "money", moneySender+"", "customer_id", senderID);
      editData("account", "money", moneyReceiver+"", "customer_id", receiverID);


      try {

  				stmt = connect.prepareStatement("INSERT INTO transfer(sender,receiver,trans_money,date) VALUES ( ?, ?, ?, ?)");
          stmt.setString(1,from);
          stmt.setString(2,to);
          stmt.setFloat(3,money);
          stmt.setString(4,date);
  				int check = stmt.executeUpdate();


  		} catch (SQLException e) {
  				e.printStackTrace();

  		}

      try {

  				stmt = connect.prepareStatement("INSERT INTO statement(date,stm_money,type,customer_id) VALUES (?, ?, 'debit', ?)");
          stmt.setString(1,date);
          stmt.setFloat(2,money);
          stmt.setString(3,senderID);
  			  stmt.executeUpdate();


  		} catch (SQLException e) {
  				e.printStackTrace();

  		}

      try {

  				stmt = connect.prepareStatement("INSERT INTO statement(date,stm_money,type,customer_id) VALUES (?, ?, 'credit', ?)");
          stmt.setString(1,date);
          stmt.setFloat(2,money);
          stmt.setString(3,receiverID);
  			  stmt.executeUpdate();


  		} catch (SQLException e) {
  				e.printStackTrace();

  		}
    } else {
      System.out.println("no wayyyyyyyyyyyyyyyyyyyy");
    }

  }


  public ResultSet selectStatemant(String customerID) {

    ResultSet stm = null;

    try {
      stmt = connect.prepareStatement("SELECT * FROM statement WHERE customer_id = ?");
      stmt.setString(1,customerID);
      stm = stmt.executeQuery();


    } catch (SQLException e) {
        System.out.println("\nWarning");

    }
    return stm;

  }

}
