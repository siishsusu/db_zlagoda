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

import static org.example.db_zlagoda.db_data.DatabaseManager_mm.*;
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
                if (USER_ROLE.equals("Менеджер")){
                    deleteButton.setDisable(false);
                }

                Object[] selectedProduct = customersTable.getSelectionModel().getSelectedItem();
                searchPercentField.setText(selectedProduct[4].toString());
            }
        });

        textFieldList = Arrays.asList(surnameField, nameField, patronymicField, phoneField, cityField, streetField, zipCodeField, percentField);
    }

    private static String USER_ROLE = "";
    public void setRole(String role){
        this.USER_ROLE = role;
    }

    public String getRole() {
        return USER_ROLE;
    }

    private void loadCustomersData(boolean allData) {
        try {
            ResultSet cust_information = null;
            // 7. Отримати інформацію про усіх постійних клієнтів, відсортованих за прізвищем;
            if (allData == true){
                cust_information = getCustomersInformation();
            } else {
                // 12. Отримати інформацію про усіх постійних клієнтів, що мають карту клієнта із певним
                // відсотком, посортованих за прізвищем;
                cust_information = getCustomersInformation_byPercent(searchPercentField.getText());
            }

            while (cust_information.next()) {
                Object[] rowData = {
                        cust_information.getString("card_number"),
                        cust_information.getString("pib"),
                        cust_information.getString("phone_number"),
                        cust_information.getString("address"),
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
        printReportCustomers();
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
            String query = "SELECT * FROM customer_card WHERE card_number = ?";
            try (Connection connectDB = DatabaseConnection.getConnection();
                 PreparedStatement statement = connectDB.prepareStatement(query)) {

                statement.setString(1, id);
                ResultSet cust_information = statement.executeQuery();

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
            }

        } catch (SQLException e) {
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
            update();
        }
    }
    public void update(){
        try {
            String newLastName = surnameField.getText();
            String newFirstName = nameField.getText();
            String newPatronymic = patronymicField.getText();
            String newPhone = phoneField.getText();
            String newCity = cityField.getText();
            String newStreet = streetField.getText();
            String newZipCode = zipCodeField.getText();
            String newPercent = percentField.getText();

            updateCustomer(id, newLastName, newFirstName, newPatronymic, newPhone,
                    newCity, newStreet, newZipCode, newPercent);

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

                // 3. Видаляти дані про постійних клієнтів
                deleteCustomer(id);

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
            add();
        }
    }

    public void add(){
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

            insertCustomer(id_gen, newLastName, newFirstName, newPatronymic, newPhone, newCity,
                    newStreet, newZipCode, newPercent);

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
