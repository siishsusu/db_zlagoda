package org.example.db_zlagoda;

import org.example.db_zlagoda.utils.tableview_tools.ClientItem;

import java.sql.*;

public class DatabaseConnection {
    public static Connection databaseLink;

    public static Connection getConnection(){
        String databaseName = "zzlagoda_db",
                databaseUser = "root",
                databasePassword = "Yellow768215!",
                link = "jdbc:mysql://localhost/" + databaseName;

        try {
            Class.forName("com.mysql.cj.jdbc.Driver").newInstance();
            databaseLink = DriverManager.getConnection(link, databaseUser, databasePassword);
        }catch (Exception error){
            error.printStackTrace();
            error.getCause();
        }
        return databaseLink;
    }

}
