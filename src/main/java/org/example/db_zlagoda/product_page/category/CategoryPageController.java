package org.example.db_zlagoda.product_page.category;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.lowagie.text.pdf.BaseFont;
import javafx.beans.property.SimpleStringProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.print.PrinterJob;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import org.example.db_zlagoda.DatabaseConnection;

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

import static org.example.db_zlagoda.login_page.RegistrationController.generateRandomId;

public class CategoryPageController implements Initializable {
    @FXML
    private TableView<Object[]> categoriesTable;

    @FXML
    private Button addButton, updateButton, deleteButton, doAddButton, doUpdateButton, printButton;

    @FXML
    private TableColumn<Object[], String> categoryNumCol, categoryNameCol;

    @FXML
    private TextField addNameField, updateNameField;

    private String category_number;

    @FXML
    private VBox updateBox, addBox;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        categoryNumCol.setCellValueFactory(cellData -> cellData.getValue()[0] != null ? new SimpleStringProperty(cellData.getValue()[0].toString()) : null);
        categoryNameCol.setCellValueFactory(cellData -> cellData.getValue()[1] != null ? new SimpleStringProperty(cellData.getValue()[1].toString()) : null);

        categoryNumCol.setSortable(false);
        categoryNameCol.setSortable(false);

        loadCategoriesData();

//        updateAddBlock.setVisible(false);
//
        updateButton.setDisable(true);
        deleteButton.setDisable(true);
        addButton.setDisable(false);

        updateBox.setDisable(true);
        addBox.setDisable(true);

        categoriesTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection == null) {
                updateButton.setDisable(true);
                deleteButton.setDisable(true);
            } else {
                updateButton.setDisable(false);
                deleteButton.setDisable(false);
            }
        });
    }

    private void loadCategoriesData() {
        try {
            DatabaseConnection connection = new DatabaseConnection();
            Connection connectDB = connection.getConnection();
            Statement statement = connectDB.createStatement();
            ResultSet category_information = null;
            // 8. Отримати інформацію про усі категорії, відсортовані за назвою;
            category_information = statement.executeQuery(
                    "SELECT * " +
                            "FROM category " +
                            "ORDER BY category_name");

            while (category_information.next()) {
                Object[] rowData = {
                        category_information.getString("category_number"),
                        category_information.getString("category_name")
                };
                categoriesTable.getItems().add(rowData);
            }

            category_information.close();
            statement.close();
            connectDB.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void updateButtonOnAction(ActionEvent event) throws IOException {

        Object[] selectedCustomer = categoriesTable.getSelectionModel().getSelectedItem();
        if (selectedCustomer != null) {
            // 2. Редагувати дані про категорії товарів
            this.category_number = selectedCustomer[0].toString();
            fillFields(category_number);

            addButton.setDisable(true);
            deleteButton.setDisable(true);
            updateButton.setDisable(true);

            updateBox.setDisable(false);

        }
    }

    @FXML
    public void doUpdateButtonOnAction(ActionEvent event) throws IOException {
        boolean allFieldsFilled = true;
        if (updateNameField.getText().isBlank()) {
            updateNameField.setStyle("-fx-border-color: RED; -fx-border-width: 2px");
            updateNameField.setTooltip(new Tooltip("Поле не повинно бути пустим"));
            allFieldsFilled = false;
        } else {
            updateNameField.setStyle("");
            if (updateNameField.getText().length() > 50) {
                updateNameField.setStyle("-fx-border-color: RED; -fx-border-width: 2px");
                updateNameField.setTooltip(new Tooltip("Довжина тексту не повинна перевищувати 50 символів"));
                    allFieldsFilled = false;
            }
        }
        if (allFieldsFilled) {
            updateCategory();
        }
    }

    public void updateCategory(){
        try {
            String newName = updateNameField.getText();

            DatabaseConnection connection = new DatabaseConnection();
            Connection connectDB = connection.getConnection();
            Statement statement = connectDB.createStatement();

            // 2. Редагувати дані про категорії товарів
            String updateQuery = "UPDATE category SET " +
                    "category_name = '" + newName + "' " +
                    "WHERE category_number = '" + category_number + "'";

            statement.executeUpdate(updateQuery);

            statement.close();
            connectDB.close();

            addButton.setDisable(false);
            deleteButton.setDisable(false);
            updateButton.setDisable(false);

            clearTable();
            loadCategoriesData();
            updateBox.setDisable(true);
            cleanField(updateNameField);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void fillFields(String id) {
        try {
            DatabaseConnection connection = new DatabaseConnection();
            Connection connectDB = connection.getConnection();
            Statement statement = connectDB.createStatement();

            ResultSet cust_information = statement.executeQuery(
                    "SELECT *" +
                            "FROM category " +
                            "WHERE category_number = '" + id + "'");

            if (cust_information.next()) {
                updateNameField.setText(cust_information.getString("category_name"));
            }

            statement.close();
            connectDB.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void deleteButtonOnAction (ActionEvent event) throws IOException {
        try{
            Object[] selectedCustomer = categoriesTable.getSelectionModel().getSelectedItem();
            if (selectedCustomer != null) {
                String id = selectedCustomer[0].toString();

                DatabaseConnection connection = new DatabaseConnection();
                Connection connectDB = connection.getConnection();
                Statement statement = connectDB.createStatement();

                // 3. Видаляти дані про категорії товарів
                String checkProductsQuery = "SELECT COUNT(*) FROM product WHERE category_number = '" + id + "'";
                ResultSet resultSet = statement.executeQuery(checkProductsQuery);
                resultSet.next();
                int productCount = resultSet.getInt(1);

                if (productCount > 0) {
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Помилка");
                    alert.setHeaderText(null);
                    alert.setContentText("Неможливо видалити категорію, оскільки вона містить товари.");
                    alert.showAndWait();
                } else {
                    String deleteCategoryQuery = "DELETE FROM category WHERE category_number = '" + id + "'";
                    statement.executeUpdate(deleteCategoryQuery);

                    clearTable();
                    loadCategoriesData();
                }

                resultSet.close();
                statement.close();
                connectDB.close();

                clearTable();
                loadCategoriesData();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void addButtonOnAction (ActionEvent event) throws IOException{
        addButton.setDisable(true);
        deleteButton.setDisable(true);
        updateButton.setDisable(true);

        addBox.setDisable(false);
    }

    @FXML
    public void doAddButtonOnAction(ActionEvent event) {
        boolean allFieldsFilled = true;
        if (addNameField.getText().isBlank()) {
            addNameField.setStyle("-fx-border-color: RED; -fx-border-width: 2px");
            addNameField.setTooltip(new Tooltip("Поле не повинно бути пустим"));
            allFieldsFilled = false;
        } else {
            addNameField.setStyle("");
            if (addNameField.getText().length() > 50) {
                addNameField.setStyle("-fx-border-color: RED; -fx-border-width: 2px");
                addNameField.setTooltip(new Tooltip("Довжина тексту не повинна перевищувати 50 символів"));
                allFieldsFilled = false;
            }
        }
        if (allFieldsFilled) {
            addCategory();
        }
    }

    public void addCategory(){
        try {
            String name = addNameField.getText();

            DatabaseConnection connection = new DatabaseConnection();
            Connection connectDB = connection.getConnection();
            Statement statement = connectDB.createStatement();

            // 1. Додавати нові дані про постійних клієнтів,
            String id_gen = generateRandomId(5, true);

            String insertCategoryQuery = "INSERT INTO category (category_number, category_name) " +
                    "VALUES ('" + id_gen + "', '" + name +  "')";

            statement.executeUpdate(insertCategoryQuery);

            statement.close();
            connectDB.close();

            addButton.setDisable(false);
            deleteButton.setDisable(false);
            updateButton.setDisable(false);

            addBox.setDisable(true);

            clearTable();
            loadCategoriesData();
            cleanField(addNameField);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void cleanField(TextField textField){
        textField.setText("");
    }

    private void clearTable() {
        categoriesTable.getItems().clear();
    }

    @FXML
    public void printButtonOnAction(ActionEvent event) {
        // 4. Видруковувати звіти з інформацією про усіх категорії товарів
        try {
            ResultSet category_information = null;
            category_information = executeQuery(
                    "SELECT * " +
                            "FROM category;");

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
                Paragraph title = new Paragraph("Звіт з інформацією про категорії", font);

                title.setAlignment(Element.ALIGN_CENTER);
                title.setSpacingAfter(10);
                document.add(title);

                PdfPTable table = new PdfPTable(2);
                table.setWidthPercentage(100);

                Stream.of("Номер категорії", "Назва категорії")
                        .forEach(columnTitle -> {
                            PdfPCell header = new PdfPCell();
                            header.setBackgroundColor(BaseColor.LIGHT_GRAY);
                            header.setBorderWidth(1);
                            header.setPhrase(new Phrase(columnTitle, font));
                            table.addCell(header);
                        });

                font.setSize(12);
                font.setStyle(Font.NORMAL);
                while (category_information.next()) {
                    Stream.of(
                            category_information.getString("category_number"),
                            category_information.getString("category_name")
                    ).forEach(data -> {
                        PdfPCell cell = new PdfPCell();
                        cell.setPhrase(new Phrase(data, font));
                        table.addCell(cell);
                    });
                }

                table.completeRow();
                document.add(table);

                document.close();
                category_information.close();
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

    private ResultSet executeQuery(String query) throws SQLException {
        DatabaseConnection connection = new DatabaseConnection();
        Connection connectDB = connection.getConnection();
        Statement statement = connectDB.createStatement();
        return statement.executeQuery(query);
    }

}
