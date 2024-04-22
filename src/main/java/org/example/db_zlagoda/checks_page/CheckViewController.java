package org.example.db_zlagoda.checks_page;

import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import org.example.db_zlagoda.DatabaseConnection;

import java.net.URL;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ResourceBundle;


public class CheckViewController implements Initializable {
    @FXML
    private Label checkNumberLabel, finalPriceLabel;

    @FXML
    private TableView<Object[]> checkTable;

    @FXML
    private TableColumn<Object[], String> upcCol, nameCol, numberOfCol, priceCol, sumCol;

    private String CHECK_NUMBER = "";

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        upcCol.setCellValueFactory(cellData -> cellData.getValue()[0] != null ? new SimpleStringProperty(cellData.getValue()[0].toString()) : null);
        nameCol.setCellValueFactory(cellData -> cellData.getValue()[1] != null ? new SimpleStringProperty(cellData.getValue()[1].toString()) : null);
        numberOfCol.setCellValueFactory(cellData -> cellData.getValue()[2] != null ? new SimpleStringProperty(cellData.getValue()[2].toString()) : null);
        priceCol.setCellValueFactory(cellData -> cellData.getValue()[3] != null ? new SimpleStringProperty(cellData.getValue()[3].toString()) : null);
        sumCol.setCellValueFactory(cellData -> cellData.getValue()[4] != null ? new SimpleStringProperty(cellData.getValue()[4].toString()) : null);
    }

    public void fillCheck(String checkNumber){
        this.CHECK_NUMBER = checkNumber;
        checkNumberLabel.setText(checkNumber);
        finalPriceLabel.setText(finalPrice());
        setTable();
    }

    private String finalPrice(){
        String finalPrice = "";
        try {
            DatabaseConnection connection = new DatabaseConnection();
            Connection connectDB = connection.getConnection();
            Statement statement = connectDB.createStatement();
            // Загальна вартість чеку за його номером
            ResultSet checks_information = statement.executeQuery(
                    "SELECT sum_total " +
                            "FROM `check` " +
                            "WHERE check_number = '" + CHECK_NUMBER + "' " );
            if (checks_information.next()) {
                finalPrice = checks_information.getString("sum_total");
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return finalPrice;
    }

    private void setTable(){
        try {
            DatabaseConnection connection = new DatabaseConnection();
            Connection connectDB = connection.getConnection();
            Statement statement = connectDB.createStatement();
            // 17. Отримати інформацію про усі чеки, створені певним касиром за певний період
            // часу (з можливістю перегляду куплених товарів у цьому чеку, їх назви, к-сті та ціни);
            ResultSet checks_information = statement.executeQuery(
                    "SELECT sale.UPC, sale.product_number, sale.selling_price, product_name " +
                            "FROM sale " +
                            "INNER JOIN store_product " +
                            "ON store_product.UPC = sale.UPC " +
                            "INNER JOIN product " +
                            "ON product.id_product = store_product.id_product " +
                            "WHERE check_number = '" + CHECK_NUMBER + "'");
            while (checks_information.next()) {
                Object[] rowData = {
                        checks_information.getString("UPC"),
                        checks_information.getString("product_name"),
                        checks_information.getString("product_number"),
                        checks_information.getString("selling_price"),
                        String.valueOf(Double.parseDouble(checks_information.getString("selling_price")) * Double.parseDouble(checks_information.getString("product_number")))
                };
                checkTable.getItems().add(rowData);
            }

        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
