package org.example.db_zlagoda.db_data;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.example.db_zlagoda.DatabaseConnection;
import org.example.db_zlagoda.cashier_page.Controllers.CashierMenuViewController;
import org.example.db_zlagoda.utils.SaleFilter;
import org.example.db_zlagoda.utils.receipt_tools.Receipt;
import org.example.db_zlagoda.utils.tableview_tools.*;

import javafx.stage.Stage;
import javafx.stage.Modality;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.Objects;

import static org.example.db_zlagoda.db_data.DatabaseManager_mm.getCustomersInformation;
import static org.example.db_zlagoda.db_data.DatabaseManager_mm.getCustomersInformation_byPercent;

public class DatabaseManager {

    public static void showSQLError(String errorMessage) {
        Stage window = new Stage();

        window.initModality(Modality.APPLICATION_MODAL);
        window.setTitle("Database Error");

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

    public static CategoryItem getCategoryByID(Connection db, int id) throws SQLException {
        CategoryItem categoryItem = null;
        try{
            PreparedStatement preparedStatement = db.prepareStatement(
                    "SELECT category_name " +
                    "FROM category " +
                    "WHERE category_number = ?");
            preparedStatement.setInt(1, id);
            ResultSet category_info = preparedStatement.executeQuery();

            if(category_info.next()){
                String name = category_info.getString("category_name");
                categoryItem = new CategoryItem(id, name);
            }
            category_info.close();
            preparedStatement.close();

        }catch (SQLException e) {
            e.printStackTrace();
        }
        return categoryItem;
    }



    public static ClientItem getCardById(Connection connection, String id) {
        ClientItem card = null;
        try{
            PreparedStatement preparedStatement = connection.prepareStatement(
                    "SELECT * " +
                    "FROM customer_card " +
                    "WHERE card_number = ?");
            preparedStatement.setString(1, id);
            ResultSet customers_information = preparedStatement.executeQuery();

            if(customers_information.next()){
                String name = customers_information.getString("cust_surname") + " " + customers_information.getString("cust_name") + " " + customers_information.getString("cust_patronymic");
                String phoneNum = customers_information.getString("phone_number");
                String address = "м. " + customers_information.getString("city") + ", вул. " + customers_information.getString("street") + ", поштовий індекс : " + customers_information.getString("zip_code");
                double percent = customers_information.getDouble("percent");
                card = new ClientItem(id, name, phoneNum, address, percent);
            }
            customers_information.close();
            preparedStatement.close();

        }catch (SQLException e) {
            e.printStackTrace();
        }
        return card;
    }
    public static Receipt getReceiptByID(Connection connection, String id) {
        Receipt receipt = null;
        try {
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM `check` c WHERE check_number = ?");
            preparedStatement.setString(1, id);
            ResultSet check_info = preparedStatement.executeQuery();

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

            preparedStatement = connection.prepareStatement("SELECT * FROM product p " +
                    "JOIN store_product s ON p.id_product = s.id_product " +
                    "JOIN sale sa ON sa.upc = s.upc " +
                    "JOIN `check` c ON sa.check_number = c.check_number " +
                    "WHERE c.check_number = ?");
            preparedStatement.setString(1, id);
            ResultSet product_info = preparedStatement.executeQuery();

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
            preparedStatement.close();

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

        PreparedStatement preparedStatement = connection.prepareStatement("SELECT * " +
                "FROM `check` " +
                "WHERE id_employee = ? AND print_date >= ?");
        preparedStatement.setString(1, employee);
        preparedStatement.setDate(2, date);
        ResultSet resultSet = preparedStatement.executeQuery();

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
        preparedStatement.close();

        return receipts;
    }

    public static void addReceiptToDB(Receipt receipt) throws SQLException {
        String card = null;
        if(receipt.getCard() != null) card = receipt.getCard().getCard_id();

        Connection connection = CashierMenuViewController.database;
        PreparedStatement preparedStatement = connection.prepareStatement("INSERT INTO `check` (id_employee, card_number, print_date, sum_total, vat)" +
                " VALUES (?, ?, ?, ?, ?)");

        preparedStatement.setString(1, receipt.getEmployee());
        preparedStatement.setString(2, card);
        preparedStatement.setDate(3, new Date(receipt.getDate().getTime()));
        preparedStatement.setDouble(4, receipt.getSum());
        preparedStatement.setDouble(5, receipt.getVat());
        preparedStatement.executeUpdate();
        preparedStatement.close();
        addSoldProductsToDB(receipt);
    }

    public static void addSoldProductsToDB(Receipt receipt) throws SQLException {
        int id = getCheckID(receipt);
        Connection connection = CashierMenuViewController.database;
        for (ProductItem productItem : receipt.getProducts()) {
            double price = productItem.getPrice()/productItem.getAmount();
            PreparedStatement preparedStatement = connection.prepareStatement(
                    "INSERT INTO `sale` (upc, check_number, product_number, selling_price)" +
                    " VALUES (?, ?, ?, ?)");
            preparedStatement.setString(1, productItem.getUpc());
            preparedStatement.setInt(2, id);
            preparedStatement.setInt(3, productItem.getAmount());
            preparedStatement.setDouble(4, price);
            try{
                preparedStatement.executeUpdate();
            }catch (SQLException e){
                showSQLError("Could not add sold products to the database");
            }
            preparedStatement.close();
        }
    }

    public static int getCheckID(Receipt receipt) throws SQLException {
        Connection connection = CashierMenuViewController.database;
        PreparedStatement preparedStatement = connection.prepareStatement("SELECT check_number " +
                "FROM `check` " +
                "WHERE id_employee = ? " +
                "AND print_date = ? " +
                "AND sum_total = ? ");
        preparedStatement.setString(1, receipt.getEmployee());
        preparedStatement.setDate(2, new Date(receipt.getDate().getTime()));
        preparedStatement.setDouble(3, receipt.getSum());

        ResultSet set = preparedStatement.executeQuery();
        if(set != null && set.next()) {
            return set.getInt("check_number");
        } else {
            throw new SQLException("No check number found");
        }

    }

    public static void removeReceiptProducts(ObservableList<ProductItem> items) throws SQLException {
        Connection connection = CashierMenuViewController.database;
        for(ProductItem item : items) {
            PreparedStatement preparedStatement = connection.prepareStatement(
                    "SELECT * " +
                    "FROM store_product " +
                            "WHERE upc = ?");
            preparedStatement.setString(1, item.getUpc());
            ResultSet dbItem = preparedStatement.executeQuery();


            if(dbItem == null || !dbItem.next()) {
                showSQLError("No item with UPC "+item.getUpc());
                throw new SQLException("No item with UPC "+item.getUpc());
            }

            int amount = dbItem.getInt("products_number") - item.getAmount();
            if(amount < 0) {
                showSQLError("Product "+item.getUpc()+" is out of stock");
                throw new SQLException("Product "+item.getUpc()+" is out of stock");
            }

            preparedStatement = connection.prepareStatement(
                    "UPDATE store_product t " +
                    "SET t.products_number = ? " +
                    "WHERE t.upc = ?");
            preparedStatement.setInt(1, amount);
            preparedStatement.setString(2, dbItem.getString("upc"));

            try{
                preparedStatement.executeUpdate();
            }catch (SQLException e) {
                showSQLError("Failed to update product with UPC "+item.getUpc());
            }
            preparedStatement.close();
        }
    }


    public static String getWhereFilter(FilterQuery query) {
        String promo = "";
        if(query.saleFilter() == SaleFilter.Sale) promo = "sp.promotional_product = 1";
        else if (query.saleFilter() == SaleFilter.NonSale) promo = "sp.promotional_product = 0";

        String category = "";
        if(query.categoryFilter() != null) {
            if(Objects.equals(query.tableType(), "categories"))
                category = "c.category_number = " + query.categoryFilter().getId();
            else
                category = "p.category_number = " + query.categoryFilter().getId();

        }

        String textarea = "";
        if(query.query() != null && !query.query().isEmpty() && query.searchBy().equals("Назвою")) {
            if(Objects.equals(query.tableType(), "categories"))
                textarea = "LOWER(c.category_name) LIKE '"+query.query().toLowerCase() + "%'";
            else
                textarea = "LOWER(p.product_name) LIKE '"+query.query().toLowerCase() + "%'";
        }
        if(query.query() != null && !query.query().isEmpty() && query.searchBy().equals("UPC"))
            textarea = "LOWER(sp.upc) LIKE '"+query.query().toLowerCase() + "%'";
        if(query.query() != null && !query.query().isEmpty() && query.searchBy().equals("ID")) {
            try{
                Integer id = Integer.parseInt(query.query());
                textarea = "p.id_product = "+id;

            }catch (NumberFormatException e) {
                textarea = "p.id_product = -1";

            }
        }
        if(query.query() != null && !query.query().isEmpty() && query.searchBy().equals("Номером")){
            try{
                Integer id = Integer.parseInt(query.query());
                if(Objects.equals(query.tableType(), "categories"))
                    textarea = "c.category_number = "+id;
                else
                    textarea = "p.category_number = "+id;

            }catch (NumberFormatException e) {
                textarea = "c.category_number = -1";
            }


        }

        //нема сил писати розумно хай буде так
        String result = "";
        if(Objects.equals(query.tableType(), "products") && !promo.isEmpty()) {
            result = promo;
            if(!category.isEmpty()) {
                result += " AND " + category;
                if(!textarea.isEmpty()) {
                    result += " AND " + textarea;
                }
            }else if(!textarea.isEmpty()) {
                result += " AND " + textarea;
            }
        } else if(!category.isEmpty()) {
            result += category;
            if(!textarea.isEmpty()) {
                result += " AND " + textarea;
            }
        } else if(!textarea.isEmpty()) {
            result += textarea;
        }

        System.out.println("WHERE "+result);
        return result;
    }

    public static String getOrderByFilter(FilterQuery query) {
        if(query.sortBy().equals("Назвою")) return query.tableType().equals("categories") ? "category_name" :  "product_name";
        if(query.sortBy().equals("Кількістю")) return "products_number";
        if(query.sortBy().equals("Категорією")) return "p.category_number";
        if(query.sortBy().equals("Номером")) return "category_number";
        return "";
    }


    public static ObservableList<ProductItem> getFilteredProducts(FilterQuery query, Connection connection) {

        ObservableList<ProductItem> productItems = FXCollections.observableArrayList();

        try {
            Statement db = connection.createStatement();

            String whereFilter = getWhereFilter(query);
            String orderBy = getOrderByFilter(query);

            if(whereFilter != null && !whereFilter.isEmpty()) whereFilter = "WHERE "+whereFilter;
            else whereFilter = "";
            if(orderBy != null && !orderBy.isEmpty()) orderBy = "ORDER BY "+orderBy;
            else orderBy = "";

            ResultSet product_info = db.executeQuery(
                    "SELECT * FROM product p " +
                            "JOIN store_product sp ON p.id_product = sp.id_product " +
                            whereFilter + " " +
                            orderBy);

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

    public static ObservableList<ProductInfo> getFilteredAllProductTableItems(FilterQuery query, Connection connection) {
        ObservableList<ProductInfo> productItems = FXCollections.observableArrayList();
        try {
            Statement db = connection.createStatement();
            String whereFilter = getWhereFilter(query);
            String orderBy = getOrderByFilter(query);

            if(whereFilter != null && !whereFilter.isEmpty()) whereFilter = "WHERE "+whereFilter;
            else whereFilter = "";
            if(orderBy != null && !orderBy.isEmpty()) orderBy = "ORDER BY "+orderBy;
            else orderBy = "";

            ResultSet product_info = db.executeQuery(
                    "SELECT * FROM product p " +
                            "JOIN category c ON p.category_number = c.category_number " +
                            whereFilter + " " +
                            orderBy);

            while (product_info.next()) {
                int id = product_info.getInt("id_product");
                String name = product_info.getString("product_name");
                String description = product_info.getString("characteristics");
                String category = product_info.getString("category_name");
                productItems.add(new ProductInfo(id, category, name, description));
            }

            product_info.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return productItems;

    }

    public static ObservableList<CategoryItem> getFilteredCategoryTableItems(FilterQuery query, Connection connection) {
        ObservableList<CategoryItem> productItems = FXCollections.observableArrayList();
        try {
            Statement db = connection.createStatement();

            String whereFilter = getWhereFilter(query);
            String orderBy = getOrderByFilter(query);

            if(whereFilter != null && !whereFilter.isEmpty()) whereFilter = "WHERE "+whereFilter;
            else whereFilter = "";
            if(orderBy != null && !orderBy.isEmpty()) orderBy = "ORDER BY "+orderBy;
            else orderBy = "";

            ResultSet product_info = db.executeQuery(
                    "SELECT * FROM category c " +
                            whereFilter + " " +
                            orderBy);

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
}
