package org.example.db_zlagoda;

import org.example.db_zlagoda.utils.tableview_tools.ClientItem;

import java.sql.*;

public class DatabaseConnection {
    public Connection databaseLink;

    public Connection getConnection(){
        String databaseName = "zlagoda_db",
                databaseUser = "root",
                databasePassword = "qenze22KaNkKa",
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
