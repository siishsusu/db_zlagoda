package org.example.db_zlagoda.db_data;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.example.db_zlagoda.DatabaseConnection;
import org.example.db_zlagoda.cashier_page.Controllers.CashierMenuViewController;
import org.example.db_zlagoda.utils.receipt_tools.Receipt;
import org.example.db_zlagoda.utils.tableview_tools.ClientItem;
import org.example.db_zlagoda.utils.tableview_tools.ProductItem;

import java.sql.*;
import java.text.SimpleDateFormat;

public class DatabaseManager {

    public static ObservableList<ProductItem> getProductTableItems(Statement db) {
        ObservableList<ProductItem> productItems = FXCollections.observableArrayList();
        try {

            ResultSet customers_information = db.executeQuery(
                    "SELECT * FROM product p " +
                            "JOIN store_product sp ON p.id_product = sp.id_product");

            while (customers_information.next()) {
                String id = customers_information.getString("upc");
                String name = customers_information.getString("product_name");
                int amount = customers_information.getInt("products_number");
                double price = customers_information.getDouble("selling_price");
                productItems.add(new ProductItem(id, name, amount, price));
            }

            customers_information.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return productItems;
    }

    public static ObservableList<ClientItem> getClientTableItems() {

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

    public static ObservableList<Receipt> selectReceipts(Statement db, Date date, String employee) throws SQLException {
        ObservableList<Receipt> receipts = FXCollections.observableArrayList();
        String query = "SELECT * FROM `check` WHERE id_employee = '" + employee + "' AND print_date >= '" + date + "'";
        ResultSet resultSet = executeSelectQuery(db, query);

        while(resultSet != null && resultSet.next()) {
            int id = resultSet.getInt("check_number");
            Date printDate = resultSet.getDate("print_date");
            double sum = resultSet.getDouble("sum_total");
            double vat = resultSet.getDouble("vat");
            String card = resultSet.getString("card_number");
            receipts.add(new Receipt(id, employee, card, sum, vat, date));
        }
        return receipts;
    }

    public static void addReceiptToDB(Receipt receipt) throws SQLException {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");

        String card = receipt.getCard();
        if(card != null) card = "'" + card + "'";

        String insertQuery = "INSERT INTO `check` (id_employee, card_number, print_date, sum_total, vat)" +
                " VALUES ('"
                + receipt.getEmployee() + "', "
                + card + ", '"
                + formatter.format(receipt.getDate()) + "', "
                + receipt.getSum() + ", "
                + receipt.getVat()
                + ");";
        System.out.println(insertQuery);
        DatabaseManager.executeUpdateQuery(CashierMenuViewController.database, insertQuery);
        addSoldProductsToDB(receipt);
    }

    public static void addSoldProductsToDB(Receipt receipt) throws SQLException {
        int id = getCheckID(receipt);
        for (ProductItem productItem : receipt.getProducts()) {
            double price = productItem.getPrice()/productItem.getAmount();
            String query = "INSERT INTO `sale` (upc, check_number, product_number, selling_price)" +
                    " VALUES ('"
                    + productItem.getUpc() + "', "
                    + id + ", "
                    + productItem.getAmount() + ", "
                    + price + ");";
            if (!executeUpdateQuery(CashierMenuViewController.database, query)) throw new SQLException("Could not add sold products to the database");
        }
    }

    public static int getCheckID(Receipt receipt) throws SQLException {
        String query = "SELECT check_number " +
                "FROM `check` " +
                "WHERE id_employee = '" + receipt.getEmployee() + "' " +
                "AND print_date = '" + receipt.getDate() + "' " +
                "AND sum_total = '" + receipt.getSum() + "'";
        ResultSet set = executeSelectQuery(CashierMenuViewController.database, query);
        if(set != null && set.next()) {
            return set.getInt("check_number");
        } else {
            throw new SQLException("No check number found");
        }
    }

    public static void removeReceiptProducts(ObservableList<ProductItem> items) throws SQLException {

        for(ProductItem item : items) {
            ResultSet dbItem = DatabaseManager.executeSelectQuery(CashierMenuViewController.database,
                    "SELECT * FROM store_product WHERE upc = '"+item.getUpc()+"'");


            if(dbItem == null || !dbItem.next()) throw new SQLException("No item with UPC "+item.getUpc());

            int amount = dbItem.getInt("products_number") - item.getAmount();
            if(amount < 0) throw new SQLException("Product "+item.getUpc()+" is out of stock");

            if(!executeUpdateQuery(CashierMenuViewController.database,
                    "UPDATE store_product t " +
                    "SET t.products_number = " + amount + " " +
                    "WHERE t.upc = '" + dbItem.getString("upc") + "'"))
                throw new SQLException("Failed to update product");
        }
    }

    public static ResultSet executeSelectQuery(Statement db, String query){
        try {
            return db.executeQuery(query);
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static boolean executeUpdateQuery(Statement db, String query){
        try {
            db.executeUpdate(query);
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }


}
