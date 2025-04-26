package DoorStepServiceProject.DoorStepServiceProject.vmm;

import java.sql.*;
public class DBLoader {
   public static ResultSet executeQuery( String stm)
   {
       try
       {
           //1ST STEP:-LOADING JDBC DRIVERS
             Class.forName("com.mysql.cj.jdbc.Driver");      
                   System.out.println("Driver loaded successfully!!");

            //2ND STEP:-create connection to the mysql databases
                   Connection conn = DriverManager.getConnection("jdbc:mysql://127.0.0.1:3306/doorstepservices", "root", "system");
                   System.out.println("Connection build");

            //3RD STEP:-CONNECT THE STATEMENT WITH THE CONNECTION STRING
                   Statement stmt = conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
                   System.out.println("Statement created");

            //4TH STEP:- EXECUTE THE QUERY
           
                  ResultSet rs = stmt.executeQuery(stm);
                  return rs;
       }
       catch(Exception exp)
       {
           exp.printStackTrace();
       }
       return null;
   }

    public static int executeUpdate(String stm)
   {
       try
       {
           //1ST STEP:-LOADING JDBC DRIVERS
             Class.forName("com.mysql.cj.jdbc.Driver");      
                   System.out.println("Driver loaded successfully!!");

            //2ND STEP:-create connection to the mysql databases
                   Connection conn = DriverManager.getConnection("jdbc:mysql://127.0.0.1:3306/doorstepservices", "root", "system");
                   System.out.println("Connection build");

            //3RD STEP:-CONNECT THE STATEMENT WITH THE CONNECTION STRING
                   Statement stmt = conn.createStatement();
                   System.out.println("Statement created");

            //4TH STEP:- EXECUTE THE QUERY
           
                  int result = stmt.executeUpdate(stm);
                  conn.close();
                  return result;
       }
       catch(Exception exp)
       {
           exp.printStackTrace();
       }
       return 0;
   }

   public static Connection getConnection() {
    try {
        Class.forName("com.mysql.cj.jdbc.Driver");
        Connection conn = DriverManager.getConnection(
            "jdbc:mysql://127.0.0.1:3306/doorstepservices", "root", "system"
        );
        return conn;
    } catch (Exception e) {
        e.printStackTrace();
        return null;
    }
}
    
}
