package org.example.db_zlagoda.db_data;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.example.db_zlagoda.DatabaseConnection;
import org.example.db_zlagoda.cashier_page.Controllers.CashierMenuViewController;
import org.example.db_zlagoda.utils.receipt_tools.Receipt;
import org.example.db_zlagoda.utils.tableview_tools.CategoryItem;
import org.example.db_zlagoda.utils.tableview_tools.ClientItem;
import org.example.db_zlagoda.utils.tableview_tools.ProductInfo;
import org.example.db_zlagoda.utils.tableview_tools.ProductItem;

import javafx.stage.Stage;
import javafx.stage.Modality;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

import java.sql.*;
import java.text.SimpleDateFormat;

public class DatabaseManager {


    public static CategoryItem getCategoryByID(int id) throws SQLException {
        CategoryItem categoryItem = null;
        Statement db = new DatabaseConnection().getConnection().createStatement();
        try{
            ResultSet category_info = executeSelectQuery(db,
                    "SELECT * FROM category " +
                            "WHERE category_number = " + id + ";");
            if(category_info.next()){
                String name = category_info.getString("category_name");
                categoryItem = new CategoryItem(id, name);
            }
            category_info.close();
        }catch (SQLException e) {
            e.printStackTrace();
        }
        return categoryItem;
    }
    public static void showError(String errorMessage) {
        Stage window = new Stage();

        window.initModality(Modality.APPLICATION_MODAL);
        window.setTitle("Error");

        window.setMinWidth(250);

        Label label = new Label();
        label.setText(errorMessage);

        Button closeButton = new Button("Ок");
        closeButton.setOnAction(e -> window.close());

        VBox layout = new VBox(10);
        layout.getChildren().addAll(label, closeButton);
        layout.setAlignment(Pos.CENTER);

        Scene scene = new Scene(layout);
        window.setScene(scene);
        window.showAndWait();
    }

    public static ClientItem getCardById(Connection connection, String id) {
        ClientItem card = null;
        try{
            Statement db = connection.createStatement();
            ResultSet customers_information = executeSelectQuery(db,
                    "SELECT * FROM customer_card " +
                            "WHERE card_number = '" + id + "';");
            if(customers_information.next()){
                String name = customers_information.getString("cust_surname") + " " + customers_information.getString("cust_name") + " " + customers_information.getString("cust_patronymic");
                String phoneNum = customers_information.getString("phone_number");
                String address = "м. " + customers_information.getString("city") + ", вул. " + customers_information.getString("street") + ", поштовий індекс : " + customers_information.getString("zip_code");
                double percent = customers_information.getDouble("percent");
                card = new ClientItem(id, name, phoneNum, address, percent);
            }
            customers_information.close();
        }catch (SQLException e) {
            e.printStackTrace();
        }
        return card;
    }
    public static Receipt getReceiptByID(Connection connection, String id) {
        Receipt receipt = null;
        try {
            Statement db = connection.createStatement();

            ResultSet check_info = executeSelectQuery(db,
                    "SELECT * FROM `check` c WHERE check_number = " + id + ";");

            Date date = null;
            ObservableList<ProductItem> products = FXCollections.observableArrayList();
            String employee = null;
            String card = null;

            if(check_info == null) return null;
            if(check_info.next()) {
                date = check_info.getDate("print_date");
                employee = check_info.getString("id_employee");
                card = check_info.getString("card_number");
            }

            check_info.close();

            ResultSet product_info = executeSelectQuery(db,
                    "SELECT * FROM product p " +
                            "JOIN store_product s ON p.id_product = s.id_product " +
                            "JOIN sale sa ON sa.upc = s.upc " +
                            "JOIN `check` c ON sa.check_number = c.check_number " +
                            "WHERE c.check_number = " + id + ";");

            if(product_info == null) return null;

            while (product_info.next()) {
                String upc = product_info.getString("upc");
                String saleUpc = product_info.getString("upc_prom");
                String name = product_info.getString("product_name");
                //отут в пдфці в store_product products_number, а в sale product_number
                int amount = product_info.getInt("product_number");
                double price = product_info.getDouble("selling_price");
                int category = product_info.getInt("category_number");
                int prom = product_info.getInt("promotional_product");
                products.add(new ProductItem(upc, saleUpc, name, amount, price, category, prom));

            }

            ClientItem cardObj = getCardById(connection, card);

            receipt = new Receipt(date, products, employee, cardObj);

            product_info.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return receipt;
    }

    public static ObservableList<CategoryItem> getCategoryTableItems(Connection connection) {
        ObservableList<CategoryItem> productItems = FXCollections.observableArrayList();
        try {
            Statement db = connection.createStatement();

            ResultSet product_info = db.executeQuery(
                    "SELECT * FROM category " +
                            "ORDER BY category_name");

            while (product_info.next()) {
                int id = product_info.getInt("category_number");
                String name = product_info.getString("category_name");
                productItems.add(new CategoryItem(id, name));
            }

            product_info.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return productItems;
    }


    public static ObservableList<ProductInfo> getAllProductTableItems(Connection connection) {
        ObservableList<ProductInfo> productItems = FXCollections.observableArrayList();
        try {
            Statement db = connection.createStatement();

            ResultSet product_info = db.executeQuery(
                    "SELECT * FROM product p " +
                            "JOIN category sp ON p.category_number = sp.category_number " +
                            "ORDER BY p.product_name");

            while (product_info.next()) {
                int id = product_info.getInt("id_product");
                String name = product_info.getString("product_name");
                String description = product_info.getString("characteristics");
                String category = product_info.getString("category_name");
                productItems.add(new ProductInfo(id, category, name, description));
                System.out.println("name : " + name + " description : " + description);
            }

            product_info.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return productItems;
    }

    public static ObservableList<ProductItem> getProductTableItems(Connection connection) {
        ObservableList<ProductItem> productItems = FXCollections.observableArrayList();
        try {
            Statement db = connection.createStatement();

            ResultSet product_info = db.executeQuery(
                    "SELECT * FROM product p " +
                            "JOIN store_product sp ON p.id_product = sp.id_product " +
                            "ORDER BY p.product_name");

            while (product_info.next()) {
                String id = product_info.getString("upc");
                String saleUpc = product_info.getString("upc_prom");
                String name = product_info.getString("product_name");
                int amount = product_info.getInt("products_number");
                double price = product_info.getDouble("selling_price");
                int category = product_info.getInt("category_number");
                int prom = product_info.getInt("promotional_product");
                productItems.add(new ProductItem(id, saleUpc, name, amount, price, category, prom));
            }

            product_info.close();
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

    public static ObservableList<Receipt> selectReceipts(Connection connection, Date date, String employee) throws SQLException {
        ObservableList<Receipt> receipts = FXCollections.observableArrayList();
        Statement db = connection.createStatement();

        String query = "SELECT * FROM `check` WHERE id_employee = '" + employee + "' AND print_date >= '" + date + "'";
        ResultSet resultSet = executeSelectQuery(db, query);

        while(resultSet != null && resultSet.next()) {
            int id = resultSet.getInt("check_number");
            Date printDate = resultSet.getDate("print_date");
            double sum = resultSet.getDouble("sum_total");
            double vat = resultSet.getDouble("vat");
            String card = resultSet.getString("card_number");
            ClientItem cardObj = getCardById(connection, card);
            receipts.add(new Receipt(id, employee, cardObj, sum, vat, printDate));
        }
        if(resultSet != null) resultSet.close();
        return receipts;
    }

    public static void addReceiptToDB(Receipt receipt) throws SQLException {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");

        String card = null;
        if(receipt.getCard() != null) card = "'" + receipt.getCard().getCard_id() + "'";

        String insertQuery = "INSERT INTO `check` (id_employee, card_number, print_date, sum_total, vat)" +
                " VALUES ('"
                + receipt.getEmployee() + "', "
                + card + ", '"
                + formatter.format(receipt.getDate()) + "', "
                + receipt.getSum() + ", "
                + receipt.getVat()
                + ");";
        System.out.println(insertQuery);
        DatabaseManager.executeUpdateQuery(CashierMenuViewController.database.createStatement(), insertQuery);
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
            if (!executeUpdateQuery(CashierMenuViewController.database.createStatement(), query)) throw new SQLException("Could not add sold products to the database");
        }
    }

    public static int getCheckID(Receipt receipt) throws SQLException {
        String query = "SELECT check_number " +
                "FROM `check` " +
                "WHERE id_employee = '" + receipt.getEmployee() + "' " +
                "AND print_date = '" + receipt.getDate() + "' " +
                "AND sum_total = '" + receipt.getSum() + "'";
        ResultSet set = executeSelectQuery(CashierMenuViewController.database.createStatement(), query);
        if(set != null && set.next()) {
            return set.getInt("check_number");
        } else {
            throw new SQLException("No check number found");
        }
    }

    public static void removeReceiptProducts(ObservableList<ProductItem> items) throws SQLException {

        for(ProductItem item : items) {
            ResultSet dbItem = DatabaseManager.executeSelectQuery(CashierMenuViewController.database.createStatement(),
                    "SELECT * FROM store_product WHERE upc = '"+item.getUpc()+"'");


            if(dbItem == null || !dbItem.next()) throw new SQLException("No item with UPC "+item.getUpc());

            int amount = dbItem.getInt("products_number") - item.getAmount();
            if(amount < 0) throw new SQLException("Product "+item.getUpc()+" is out of stock");

            if(!executeUpdateQuery(CashierMenuViewController.database.createStatement(),
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
