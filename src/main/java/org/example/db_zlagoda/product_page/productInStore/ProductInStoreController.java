package org.example.db_zlagoda.product_page.productInStore;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.lowagie.text.pdf.BaseFont;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.print.PrinterJob;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.example.db_zlagoda.DatabaseConnection;
import org.example.db_zlagoda.update_employee_page.UpdateEmployeeController;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.sql.*;
import java.util.ResourceBundle;
import java.util.stream.Stream;

import static org.example.db_zlagoda.db_data.DatabaseManager_mm.*;

public class ProductInStoreController implements Initializable {
    int SCENE = 0;

    @FXML
    private Button updateButton, addButton, deleteButton,
            showInfoButton,
            allProdButton, promProdButton, no_promProdButton,
            printButton,
            productRevaluationButton;

    @FXML
    private TableView<Object[]> productsInStoreTable;

    @FXML
    private TableColumn<Object[], String> upcColumn, upcPromColumn, prodIDColumn,
            productNameColumn, sellingPriceColumn, numOfProdColumn, promotionalColumm;

    @FXML
    private TextField UPC_field;

    @FXML
    private TableView<Object[]> searchTable;

    @FXML
    private TableColumn<Object[], String> nameOfProdCol, characteristicsOfProdCol, priceOfProdCol, numOfProdCol;

    @FXML
    private ComboBox sortByBox;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        upcColumn.setCellValueFactory(cellData -> cellData.getValue()[0] != null ? new SimpleStringProperty(cellData.getValue()[0].toString()) : null);
        upcPromColumn.setCellValueFactory(cellData -> cellData.getValue()[1] != null ? new SimpleStringProperty(cellData.getValue()[1].toString()) : null);
        prodIDColumn.setCellValueFactory(cellData -> cellData.getValue()[2] != null ? new SimpleStringProperty(cellData.getValue()[2].toString()) : null);
        productNameColumn.setCellValueFactory(cellData -> cellData.getValue()[3] != null ? new SimpleStringProperty(cellData.getValue()[3].toString()) : null);
        sellingPriceColumn.setCellValueFactory(cellData -> cellData.getValue()[4] != null ? new SimpleStringProperty(cellData.getValue()[4].toString()) : null);
        numOfProdColumn.setCellValueFactory(cellData -> cellData.getValue()[5] != null ? new SimpleStringProperty(cellData.getValue()[5].toString()) : null);
        promotionalColumm.setCellValueFactory(cellData -> cellData.getValue()[6] != null ? new SimpleStringProperty(cellData.getValue()[6].toString()) : null);

        upcColumn.setSortable(false);
        upcPromColumn.setSortable(false);
        prodIDColumn.setSortable(false);
        productNameColumn.setSortable(false);
        sellingPriceColumn.setSortable(false);
        numOfProdColumn.setSortable(false);
        promotionalColumm.setSortable(false);

        loadProductsInStore();

        nameOfProdCol.setCellValueFactory(cellData -> cellData.getValue()[0] != null ? new SimpleStringProperty(cellData.getValue()[0].toString()) : null);
        characteristicsOfProdCol.setCellValueFactory(cellData -> cellData.getValue()[1] != null ? new SimpleStringProperty(cellData.getValue()[1].toString()) : null);
        priceOfProdCol.setCellValueFactory(cellData -> cellData.getValue()[2] != null ? new SimpleStringProperty(cellData.getValue()[2].toString()) : null);
        numOfProdCol.setCellValueFactory(cellData -> cellData.getValue()[3] != null ? new SimpleStringProperty(cellData.getValue()[3].toString()) : null);

        nameOfProdCol.setSortable(false);
        characteristicsOfProdCol.setSortable(false);
        priceOfProdCol.setSortable(false);
        numOfProdCol.setSortable(false);

        sortByBox.setVisible(false);

        ObservableList<String> options =
                FXCollections.observableArrayList(
                        "За назвою товару",
                        "За кількістю одиниць товару"
                );
        sortByBox.setItems(options);
//        updateAddBlock.setVisible(false);
//
        updateButton.setDisable(true);
        deleteButton.setDisable(true);
        addButton.setDisable(false);
        productRevaluationButton.setDisable(true);

//        updateBox.setDisable(true);
//        addBox.setDisable(true);

        productsInStoreTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection == null) {
                updateButton.setDisable(true);
                deleteButton.setDisable(true);
                productRevaluationButton.setDisable(true);
            } else {
                updateButton.setDisable(false);
                deleteButton.setDisable(false);
                if (newSelection[6].toString().equals("0")){
                    productRevaluationButton.setDisable(false);
                } else if (newSelection[6].toString().equals("1")) {
                    productRevaluationButton.setDisable(false);
                    DatabaseConnection connection = new DatabaseConnection();
                    Connection connectDB = connection.getConnection();
                    try {
                        String productId = newSelection[2].toString();

                        String checkNonPromotionalQuery = "SELECT * FROM store_product " +
                                "WHERE id_product = '" + productId + "' " +
                                "AND promotional_product = '0'";

                        Statement statement = connectDB.createStatement();
                        ResultSet resultSet = statement.executeQuery(checkNonPromotionalQuery);
                        if (resultSet.next()) {
                            // Якщо є товар з вказаним id, який не є акційним, то активувати кнопку
                            productRevaluationButton.setOnAction(productRevaluationButton.getOnAction());
                        } else {
                            // Якщо товару з вказаним id немає або він є акційним, то деактивувати кнопку
                            try {
                                FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/db_zlagoda/product_page/product-in-store-add-update-view.fxml"));
                                Parent root = loader.load();
                                ProductInStoreAddUpdateController controller = loader.getController();
                                controller.add(newSelection[2].toString());

                                Stage stage = new Stage();
                                stage.setScene(new Scene(root));
                                stage.show();
                                stage.setOnHidden(e -> {
                                    productsInStoreTable.getItems().clear();
                                    loadProductsInStore();
                                });
                            } catch (IOException e) {}
                        }

                        resultSet.close();
                        statement.close();
                        connectDB.close();
                    } catch (SQLException e) {
                        throw new RuntimeException(e);
                    }

                }

                Object[] selectedProduct = productsInStoreTable.getSelectionModel().getSelectedItem();
                UPC_field.setText(selectedProduct[0].toString());
            }
        });
    }

    @FXML
    public void productRevaluationButtonOnAction (ActionEvent event) throws IOException, SQLException {
        // переоцінка товару
        revaluate();
    }

    public void revaluate () throws IOException, SQLException {
        Object[] selectedProduct = productsInStoreTable.getSelectionModel().getSelectedItem();
        if (selectedProduct != null) {
            String prod_upc = selectedProduct[0].toString();

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/db_zlagoda/product_page/product-revaluate-view.fxml"));
            Parent root = loader.load();
            ProductInStoreRevaluateController controller = loader.getController();
            // змінити ціну можна лише в НЕакційного товару
            if (selectedProduct[6].equals("0")) {
                controller.fillFields(prod_upc);
            } else {
                DatabaseConnection connection = new DatabaseConnection();
                Connection connectDB = connection.getConnection();
                Statement statement = connectDB.createStatement();

                // якщо обраний товар акційний, то відбувається пошук НЕакційного товару, що посилається на той
                // самий продукт
                ResultSet non_promUPC = statement.executeQuery(
                        "SELECT UPC FROM store_product " +
                                "WHERE id_product = '" + selectedProduct[2] + "' AND " +
                                "promotional_product = '0'"
                );
                if (non_promUPC.next()){
                    controller.fillFields(non_promUPC.getString("UPC"));
                }
            }

            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.show();
            stage.setOnHidden(e -> {
                productsInStoreTable.getItems().clear();
                loadProductsInStore();
            });
        }
    }

    private void loadProductsInStore() {
        try {
            ResultSet product_store_information = null;
            if (SCENE == 0){
                // 10. Отримати інформацію про усі товари у магазині, відсортовані за кількістю;
                product_store_information = getProductsInStoreInformation_quantity();
            } else if (SCENE == 1){
                // 15. Отримати інформацію про усі акційні товари, відсортовані за назвою товару
                product_store_information = getProductsInStoreInformation_prom_name();
            } else if (SCENE == 2){
                // 15. Отримати інформацію про усі акційні товари, відсортовані за кількістю одиниць товару
                product_store_information = getProductsInStoreInformation_prom_quantity();
            } else if (SCENE == 3){
                // 16. Отримати інформацію про усі не акційні товари, відсортовані за назвою;
                product_store_information = getProductsInStoreInformation_non_prom_name();
            } else if (SCENE == 4){
                // 16. Отримати інформацію про усі не акційні товари, відсортовані за кількістю одиниць товару
                product_store_information = getProductsInStoreInformation_non_prom_quantity();
            }

            while (product_store_information.next()) {
                addProductToTable(product_store_information);
            }

            product_store_information.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void addProductToTable(ResultSet product_store_information) {
        try {
            String productsNumber = product_store_information.getString("products_number");
            Object[] rowData = {
                    product_store_information.getString("UPC"),
                    product_store_information.getString("UPC_prom"),
                    product_store_information.getString("id_product"),
                    product_store_information.getString("product_name"),
                    product_store_information.getString("selling_price"),
                    product_store_information.getString("products_number"),
                    product_store_information.getString("promotional_product")
            };

            productsInStoreTable.getItems().add(rowData);

            TableColumn<Object[], String> productsNumberColumn = (TableColumn<Object[], String>) productsInStoreTable.getColumns().get(5);
            productsNumberColumn.setCellValueFactory(cellData -> new SimpleStringProperty((String) cellData.getValue()[5]));

            productsNumberColumn.setCellFactory(column -> new TableCell<Object[], String>() {
                @Override
                protected void updateItem(String item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty || item == null) {
                        setText(null);
                        setStyle("");
                    } else {
                        setText(item);
                        TableRow<Object[]> row = getTableRow();
                        if (row != null && row.getItem() != null) {
                            String productsNumber = item;
                            if ("0".equals(productsNumber)) {
                                row.setStyle("-fx-background-color: red;");
                            } else {
                                row.setStyle("");
                            }
                        }
                    }
                }
            });
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    @FXML
    public void updateButtonOnAction(ActionEvent event) throws IOException {
        Object[] selectedProduct = productsInStoreTable.getSelectionModel().getSelectedItem();
        if (selectedProduct != null) {
            String prod_upc = selectedProduct[0].toString();

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/db_zlagoda/product_page/product-in-store-add-update-view.fxml"));
            Parent root = loader.load();
            ProductInStoreAddUpdateController controller = loader.getController();
            controller.fillFields(prod_upc, false);
            controller.update();

            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.show();
            stage.setOnHidden(e -> {
                productsInStoreTable.getItems().clear();
                loadProductsInStore();
            });
        }
    }

    @FXML
    public void addButtonOnAction(ActionEvent event) throws IOException, SQLException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/db_zlagoda/product_page/product-in-store-add-update-view.fxml"));
        Parent root = loader.load();
        ProductInStoreAddUpdateController controller = loader.getController();
        controller.add(null);

        Stage stage = new Stage();
        stage.setScene(new Scene(root));
        stage.show();
        stage.setOnHidden(e -> {
            productsInStoreTable.getItems().clear();
            loadProductsInStore();
        });
    }

    @FXML
    public void deleteButtonOnAction(ActionEvent event) throws IOException {
        try{
            Object[] selectedProduct = productsInStoreTable.getSelectionModel().getSelectedItem();
            if (selectedProduct != null) {
                String upc_prod = selectedProduct[0].toString();

                DatabaseConnection connection = new DatabaseConnection();
                Connection connectDB = connection.getConnection();
                Statement statement = connectDB.createStatement();

                // 3. Видаляти дані про товари у магазині
                String checkProductsQuery = "SELECT COUNT(*) FROM store_product " +
                        "WHERE UPC = '" + upc_prod + "' AND UPC_prom IS NOT NULL";
                ResultSet resultSet = statement.executeQuery(checkProductsQuery);
                resultSet.next();
                int productCount = resultSet.getInt(1);

                if (productCount > 0) {
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Помилка");
                    alert.setHeaderText(null);
                    alert.setContentText("Неможливо видалити неакційний товар, оскільки " +
                            "на нього посилається акційний.");
                    alert.showAndWait();
                } else {
                    deleteProductInStore(upc_prod);
                }

                statement.close();
                connectDB.close();

                productsInStoreTable.getItems().clear();
                loadProductsInStore();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void printButtonOnAction(ActionEvent event) throws IOException {
        printReportProductInStore(SCENE);
    }

    @FXML
    public void allProdButtonOnAction(ActionEvent event) throws IOException {
        SCENE = 0;
        sortByBox.setVisible(false);
        productsInStoreTable.getItems().clear();
        loadProductsInStore();
    }

    @FXML
    public void promProdButtonOnAction(ActionEvent event) throws IOException {
        sortByBox.setVisible(true);
        productsInStoreTable.getItems().clear();

        sortByBox.valueProperty().addListener((observable, oldValue, option) -> {
            if (option != null) {
                sortByBox.setStyle(null);
                productsInStoreTable.getItems().clear();
                if (sortByBox.getValue().equals("За назвою товару")){
                    SCENE = 1;
                } else if (sortByBox.getValue().equals("За кількістю одиниць товару")){
                    SCENE = 2;
                }
                loadProductsInStore();
            } else {
                sortByBox.setStyle("-fx-border-color: RED; -fx-border-width: 2px");
            }
        });
    }

    @FXML
    public void no_promProdButtonOnAction(ActionEvent event) throws IOException {
        sortByBox.setVisible(true);
        productsInStoreTable.getItems().clear();

        sortByBox.valueProperty().addListener((observable, oldValue, option) -> {
            if (option != null){
                sortByBox.setStyle(null);
                productsInStoreTable.getItems().clear();
                if (sortByBox.getValue().equals("За назвою товару")){
                    SCENE = 3;
                } else if (sortByBox.getValue().equals("За кількістю одиниць товару")){
                    SCENE = 4;
                }
                loadProductsInStore();
            } else {
                sortByBox.setStyle("-fx-border-color: RED; -fx-border-width: 2px");
            }
        });
    }

    @FXML
    public void showInfoButtonOnAction(ActionEvent event) throws IOException, SQLException {
        // 14. За UPC-товару знайти ціну продажу товару, кількість наявних одиниць
        // товару, назву та характеристики товару;
        searchTable.getItems().clear();
        try {
            ResultSet prod_store_info = searchByUPC(UPC_field.getText());

            while (prod_store_info.next()) {
                String name = prod_store_info.getString("product_name");
                String characteristics = prod_store_info.getString("characteristics");
                String price = prod_store_info.getString("selling_price");
                String number = prod_store_info.getString("products_number");

                Object[] rowData = {name, characteristics, price, number};
                searchTable.getItems().add(rowData);
            }

            prod_store_info.close();
        } catch (
                SQLException e) {
            e.printStackTrace();
        }
    }

    private ResultSet executeQuery(String query) throws SQLException {
        DatabaseConnection connection = new DatabaseConnection();
        Connection connectDB = connection.getConnection();
        Statement statement = connectDB.createStatement();
        return statement.executeQuery(query);
    }
}