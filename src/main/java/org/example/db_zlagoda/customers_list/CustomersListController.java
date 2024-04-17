package org.example.db_zlagoda.customers_list;

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
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;

import java.io.File;
import java.io.FileOutputStream;
import java.sql.*;
import java.util.Arrays;

import javafx.stage.FileChooser;
import org.example.db_zlagoda.DatabaseConnection;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Stream;

import static org.example.db_zlagoda.login_page.RegistrationController.generateRandomId;

public class CustomersListController implements Initializable {
    @FXML
    private AnchorPane containerMain, table_and_buttons;

    @FXML
    private TableView<Object[]> customersTable;

    @FXML
    private TableColumn<Object[], String> idColumn;

    @FXML
    private TableColumn<Object[], String> PIBColumn;

    @FXML
    private TableColumn<Object[], String> phoneColumn;

    @FXML
    private TableColumn<Object[], String> addressColumn;

    @FXML
    private TableColumn<Object[], String> percentColumn;

    @FXML
    private Button updateButton, deleteButton, addButton, doUpdateButton;

    private List<TextField> textFieldList;

    private String id;
    @FXML
    private Label infoLabel;

    @FXML
    private BorderPane updateAddBlock;

    @FXML
    private TextField searchPercentField;

    @FXML
    private Button searchButton, clearFieldButton, printThisCustomerButton, printButton;

    @FXML
    private TextField surnameField, nameField, patronymicField, phoneField, cityField, streetField, zipCodeField, percentField;
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        idColumn.setCellValueFactory(cellData -> cellData.getValue()[0] != null ? new SimpleStringProperty(cellData.getValue()[0].toString()) : null);
        PIBColumn.setCellValueFactory(cellData -> cellData.getValue()[1] != null ? new SimpleStringProperty(cellData.getValue()[1].toString()) : null);
        phoneColumn.setCellValueFactory(cellData -> cellData.getValue()[2] != null ? new SimpleStringProperty(cellData.getValue()[2].toString()) : null);
        addressColumn.setCellValueFactory(cellData -> cellData.getValue()[3] != null ? new SimpleStringProperty(cellData.getValue()[3].toString()) : null);
        percentColumn.setCellValueFactory(cellData -> cellData.getValue()[4] != null ? new SimpleStringProperty(cellData.getValue()[4].toString()) : null);

        idColumn.setSortable(false);
        PIBColumn.setSortable(false);
        phoneColumn.setSortable(false);
        addressColumn.setSortable(false);
        percentColumn.setSortable(false);

        loadCustomersData(true);

        updateAddBlock.setVisible(false);

        updateButton.setDisable(true);
        deleteButton.setDisable(true);
        addButton.setDisable(false);

        customersTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection == null) {
                updateButton.setDisable(true);
                deleteButton.setDisable(true);
            } else {
                updateButton.setDisable(false);
                deleteButton.setDisable(false);
            }
        });

        textFieldList = Arrays.asList(surnameField, nameField, patronymicField, phoneField, cityField, streetField, zipCodeField, percentField);
    }

    private void loadCustomersData(boolean allData) {
        try {
            ResultSet cust_information = null;
            // 7. Отримати інформацію про усіх постійних клієнтів, відсортованих за прізвищем;
            if (allData == true){
                cust_information = executeQuery(
                        "SELECT card_number, " +
                                "CONCAT(cust_surname, ' ', cust_name, ' ', cust_patronymic) AS ПІБ, " +
                                "phone_number, " +
                                "CONCAT('м. ', city, ', вул. ', street, ', поштовий індекс : ', zip_code, percent) AS Адреса, " +
                                "percent " +
                                "FROM customer_card " +
                                "ORDER BY cust_surname");
            } else {
                // 12. Отримати інформацію про усіх постійних клієнтів, що мають карту клієнта із певним
                // відсотком, посортованих за прізвищем;
                cust_information = executeQuery(
                        "SELECT card_number, " +
                                "CONCAT(cust_surname, ' ', cust_name, ' ', cust_patronymic) AS ПІБ, " +
                                "phone_number, " +
                                "CONCAT('м. ', city, ', вул. ', street, ', поштовий індекс : ', zip_code, percent) AS Адреса, " +
                                "percent " +
                                "FROM customer_card " +
                                "WHERE percent = " + "'" + searchPercentField.getText() + "'" +
                                "ORDER BY cust_surname");
            }

            while (cust_information.next()) {
                Object[] rowData = {
                        cust_information.getString("card_number"),
                        cust_information.getString("ПІБ"),
                        cust_information.getString("phone_number"),
                        cust_information.getString("Адреса"),
                        cust_information.getString("percent")
                };
                customersTable.getItems().add(rowData);
            }

            cust_information.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void printButtonOnAction(ActionEvent event) throws IOException {
        try {
            ResultSet customers_information = null;
            // 4. Видруковувати звіти з інформацією про усіх постійних клієнтів
            customers_information = executeQuery(
                    "SELECT card_number, " +
                            "CONCAT(cust_surname, ' ', cust_name, ' ', cust_patronymic) AS PIB, " +
                            "phone_number, " +
                            "CONCAT('м. ', city, ' вул. ', street, ' поштовий індекс: ', zip_code) AS address, " +
                            "percent " +
                            "FROM customer_card;");

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
                Paragraph title = new Paragraph("Звіт з інформацією про постійних клієнтів", font);

                title.setAlignment(Element.ALIGN_CENTER);
                title.setSpacingAfter(10);
                document.add(title);

                PdfPTable table = new PdfPTable(5);
                table.setWidthPercentage(100);

                Stream.of("Номер карти", "ПІБ", "Номер телефону", "Адреса", "Відсоток знижки")
                        .forEach(columnTitle -> {
                            PdfPCell header = new PdfPCell();
                            header.setBackgroundColor(BaseColor.LIGHT_GRAY);
                            header.setBorderWidth(1);
                            header.setPhrase(new Phrase(columnTitle, font));
                            table.addCell(header);
                        });

                font.setSize(12);
                font.setStyle(Font.NORMAL);
                while (customers_information.next()) {
                    Stream.of(
                            customers_information.getString("card_number"),
                            customers_information.getString("pib"),
                            customers_information.getString("phone_number"),
                            customers_information.getString("address"),
                            customers_information.getString("percent")
                    ).forEach(data -> {
                        PdfPCell cell = new PdfPCell();
                        cell.setPhrase(new Phrase(data, font));
                        table.addCell(cell);
                    });
                }

                table.completeRow();
                document.add(table);

                document.close();
                customers_information.close();
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
    public void printThisCustomerButtonOnAction(ActionEvent event) throws IOException {

    }

    @FXML
    public void updateButtonOnAction(ActionEvent event) throws IOException {
        Object[] selectedCustomer = customersTable.getSelectionModel().getSelectedItem();
        if (selectedCustomer != null) {
            this.id = selectedCustomer[0].toString();
            fillFields(id);

            updateAddBlock.setVisible(true);

            addButton.setDisable(true);
            deleteButton.setDisable(true);
            updateButton.setDisable(true);

            infoLabel.setText("Оновлення інформації");
            doUpdateButton.setText("Оновити");
            doUpdateButton.setOnAction(this::doUpdateButtonOnAction);
        }
    }

    private void fillFields(String id) {
        try {
            ResultSet cust_information = executeQuery(
                    "SELECT *" +
                            "FROM customer_card " +
                            "WHERE card_number = '" + id + "'");

            if (cust_information.next()) {
                surnameField.setText(cust_information.getString("cust_surname"));
                nameField.setText(cust_information.getString("cust_name"));
                patronymicField.setText(cust_information.getString("cust_patronymic"));
                phoneField.setText(cust_information.getString("phone_number"));
                cityField.setText(cust_information.getString("city"));
                streetField.setText(cust_information.getString("street"));
                zipCodeField.setText(cust_information.getString("zip_code"));
                percentField.setText(cust_information.getString("percent"));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void doUpdateButtonOnAction(ActionEvent event) {
        boolean allFieldsFilled = true;
        for (TextField textField : textFieldList) {
            if (textField.getText().isBlank()) {
                textField.setStyle("-fx-border-color: RED; -fx-border-width: 2px");
                textField.setTooltip(new Tooltip("Поле не повинно бути пустим"));
                allFieldsFilled = false;
            } else {
                textField.setStyle("");
                if (textField == nameField || textField == surnameField || textField == patronymicField || textField == cityField
                        || textField == streetField) {
                    if (textField.getText().length() > 50) {
                        textField.setStyle("-fx-border-color: RED; -fx-border-width: 2px");
                        textField.setTooltip(new Tooltip("Довжина тексту не повинна перевищувати 50 символів"));
                        allFieldsFilled = false;
                    } else {
                        textField.setTooltip(new Tooltip(""));
                        textField.setStyle("");
                    }
                }

                if (textField == zipCodeField) {
                    if (textField.getText().length() > 9) {
                        textField.setStyle("-fx-border-color: RED; -fx-border-width: 2px");
                        textField.setTooltip(new Tooltip("Поле повинно містити менше 9 символів."));
                        allFieldsFilled = false;
                    } else {
                        textField.setTooltip(new Tooltip(""));
                        textField.setStyle("");
                    }
                }

                if (textField == phoneField) {
                    if (textField.getText().length() == 13 && textField.getText().startsWith("+380")) {
                        textField.setTooltip(new Tooltip(""));
                        textField.setStyle("");
                    } else {
                        textField.setStyle("-fx-border-color: RED; -fx-border-width: 2px");
                        textField.setTooltip(new Tooltip("Номер телефону повинен складатися з 13 символів  і починатися з \"+380\"."));
                        allFieldsFilled = false;
                    }
                }
            }
        }
        if (allFieldsFilled) {
            updateCustomer();
        }
    }
    public void updateCustomer(){
        try {
            String newLastName = surnameField.getText();
            String newFirstName = nameField.getText();
            String newPatronymic = patronymicField.getText();
            String newPhone = phoneField.getText();
            String newCity = cityField.getText();
            String newStreet = streetField.getText();
            String newZipCode = zipCodeField.getText();
            String newPercent = percentField.getText();

            DatabaseConnection connection = new DatabaseConnection();
            Connection connectDB = connection.getConnection();
            Statement statement = connectDB.createStatement();

            // 2. Редагувати дані про постійних клієнтів
            String updateQuery = "UPDATE customer_card SET " +
                    "cust_surname = '" + newLastName + "', " +
                    "cust_name = '" + newFirstName + "', " +
                    "cust_patronymic = '" + newPatronymic + "', " +
                    "phone_number = '" + newPhone + "', " +
                    "city = '" + newCity + "', " +
                    "street = '" + newStreet + "', " +
                    "zip_code = '" + newZipCode + "', " +
                    "percent = '" + newPercent + "' " +
                    "WHERE card_number = '" + id + "'";

            statement.executeUpdate(updateQuery);

            statement.close();
            connectDB.close();
            updateAddBlock.setVisible(false);

            addButton.setDisable(false);
            deleteButton.setDisable(false);
            updateButton.setDisable(false);

            clearTable();
            loadCustomersData(true);
            cleanFields();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void clearTable() {
        customersTable.getItems().clear();
    }

    @FXML
    public void deleteButtonOnAction (ActionEvent event) throws IOException{
        try{
            Object[] selectedCustomer = customersTable.getSelectionModel().getSelectedItem();
            if (selectedCustomer != null) {
                String id = selectedCustomer[0].toString();

                DatabaseConnection connection = new DatabaseConnection();
                Connection connectDB = connection.getConnection();
                Statement statement = connectDB.createStatement();

                // 3. Видаляти дані про постійних клієнтів
                String deleteCust = "DELETE FROM customer_card WHERE card_number = '" + id + "'";
                statement.executeUpdate(deleteCust);

                statement.close();
                connectDB.close();

                clearTable();
                loadCustomersData(true);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void cleanFields(){
        for (TextField textField : textFieldList) {
            textField.setText("");
        }
    }

    @FXML
    public void addButtonOnAction (ActionEvent event) throws IOException{
        updateAddBlock.setVisible(true);

        addButton.setDisable(true);
        deleteButton.setDisable(true);
        updateButton.setDisable(true);

        infoLabel.setText("Додавання клієнта");
        doUpdateButton.setText("Додати");
        doUpdateButton.setOnAction(this::doAddButtonOnAction);
    }

    @FXML
    public void doAddButtonOnAction(ActionEvent event) {
        boolean allFieldsFilled = true;
        for (TextField textField : textFieldList) {
            if (textField.getText().isBlank()) {
                textField.setStyle("-fx-border-color: RED; -fx-border-width: 2px");
                textField.setTooltip(new Tooltip("Поле не повинно бути пустим"));
                allFieldsFilled = false;
            } else {
                textField.setStyle("");
                if (textField == nameField || textField == surnameField || textField == patronymicField || textField == cityField
                        || textField == streetField) {
                    if (textField.getText().length() > 50) {
                        textField.setStyle("-fx-border-color: RED; -fx-border-width: 2px");
                        textField.setTooltip(new Tooltip("Довжина тексту не повинна перевищувати 50 символів"));
                        allFieldsFilled = false;
                    } else {
                        textField.setTooltip(new Tooltip(""));
                        textField.setStyle("");
                    }
                }

                if (textField == zipCodeField) {
                    if (textField.getText().length() > 9) {
                        textField.setStyle("-fx-border-color: RED; -fx-border-width: 2px");
                        textField.setTooltip(new Tooltip("Поле повинно містити менше 9 символів."));
                        allFieldsFilled = false;
                    } else {
                        textField.setTooltip(new Tooltip(""));
                        textField.setStyle("");
                    }
                }

                if (textField == phoneField) {
                    if (textField.getText().length() == 13 && textField.getText().startsWith("+380")) {
                        textField.setTooltip(new Tooltip(""));
                        textField.setStyle("");
                    } else {
                        textField.setStyle("-fx-border-color: RED; -fx-border-width: 2px");
                        textField.setTooltip(new Tooltip("Номер телефону повинен складатися з 13 символів  і починатися з \"+380\"."));
                        allFieldsFilled = false;
                    }
                }
            }
        }
        if (allFieldsFilled) {
            addCustomer();
        }
    }

    public void addCustomer(){
        try {
            String newLastName = surnameField.getText();
            String newFirstName = nameField.getText();
            String newPatronymic = patronymicField.getText();
            String newPhone = phoneField.getText();
            String newCity = cityField.getText();
            String newStreet = streetField.getText();
            String newZipCode = zipCodeField.getText();
            String newPercent = percentField.getText();

            // 1. Додавати нові дані про постійних клієнтів,
            String id_gen = generateRandomId(10, false);

            DatabaseConnection connection = new DatabaseConnection();
            Connection connectDB = connection.getConnection();
            Statement statement = connectDB.createStatement();

            String insertCustQuery = "INSERT INTO customer_card (card_number, cust_surname, cust_name, cust_patronymic, phone_number, city, street, zip_code, percent) " +
                    "VALUES ('" + id_gen + "', '" + newLastName + "', '" + newFirstName + "', '" + newPatronymic + "', '" + newPhone + "', '" + newCity + "', '" + newStreet + "', '" +
                    newZipCode + "', '" + newPercent +  "')";

            statement.executeUpdate(insertCustQuery);

            updateAddBlock.setVisible(false);

            addButton.setDisable(false);
            deleteButton.setDisable(false);
            updateButton.setDisable(false);

            clearTable();
            loadCustomersData(true);
            cleanFields();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void clearFieldButtonButtonOnAction (ActionEvent event) throws IOException {
        searchPercentField.setText("");
    }

    @FXML
    public void searchButtonButtonButtonOnAction (ActionEvent event) throws IOException{
        clearTable();
        if(searchPercentField.getText().isBlank()){
            loadCustomersData(true);
            searchButton.setText("Знайти");
        } else{
            loadCustomersData(false);
            searchButton.setText("Всі клієнти");
            searchPercentField.setText("");
        }
    }

    private ResultSet executeQuery(String query) throws SQLException {
        DatabaseConnection connection = new DatabaseConnection();
        Connection connectDB = connection.getConnection();
        Statement statement = connectDB.createStatement();
        return statement.executeQuery(query);
    }
}
