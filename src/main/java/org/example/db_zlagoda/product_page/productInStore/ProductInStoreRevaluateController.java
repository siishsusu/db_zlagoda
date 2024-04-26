package org.example.db_zlagoda.product_page.productInStore;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.example.db_zlagoda.DatabaseConnection;

import javafx.event.ActionEvent;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import static org.example.db_zlagoda.db_data.DatabaseManager_mm.updateProdInStore;

public class ProductInStoreRevaluateController {

    @FXML
    private TextField priceField, quantityField;

    @FXML
    private Label price_db, quantity_db, new_price, new_quantity;

    @FXML
    private Button revaluateButton;

    private Integer DB_QUANTITY = 0;

    public void initialize() {
        price_db.setText("(ціна)");
        quantity_db.setText("(кількість)");

        priceField.textProperty().addListener((observable, oldValue, newValue) -> {
            new_price.setText(newValue);
        });

        quantityField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.isEmpty()) {
                try {
                    new_quantity.setText(String.valueOf(Integer.parseInt(newValue) + DB_QUANTITY));
                } catch (NumberFormatException ex) {
                    ex.printStackTrace();
                }
            }
        });
    }

    @FXML
    public void revaluateButtonOnAction(ActionEvent event) {
        try {
            String newPrice = new_price.getText();
            String newQuantity = new_quantity.getText();
            String prodUpc = upc;

            updateProductRevaluation(prodUpc, newPrice, newQuantity);
            System.out.println("Product revaluated successfully!");

            Stage stage = (Stage) quantityField.getScene().getWindow();
            stage.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void updateProductRevaluation(String prodUpc, String newPrice, String newQuantity) throws SQLException {
        DatabaseConnection connection = new DatabaseConnection();
        Connection connectDB = connection.getConnection();
        Statement statement = connectDB.createStatement();

        String updateQuery = "UPDATE store_product " +
                "SET selling_price = '" + newPrice + "', " +
                "products_number = '" + newQuantity + "' " +
                "WHERE UPC = '" + prodUpc + "'";

        statement.executeUpdate(updateQuery);

        statement.close();
        connectDB.close();
    }

    private String upc;
    public void fillFields(String prodUpc) throws SQLException {
        upc = prodUpc;

        DatabaseConnection connection = new DatabaseConnection();
        Connection connectDB = connection.getConnection();
        Statement statement = connectDB.createStatement();
        // Витягнути інформацію за UPC з бази даних
        ResultSet resultSet = statement.executeQuery("SELECT UPC, UPC_prom, " +
                "store_product.id_product, product.product_name, " +
                "selling_price, products_number, promotional_product " +
                "FROM store_product " +
                "INNER JOIN product " +
                "ON product.id_product = store_product.id_product " +
                "WHERE UPC = '" + prodUpc + "'");

        if (resultSet.next()) {
            priceField.setText(resultSet.getString("selling_price"));
            quantity_db.setText(resultSet.getString("products_number"));
        }
        DB_QUANTITY = Integer.valueOf(quantity_db.getText());
        quantityField.setText("1");
        price_db.setText(priceField.textProperty().getValue());
    }
}
