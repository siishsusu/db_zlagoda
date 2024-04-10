package org.example.db_zlagoda.db_data;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.example.db_zlagoda.DatabaseConnection;
import org.example.db_zlagoda.utils.receipt_tools.Receipt;
import org.example.db_zlagoda.utils.tableview_tools.ClientItem;
import org.example.db_zlagoda.utils.tableview_tools.ProductItem;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseManager {
    //TODO: implement actual data selection from database

    public static ObservableList<ProductItem> getProductTableItems() {
        ObservableList<ProductItem> list = FXCollections.observableArrayList(
                new ProductItem("928765287617", "product2", 82, 200),
                new ProductItem("928765287617", "product1", 82, 123),
                new ProductItem("928765287617", "product3", 82, 543),
                new ProductItem("928765287617", "product4", 82, 12),
                new ProductItem("928765287617", "product5", 82, 2),
                new ProductItem("827491826572", "product6", 1, 543),
                new ProductItem("827491826572", "product7", 23, 1235),
                new ProductItem("827491826572", "product8", 76, 43),
                new ProductItem("827491826572", "product9", 1012, 23),
                new ProductItem("827491826572", "product10", 51, 543),
                new ProductItem("729152817298", "product11", 2938, 123),
                new ProductItem("729152817298", "product12", 200, 32),
                new ProductItem("729152817298", "product13", 31, 16),
                new ProductItem("729152817298", "product14", 81, 75),
                new ProductItem("729152817298", "product15", 2003, 3)
        );
        ObservableList<ProductItem> list2 = FXCollections.observableArrayList();
        list2.addAll(list);
        list2.addAll(list);
        list2.addAll(list);
        list2.addAll(list);

        return list2;
    }

    public static ObservableList<ClientItem> getClientTableItems() {
//        ObservableList<ClientItem> list = FXCollections.observableArrayList(
//                new ClientItem("928765287617", "12312", "82", "true", 0.5),
//                new ClientItem("928765287617", "produ123123ct2", "82", "true", 0.5),
//                new ClientItem("928765287617", "asdasd", "82", "true", 0.5),
//                new ClientItem("928765287617", "asdadsda", "82", "true", 0.5),
//                new ClientItem("928765287617", "zczxvc", "82", "true", 0.5),
//                new ClientItem("827491826572", "xcvxcvxcv", "1", "true", 0.5),
//                new ClientItem("827491826572", "kljdfgkldfjg", "23", "true", 0.5),
//                new ClientItem("827491826572", "fjdslkmfskl", "76", "true", 0.5),
//                new ClientItem("827491826572", "sjfklfnsd", "1012", "true", 0.5),
//                new ClientItem("827491826572", "vnxcm,vxn", "51", "true", 0.5),
//                new ClientItem("729152817298", "jfaskljdsak", "2938", "true", 0.5),
//                new ClientItem("729152817298", "asdjklas", "200", "true", 0.5),
//                new ClientItem("729152817298", "vncxm,nvmx,c", "31", "true", 0.5),
//                new ClientItem("729152817298", "l;akd;ls", "81", "true", 0.5),
//                new ClientItem("729152817298", "akjsbdasbd", "2003", "true", 0.5)
//        );
//        ObservableList<ClientItem> list2 = FXCollections.observableArrayList();
//        list2.addAll(list);
//        list2.addAll(list);
//        list2.addAll(list);
//        list2.addAll(list);
//
//        return list2;
        ObservableList<ClientItem> clientItems = FXCollections.observableArrayList();
        try {
            DatabaseConnection connection = new DatabaseConnection();
            Connection connectDB = connection.getConnection();
            Statement statement = connectDB.createStatement();
            ResultSet customers_information = null;

            // 3. Отримати інформацію про усіх постійних клієнтів, відсортованих за прізвищем;
            customers_information = statement.executeQuery(
                    "SELECT * " +
                            "FROM customer_card " +
                            "ORDER BY cust_surname");

            while (customers_information.next()) {
                String id = customers_information.getString("card_number");
                String name = customers_information.getString("cust_surname") + " " + customers_information.getString("cust_name") + " " + customers_information.getString("cust_patronymic");
                String phoneNum = customers_information.getString("phone_number");
                String address = "м. " + customers_information.getString("city") + ", вул. " + customers_information.getString("street") + ", поштовий індекс : " + customers_information.getString("zip_code");
                double percent = customers_information.getDouble("percent");
                clientItems.add(new ClientItem(id, name, phoneNum, address, percent));
            }

            customers_information.close();
            statement.close();
            connectDB.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return clientItems;
    }

    public static void addReceiptToDB(Receipt receipt) throws SQLException {
        DatabaseConnection connection = new DatabaseConnection();
        Connection connectDB = connection.getConnection();
        Statement statement = connectDB.createStatement();
    }
}
