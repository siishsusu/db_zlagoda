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
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ResourceBundle;
import java.util.stream.Stream;

public class ProductInStoreController implements Initializable {
    int SCENE = 0;

    @FXML
    private Button updateButton, addButton, deleteButton,
            showInfoButton,
            allProdButton, promProdButton, no_promProdButton,
            printButton;

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

//        updateBox.setDisable(true);
//        addBox.setDisable(true);

        productsInStoreTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection == null) {
                updateButton.setDisable(true);
                deleteButton.setDisable(true);
            } else {
                updateButton.setDisable(false);
                deleteButton.setDisable(false);

                Object[] selectedProduct = productsInStoreTable.getSelectionModel().getSelectedItem();
                UPC_field.setText(selectedProduct[0].toString());
            }
        });
    }

    private void loadProductsInStore() {
        try {
            DatabaseConnection connection = new DatabaseConnection();
            Connection connectDB = connection.getConnection();
            Statement statement = connectDB.createStatement();
            ResultSet product_store_information = null;
            if (SCENE == 0){
                // 10. Отримати інформацію про усі товари у магазині, відсортовані за кількістю;
                product_store_information = statement.executeQuery(
                        "SELECT UPC, UPC_prom, store_product.id_product, " +
                                "product.product_name, selling_price, " +
                                "product_number, promotional_product " +
                                "FROM store_product " +
                                "INNER JOIN product " +
                                "ON product.id_product = store_product.id_product " +
                                "ORDER BY product_number;");
            } else if (SCENE == 1){
                // 15. Отримати інформацію про усі акційні товари, відсортовані за назвою товару
                product_store_information = statement.executeQuery(
                        "SELECT UPC, UPC_prom, store_product.id_product, " +
                                "product.product_name, selling_price, " +
                                "product_number, promotional_product " +
                                "FROM store_product " +
                                "INNER JOIN product " +
                                "ON product.id_product = store_product.id_product " +
                                "WHERE promotional_product = '1'" +
                                "ORDER BY product_name;");
            } else if (SCENE == 2){
                // 15. Отримати інформацію про усі акційні товари, відсортовані за кількістю одиниць товару
                product_store_information = statement.executeQuery(
                        "SELECT UPC, UPC_prom, store_product.id_product, " +
                                "product.product_name, selling_price, " +
                                "product_number, promotional_product " +
                                "FROM store_product " +
                                "INNER JOIN product " +
                                "ON product.id_product = store_product.id_product " +
                                "WHERE promotional_product = '1'" +
                                "ORDER BY product_number;");
            } else if (SCENE == 3){
                // 16. Отримати інформацію про усі не акційні товари, відсортовані за назвою;
                product_store_information = statement.executeQuery(
                        "SELECT UPC, UPC_prom, store_product.id_product, " +
                                "product.product_name, selling_price, " +
                                "product_number, promotional_product " +
                                "FROM store_product " +
                                "INNER JOIN product " +
                                "ON product.id_product = store_product.id_product " +
                                "WHERE promotional_product = '0'" +
                                "ORDER BY product_name;");
            } else if (SCENE == 4){
                // 16. Отримати інформацію про усі не акційні товари, відсортовані за кількістю одиниць товару
                product_store_information = statement.executeQuery(
                        "SELECT UPC, UPC_prom, store_product.id_product, " +
                                "product.product_name, selling_price, " +
                                "product_number, promotional_product " +
                                "FROM store_product " +
                                "INNER JOIN product " +
                                "ON product.id_product = store_product.id_product " +
                                "WHERE promotional_product = '0'" +
                                "ORDER BY product_number;");
            }

            while (product_store_information.next()) {
                Object[] rowData = {
                        product_store_information.getString("UPC"),
                        product_store_information.getString("UPC_prom"),
                        product_store_information.getString("id_product"),
                        product_store_information.getString("product_name"),
                        product_store_information.getString("selling_price"),
                        product_store_information.getString("product_number"),
                        product_store_information.getString("promotional_product")
                };
                productsInStoreTable.getItems().add(rowData);
            }

            product_store_information.close();
            statement.close();
            connectDB.close();
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
        controller.add();

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
                String deleteProduct = "DELETE FROM store_product WHERE UPC = '" + upc_prod + "'";
                statement.executeUpdate(deleteProduct);

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
        try {
            DatabaseConnection connection = new DatabaseConnection();
            Connection connectDB = connection.getConnection();
            Statement statement = connectDB.createStatement();
            ResultSet product_information = null;
            // 4. Видруковувати звіти з інформацією про усіх товари у магазині
            if (SCENE == 0){
                product_information = statement.executeQuery(
                        "SELECT UPC, UPC_prom, store_product.id_product, " +
                                "product.product_name, product.characteristics, selling_price, " +
                                "product_number, promotional_product " +
                                "FROM store_product " +
                                "INNER JOIN product " +
                                "ON product.id_product = store_product.id_product " +
                                "ORDER BY product_name;");
            } else if (SCENE == 1 || SCENE == 2){
                // 4. Видруковувати звіти з інформацією про усі АКЦІЙНІ товари у магазині
                product_information = statement.executeQuery(
                        "SELECT UPC, UPC_prom, store_product.id_product, " +
                                "product.product_name, product.characteristics, selling_price, " +
                                "product_number, promotional_product " +
                                "FROM store_product " +
                                "INNER JOIN product " +
                                "ON product.id_product = store_product.id_product " +
                                "WHERE promotional_product = '1'" +
                                "ORDER BY product_name;");
            } else if (SCENE == 3 || SCENE == 4){
                // 4. Видруковувати звіти з інформацією про усі НЕАКЦІЙНІ товари у магазині
                product_information = statement.executeQuery(
                        "SELECT UPC, UPC_prom, store_product.id_product, " +
                                "product.product_name, product.characteristics, selling_price, " +
                                "product_number, promotional_product " +
                                "FROM store_product " +
                                "INNER JOIN product " +
                                "ON product.id_product = store_product.id_product " +
                                "WHERE promotional_product = '0'" +
                                "ORDER BY product_name;");
            }

            PrinterJob printerJob = PrinterJob.createPrinterJob();
            if (printerJob != null && printerJob.showPrintDialog(null)) {
                Document document = new Document();
                FileChooser fileChooser = new FileChooser();
                fileChooser.setTitle("Зберегти PDF-файл");
                fileChooser.getExtensionFilters().addAll(
                        new FileChooser.ExtensionFilter("PDF Files", "*.pdf")
                );
                File tempFile = fileChooser.showSaveDialog(null);
                PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream(tempFile));
                document.open();

                Font font = FontFactory.getFont("src/main/resources/fonts/Academy.ttf", BaseFont.IDENTITY_H, true);

                font.setSize(20);
                font.setStyle(Font.BOLD);
                Paragraph title = new Paragraph("Звіт з інформацією про товари в магазині", font);

                title.setAlignment(Element.ALIGN_CENTER);
                title.setSpacingAfter(10);
                document.add(title);

                PdfPTable table = new PdfPTable(8);
                table.setWidthPercentage(100);

                Stream.of("UPC", "UPC акційного товару", "ID товару", "Назва товару", "Характеристики", "Ціна",
                                "Кількість товару", "Акційний")
                        .forEach(columnTitle -> {
                            PdfPCell header = new PdfPCell();
                            header.setBackgroundColor(BaseColor.LIGHT_GRAY);
                            header.setBorderWidth(1);
                            header.setPhrase(new Phrase(columnTitle, font));
                            table.addCell(header);
                        });

                font.setSize(12);
                font.setStyle(Font.NORMAL);
                while (product_information.next()) {
                    Stream.of(
                            product_information.getString("UPC"),
                            product_information.getString("UPC_prom"),
                            product_information.getString("id_product"),
                            product_information.getString("product_name"),
                            product_information.getString("characteristics"),
                            product_information.getString("selling_price"),
                            product_information.getString("product_number"),
                            product_information.getString("promotional_product")
                    ).forEach(data -> {
                        PdfPCell cell = new PdfPCell();
                        cell.setPhrase(new Phrase(data, font));
                        table.addCell(cell);
                    });
                }

                table.completeRow();
                document.add(table);

                document.close();
                product_information.close();
                printerJob.endJob();
            } else {
                System.out.println("Користувач відмінив друк.");
            }
        } catch (SQLException | IOException e) {
            e.printStackTrace();
        } catch (com.itextpdf.text.DocumentException e) {
            throw new RuntimeException(e);
        }
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
            ResultSet prod_store_info = executeQuery(
                    "SELECT product.product_name, product.characteristics, " +
                            "selling_price, product_number " +
                            "FROM store_product " +
                            "INNER JOIN product " +
                            "ON product.id_product = store_product.id_product " +
                            "WHERE UPC = '" + UPC_field.getText() + "'");

            while (prod_store_info.next()) {
                String name = prod_store_info.getString("product_name");
                String characteristics = prod_store_info.getString("characteristics");
                String price = prod_store_info.getString("selling_price");
                String number = prod_store_info.getString("product_number");

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