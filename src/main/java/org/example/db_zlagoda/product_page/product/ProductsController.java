package org.example.db_zlagoda.product_page.product;

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
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.example.db_zlagoda.DatabaseConnection;
import org.example.db_zlagoda.product_page.productInStore.ProductInStoreAddUpdateController;
import org.example.db_zlagoda.product_page.productInStore.ProductInStoreRevaluateController;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Stream;

import static org.example.db_zlagoda.db_data.DatabaseManager_mm.*;
import static org.example.db_zlagoda.login_page.RegistrationController.generateRandomId;

public class ProductsController implements Initializable {
    @FXML
    private TableView<Object[]> productsTable;

    @FXML
    private Button updateButton, deleteButton, addButton, doUpdateButton, addToStoreButton;

    @FXML
    private ComboBox<String> searchOption;

    @FXML
    private TextField categoryField;

    @FXML
    private HBox updateAddContainer;

    @FXML
    private ComboBox categoryNamesBox;

    @FXML
    private TextField productNameField, productCharacteristicsField;

    @FXML
    private Label infoLabel;

    @FXML
    private AnchorPane allPane;

    @FXML
    private Button searchButton, clearFieldButton;

    @FXML
    private TableColumn<Object[], String> idColumn, categoryNumColumn, categoryNameColumn, productNameColumn, characteristicColumn;

    private List<TextField> textFieldList;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        updateAddContainer.setVisible(false);
        ObservableList<String> options =
                FXCollections.observableArrayList(
                        "За назвою категорії",
                        "За номером категорії"
                );
        searchOption.setItems(options);

        idColumn.setCellValueFactory(cellData -> cellData.getValue()[0] != null ? new SimpleStringProperty(cellData.getValue()[0].toString()) : null);
        categoryNumColumn.setCellValueFactory(cellData -> cellData.getValue()[1] != null ? new SimpleStringProperty(cellData.getValue()[1].toString()) : null);
        categoryNameColumn.setCellValueFactory(cellData -> cellData.getValue()[2] != null ? new SimpleStringProperty(cellData.getValue()[2].toString()) : null);
        productNameColumn.setCellValueFactory(cellData -> cellData.getValue()[3] != null ? new SimpleStringProperty(cellData.getValue()[3].toString()) : null);
        characteristicColumn.setCellValueFactory(cellData -> cellData.getValue()[4] != null ? new SimpleStringProperty(cellData.getValue()[4].toString()) : null);

        idColumn.setSortable(false);
        categoryNumColumn.setSortable(false);
        categoryNumColumn.setSortable(false);
        productNameColumn.setSortable(false);
        characteristicColumn.setSortable(false);

        loadProductsData();

        searchButton.setDisable(true);
        categoryField.setDisable(true);
        updateButton.setDisable(true);
        deleteButton.setDisable(true);
        addToStoreButton.setDisable(true);

        searchOption.valueProperty().addListener((observable, oldValue, option) -> {
            if (option != null) {
                categoryField.setDisable(false);
            } else {
                categoryField.setDisable(true);
            }
        });
        categoryField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.isBlank() && searchOption.getValue() != null) {
                searchButton.setDisable(false);
            } else {
                searchButton.setDisable(true);
            }
        });

        productsTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection == null) {
                updateButton.setDisable(true);
                deleteButton.setDisable(true);
                addToStoreButton.setDisable(true);
            } else {
                searchButton.setDisable(false);
                updateButton.setDisable(false);
                deleteButton.setDisable(false);
                addToStoreButton.setDisable(false);

                Object[] selected = productsTable.getSelectionModel().getSelectedItem();
                if(searchOption.getValue() != null) {
                    if (searchOption.getValue().equals("За номером категорії")){
                        categoryField.setText(selected[1].toString());
                    } else {
                        categoryField.setText(selected[2].toString());
                    }
                }
            }
        });

        searchOption.setValue("За номером категорії");

        ObservableList<String> categories = FXCollections.observableList(getCategories());
        categoryNamesBox.setItems(categories);

        textFieldList = Arrays.asList(productNameField, productCharacteristicsField);
    }

    private ArrayList<String> getCategories(){
        ArrayList <String> result = new ArrayList<>();
        try {
            DatabaseConnection connection = new DatabaseConnection();
            Connection connectDB = connection.getConnection();
            Statement statement = connectDB.createStatement();
            ResultSet categories_info = null;

            // витягнути назви категорій з бд
            categories_info = statement.executeQuery(
                    "SELECT category_name " +
                            "FROM category");

            while (categories_info.next()) {
                result.add(categories_info.getString("category_name"));
            }

            categories_info.close();
            statement.close();
            connectDB.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return result;
    }

    @FXML
    public void addToStoreButtonOnAction (ActionEvent event) throws IOException {
        try{
            Object[] selectedProduct = productsTable.getSelectionModel().getSelectedItem();
            if (selectedProduct != null) {
                String id_prod_selected = selectedProduct[0].toString();

                DatabaseConnection connection = new DatabaseConnection();
                Connection connectDB = connection.getConnection();
                Statement statement = connectDB.createStatement();

                // Додавати товар в магазин

                String checkProductsQuery = "SELECT COUNT(*) FROM store_product " +
                        "WHERE id_product = '" + id_prod_selected + "'";
                ResultSet resultSet = statement.executeQuery(checkProductsQuery);
                resultSet.next();
                int productCount = resultSet.getInt(1);

                if (productCount == 2) {
//                    FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/db_zlagoda/product_page/product-in-store-add-update-view.fxml"));
//                    Parent root = loader.load();
//                    ProductInStoreAddUpdateController controller = loader.getController();

                    FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/db_zlagoda/product_page/product-revaluate-view.fxml"));
                    Parent root = loader.load();
                    ProductInStoreRevaluateController controller = loader.getController();

                    // якщо обраний товар акційний, то відбувається пошук НЕакційного товару, що посилається на той
                    // самий продукт
                    ResultSet non_promUPC = statement.executeQuery(
                            "SELECT UPC FROM store_product " +
                                    "WHERE id_product = '" + selectedProduct[0] + "' AND " +
                                    "promotional_product = '0'"
                    );
                    if (non_promUPC.next()){
                        controller.fillFields(non_promUPC.getString("UPC"));
                    }

                    Stage stage = new Stage();
                    stage.setTitle("Переоцінка товару " + selectedProduct[3]);
                    stage.setScene(new Scene(root));
                    stage.show();
                } else {
                    FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/db_zlagoda/product_page/product-in-store-add-update-view.fxml"));
                    Parent root = loader.load();
                    ProductInStoreAddUpdateController controller = loader.getController();
                    controller.add(null);
                    controller.fillFields(id_prod_selected, true);

                    Stage stage = new Stage();
                    stage.setScene(new Scene(root));
                    stage.show();

                    clearTable();
                    loadProductsData();
                }

                resultSet.close();
                statement.close();
                connectDB.close();

                clearTable();
                loadProductsData();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void addButtonOnAction (ActionEvent event) throws IOException {
        updateAddContainer.setVisible(true);
        infoLabel.setText("Додавання товару");
        doUpdateButton.setText("Додати");

        addButton.setDisable(true);
        deleteButton.setDisable(true);
        updateButton.setDisable(true);

        doUpdateButton.setOnAction(this::doAddButtonOnAction);
    }

    private void doAddButtonOnAction(ActionEvent actionEvent) {
        boolean allFieldsFilled = true;
        for (TextField textField : textFieldList) {
            if (textField.getText().isBlank()) {
                textField.setStyle("-fx-border-color: RED; -fx-border-width: 2px");
                textField.setTooltip(new Tooltip("Поле не повинно бути пустим"));
                allFieldsFilled = false;
            } else {
                textField.setStyle("");
                if (textField == productNameField) {
                    if (textField.getText().length() > 50) {
                        textField.setStyle("-fx-border-color: RED; -fx-border-width: 2px");
                        textField.setTooltip(new Tooltip("Довжина тексту не повинна перевищувати 50 символів"));
                        allFieldsFilled = false;
                    } else {
                        textField.setTooltip(new Tooltip(""));
                        textField.setStyle("");
                    }
                }

                if (textField == productCharacteristicsField) {
                    if (textField.getText().length() > 100) {
                        textField.setStyle("-fx-border-color: RED; -fx-border-width: 2px");
                        textField.setTooltip(new Tooltip("Поле повинно містити менше 100 символів."));
                        allFieldsFilled = false;
                    } else {
                        textField.setTooltip(new Tooltip(""));
                        textField.setStyle("");
                    }
                }
            }
        }
        boolean categorySelected = !categoryNamesBox.getSelectionModel().isEmpty();
        if (!categorySelected) {
            categoryNamesBox.setStyle("-fx-border-color: RED; -fx-border-width: 2px");
            allFieldsFilled = false;
        } else {
            categoryNamesBox.setStyle(null);
        }
        if (allFieldsFilled) {
            addProduct();
        }
    }

    private void addProduct() {
        try {
            String nameProd = productNameField.getText();
            String characteristicProd = productCharacteristicsField.getText();

            // 1. Додавати нові дані про товари
            String id_gen = generateRandomId(9, true);
            insertProduct(id_gen,
                    getCategoryNumber(categoryNamesBox.getValue().toString()),
                    nameProd,
                    characteristicProd);

            updateAddContainer.setVisible(false);

            addButton.setDisable(false);
            deleteButton.setDisable(false);
            updateButton.setDisable(false);

            clearTable();
            loadProductsData();
            cleanFields();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private int getCategoryNumber(String name) {
        int result = 0;
        try {
            DatabaseConnection connection = new DatabaseConnection();
            Connection connectDB = connection.getConnection();
            Statement statement = connectDB.createStatement();

            ResultSet resultSet = statement.executeQuery("SELECT category_number FROM category WHERE category_name = '" + name + "'");
            if (resultSet.next()) {
                result = resultSet.getInt("category_number");
            }

            resultSet.close();
            statement.close();
            connectDB.close();
            updateAddContainer.setVisible(false);

            addButton.setDisable(false);
            deleteButton.setDisable(false);
            updateButton.setDisable(false);

            clearTable();
            loadProductsData();
            cleanFields();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    private void cleanFields(){
        for (TextField textField : textFieldList) {
            textField.setText("");
        }
    }

    @FXML
    public void deleteButtonOnAction (ActionEvent event) throws IOException {
        try{
            Object[] selectedProduct = productsTable.getSelectionModel().getSelectedItem();
            if (selectedProduct != null) {
                String id_prod = selectedProduct[0].toString();

                DatabaseConnection connection = new DatabaseConnection();
                Connection connectDB = connection.getConnection();
                Statement statement = connectDB.createStatement();

                // 3. Видаляти дані про товари
                String checkProductsQuery = "SELECT COUNT(*) FROM store_product WHERE id_product = '" + id_prod + "'";
                ResultSet resultSet = statement.executeQuery(checkProductsQuery);
                resultSet.next();
                int productCount = resultSet.getInt(1);

                if (productCount > 0) {
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Помилка");
                    alert.setHeaderText(null);
                    alert.setContentText("Неможливо видалити товар, оскільки він є в магазині.");
                    alert.showAndWait();
                } else {
                    deleteProduct(id_prod);

                    clearTable();
                    loadProductsData();
                }

                clearTable();
                loadProductsData();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String prod_id;
    @FXML
    public void updateButtonOnAction (ActionEvent event) throws IOException {
        updateAddContainer.setVisible(true);
        infoLabel.setText("Оновлення інформації");
        doUpdateButton.setText("Оновити");
        Object[] selectedProduct = productsTable.getSelectionModel().getSelectedItem();
        if (selectedProduct != null) {
            this.prod_id = selectedProduct[0].toString();
            fillFields(prod_id);

            addButton.setDisable(true);
            deleteButton.setDisable(true);
            updateButton.setDisable(true);

            doUpdateButton.setOnAction(this::doUpdateButtonOnAction);
        }
    }

    private void fillFields(String id) {
        try {
            DatabaseConnection connection = new DatabaseConnection();
            Connection connectDB = connection.getConnection();
            Statement statement = connectDB.createStatement();

            ResultSet prod_info = statement.executeQuery(
                    "SELECT product.category_number, " +
                            "category.category_name, product.product_name, " +
                            "product.characteristics " +
                            "FROM product " +
                            "INNER JOIN category " +
                            "ON category.category_number = product.category_number " +
                            "WHERE id_product = '" + prod_id + "'");

            if (prod_info.next()) {
                productNameField.setText(prod_info.getString("product_name"));
                productCharacteristicsField.setText(prod_info.getString("characteristics"));
                categoryNamesBox.setValue(prod_info.getString("category_name"));
            }

            statement.close();
            connectDB.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void doUpdateButtonOnAction(ActionEvent actionEvent) {
        boolean allFieldsFilled = true;
        for (TextField textField : textFieldList) {
            if (textField.getText().isBlank()) {
                textField.setStyle("-fx-border-color: RED; -fx-border-width: 2px");
                textField.setTooltip(new Tooltip("Поле не повинно бути пустим"));
                allFieldsFilled = false;
            } else {
                textField.setStyle("");
                if (textField == productNameField) {
                    if (textField.getText().length() > 50) {
                        textField.setStyle("-fx-border-color: RED; -fx-border-width: 2px");
                        textField.setTooltip(new Tooltip("Довжина тексту не повинна перевищувати 50 символів"));
                        allFieldsFilled = false;
                    } else {
                        textField.setTooltip(new Tooltip(""));
                        textField.setStyle("");
                    }
                }

                if (textField == productCharacteristicsField) {
                    if (textField.getText().length() > 100) {
                        textField.setStyle("-fx-border-color: RED; -fx-border-width: 2px");
                        textField.setTooltip(new Tooltip("Поле повинно містити менше 100 символів."));
                        allFieldsFilled = false;
                    } else {
                        textField.setTooltip(new Tooltip(""));
                        textField.setStyle("");
                    }
                }
            }
        }
        boolean categorySelected = !categoryNamesBox.getSelectionModel().isEmpty();
        if (!categorySelected) {
            categoryNamesBox.setStyle("-fx-border-color: RED; -fx-border-width: 2px");
            allFieldsFilled = false;
        } else {
            categoryNamesBox.setStyle(null);
        }
        if (allFieldsFilled) {
            updateProduct();
        }
    }

    private void updateProduct() {
        try {
            String nameProd = productNameField.getText();
            String characteristicProd = productCharacteristicsField.getText();

            // 2. Редагувати дані про товари
            updateProd(prod_id, getCategoryNumber(categoryNamesBox.getValue().toString()), nameProd, characteristicProd);

            updateAddContainer.setVisible(false);

            addButton.setDisable(false);
            deleteButton.setDisable(false);
            updateButton.setDisable(false);

            clearTable();
            loadProductsData();
            cleanFields();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void allProductsButtonOnAction (ActionEvent event) throws IOException {
        clearTable();
        SCREEN = 0;
        loadProductsData();
    }

    @FXML
    public void clearFieldButtonButtonOnAction (ActionEvent event) throws IOException {
        categoryField.setText("");
    }

    @FXML
    public void productsInStoreButtonButtonOnAction (ActionEvent event) throws IOException {
        clearTable();
        SCREEN = 1;
        loadProductsData();
    }

    @FXML
    public void searchButtonButtonButtonOnAction (ActionEvent event) throws IOException {
        if(categoryField.getText().isBlank()){
            Object[] selectedProduct = productsTable.getSelectionModel().getSelectedItem();
            if (selectedProduct != null) {
                categoryField.setText(selectedProduct[1].toString());
                categoryField.setDisable(false);
                loadProducts("category_number");
            }
        }
        else if(searchOption.getValue().equals("За назвою категорії")) {
            loadProducts("category_name");
        }else if (searchOption.getValue().equals("За номером категорії")){
            loadProducts("category_number");
        }
    }

    private void clearTable() {
        productsTable.getItems().clear();
    }

    private void loadProducts(String option) throws IOException {
        clearTable();
        try {
            DatabaseConnection connection = new DatabaseConnection();
            Connection connectDB = connection.getConnection();
            Statement statement = connectDB.createStatement();
            ResultSet product_information = null;

            // 13. Здійснити пошук усіх товарів, що належать певній категорії, відсортованих за назвою;
            product_information = searchByCategory(option, categoryField.getText());

            while (product_information.next()) {
                Object[] rowData = {
                        product_information.getString("id_product"),
                        product_information.getString("category_number"),
                        product_information.getString("category_name"),
                        product_information.getString("product_name"),
                        product_information.getString("characteristics")
                };
                productsTable.getItems().add(rowData);
            }

            product_information.close();
            statement.close();
            connectDB.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private int SCREEN = 0;
    private void loadProductsData() {
        try {
            DatabaseConnection connection = new DatabaseConnection();
            Connection connectDB = connection.getConnection();
            Statement statement = connectDB.createStatement();
            ResultSet product_information = null;
            if(SCREEN == 0){
                // 9. Отримати інформацію про усі товари, відсортовані за назвою;
                product_information = getProductsInformation_name();
            }else if(SCREEN == 1){
                product_information = getProductsInformation_quantity();
            }

            while (product_information.next()) {
                Object[] rowData = {
                        product_information.getString("id_product"),
                        product_information.getString("category_number"),
                        product_information.getString("category_name"),
                        product_information.getString("product_name"),
                        product_information.getString("characteristics")
                };
                productsTable.getItems().add(rowData);
            }


            product_information.close();
            statement.close();
            connectDB.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void printButtonOnAction(ActionEvent event) {
        // 4. Видруковувати звіти з інформацією про усіх товари
        printReportProduct();
    }
}
