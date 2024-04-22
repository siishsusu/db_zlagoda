package org.example.db_zlagoda.checks_page;

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
import org.controlsfx.control.textfield.TextFields;
import org.example.db_zlagoda.DatabaseConnection;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.ResourceBundle;
import java.util.stream.Stream;

public class ChecksListController implements Initializable {
    @FXML
    private Button showInfoButton, showAllChecksButton, checkInfoButton, printButton, deleteButton;

    @FXML
    private TableView<Object[]> checkInfoTable;

    @FXML
    private TableColumn<Object[], String> check_numberCol, id_employeeCol, card_numberCol, print_dateCol,
            sum_totalCol, vatCol;

    @FXML
    private TextField employee_field;

    @FXML
    private ComboBox employee_product_box;

    @FXML
    private Label infoLabel, price_number_label, nameLabel, periodLabel;

    @FXML
    private DatePicker startDatePicker, endDatePicker;

    int SCENE = 0; // 0 - всі чеки за період (всі касири)
    // 1 - всі чеки за період (певний касир)
    // 2 - всі чеки в яких міститься певний товар

    @FXML
    public void deleteButtonOnAction(ActionEvent event){
        try{
            Object[] selectedCheck = checkInfoTable.getSelectionModel().getSelectedItem();
            if (selectedCheck != null) {
                String checkNum = selectedCheck[0].toString();

                DatabaseConnection connection = new DatabaseConnection();
                Connection connectDB = connection.getConnection();
                Statement statement = connectDB.createStatement();

                // 3. Видаляти дані про чеки
                String deleteProduct = "DELETE FROM `check` WHERE check_number = '" + checkNum + "'";
                statement.executeUpdate(deleteProduct);

                statement.close();
                connectDB.close();
            }

            checkInfoTable.getItems().clear();
            try {
                loadTableInfo();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void showInfoEmployeesButtonOnAction(ActionEvent event) {
        checkInfoTable.getItems().clear();
        findAndSetTotalPrice(false);
        SCENE = 1;
        try {
            loadTableInfo();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @FXML
    public void showAllChecksButtonOnAction(ActionEvent event){
        checkInfoTable.getItems().clear();
        findAndSetTotalPrice(true);
        try {
            nameLabel.setText("всі чеки");
            SCENE = 0;
            try {
                loadTableInfo();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void findAndSetTotalPrice(boolean allEmployees){
        try {
            DatabaseConnection connection = new DatabaseConnection();
            Connection connectDB = connection.getConnection();
            Statement statement = connectDB.createStatement();
            // 20. Визначити загальну суму проданих товарів з чеків, створених усіма касиром за певний період часу;
            if (allEmployees){
                ResultSet totalPrice_q = statement.executeQuery(
                        "SELECT SUM(sum_total) AS total_sales_price " +
                                "FROM `check` " +
                                "WHERE print_date " +
                                "BETWEEN '" + startDatePicker.getValue() + "' " +
                                "AND '" + endDatePicker.getValue() + "'"
                );
                if(totalPrice_q.next()){
                    String price = totalPrice_q.getString("total_sales_price");
                    if (price == null){
                        price_number_label.setText("0 грн");
                    } else {
                        price_number_label.setText(price + " грн");
                    }
                }
            } else {
                // 19. Визначити загальну суму проданих товарів з чеків, створених певним касиром за певний період часу;
                ResultSet totalPrice_q = statement.executeQuery(
                        "SELECT SUM(sum_total) AS total_sales_price " +
                                "FROM `check` " +
                                "WHERE id_employee = '" + employee_field.getText() + "' " +
                                "AND print_date " +
                                "BETWEEN '" + startDatePicker.getValue() + "' " +
                                "AND '" + endDatePicker.getValue() + "'"
                );
                if(totalPrice_q.next()){
                    String price = totalPrice_q.getString("total_sales_price");
                    if (price == null){
                        price_number_label.setText("0 грн");
                    } else {
                        price_number_label.setText(price + " грн");
                    }
                }
            }
        }catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void findAndSetTotalCount(){
        try {
            // 21. Визначити загальну кількість одиниць певного товару, проданого за певний період часу.
            DatabaseConnection connection = new DatabaseConnection();
            Connection connectDB = connection.getConnection();
            Statement statement = connectDB.createStatement();
            ResultSet totalNumber_q = statement.executeQuery(
                    "SELECT SUM(s.product_number) AS total_quantity_sold " +
                            "FROM `check` c " +
                            "INNER JOIN sale s " +
                            "ON c.check_number = s.check_number " +
                            "WHERE s.UPC = '" + employee_field.getText() + "' AND print_date " +
                            "BETWEEN '" + startDatePicker.getValue() + "' " +
                            "AND '" + endDatePicker.getValue() + "'"
            );
            if(totalNumber_q.next()){
                String quantitySold = totalNumber_q.getString("total_quantity_sold");
                if (quantitySold == null){
                    price_number_label.setText("0 шт");
                } else {
                    price_number_label.setText(quantitySold + " шт");
                }
            }
        }catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void checkInfoButtonOnAction(ActionEvent event){
        Object[] selectedProduct = checkInfoTable.getSelectionModel().getSelectedItem();
        if (selectedProduct != null) {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/db_zlagoda/checks_page/check-full-view.fxml"));
                Parent root = loader.load();

                CheckViewController controller = loader.getController();
                controller.fillCheck(selectedProduct[0].toString());

                Stage stage = new Stage();
                stage.setScene(new Scene(root));
                stage.show();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @FXML
    public void showInfoProductButtonOnAction(ActionEvent event){
        checkInfoTable.getItems().clear();
        findAndSetTotalCount();
        SCENE = 2;
        try {
            loadTableInfo();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        ObservableList<String> items = FXCollections.observableArrayList("Пошук за id працівника",
                "Пошук за UPC товару");
        employee_product_box.setItems(items);

        employee_product_box.valueProperty().addListener((observable, oldValue, newValue) -> {
            if(newValue != null){
                if(newValue.equals("Пошук за id працівника")){
                    employee_field.setPromptText("Введіть id касира");
                    initializeEmployee();
                    infoLabel.setText("Загальна сума проданих товарів:");
                    price_number_label.setText("0.0000 грн");
                    showInfoButton.setOnAction(this::showInfoEmployeesButtonOnAction);
                } else if(newValue.equals("Пошук за UPC товару")){
                    employee_field.setPromptText("Введіть UPC товару");
                    initializeProducts();
                    infoLabel.setText("Загальна кількість проданого товару:");
                    price_number_label.setText("0 шт");
                    showInfoButton.setOnAction(this::showInfoProductButtonOnAction);
                }
            }
        });

        employee_product_box.setValue(items.get(0));

        checkInfoTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if(newSelection != null){
                checkInfoButton.setDisable(false);
                deleteButton.setDisable(false);
            }
        });

        startDatePicker.setValue(LocalDate.now().minusDays(1));
        endDatePicker.setValue(LocalDate.now());

        check_numberCol.setCellValueFactory(cellData -> cellData.getValue()[0] != null ? new SimpleStringProperty(cellData.getValue()[0].toString()) : null);
        id_employeeCol.setCellValueFactory(cellData -> cellData.getValue()[1] != null ? new SimpleStringProperty(cellData.getValue()[1].toString()) : null);
        card_numberCol.setCellValueFactory(cellData -> cellData.getValue()[2] != null ? new SimpleStringProperty(cellData.getValue()[2].toString()) : null);
        print_dateCol.setCellValueFactory(cellData -> cellData.getValue()[3] != null ? new SimpleStringProperty(cellData.getValue()[3].toString()) : null);
        sum_totalCol.setCellValueFactory(cellData -> cellData.getValue()[4] != null ? new SimpleStringProperty(cellData.getValue()[4].toString()) : null);
        vatCol.setCellValueFactory(cellData -> cellData.getValue()[5] != null ? new SimpleStringProperty(cellData.getValue()[5].toString()) : null);
    }

    private void initializeEmployee(){
        try {
            DatabaseConnection connection = new DatabaseConnection();
            Connection connectDB = connection.getConnection();
            Statement statement = connectDB.createStatement();
            // Витягує id працівників для "розумного пошуку"
            ResultSet employees_information = statement.executeQuery(
                    "SELECT id_employee " +
                            "FROM employee");
            ArrayList ids = new ArrayList<>();
            while (employees_information.next()) {
                ids.add(employees_information.getString("id_employee"));
            }

            TextFields.bindAutoCompletion(employee_field, ids);

            employee_field.textProperty().addListener((observable, oldValue, newValue) -> {
                if (!newValue.isBlank()) {
                    checkTime();
                    findEmployee();
                } else {
                    showInfoButton.setDisable(true);
                }
            });

            startDatePicker.valueProperty().addListener((observable, oldValue, newValue) -> {
                checkTime();
            });

            endDatePicker.valueProperty().addListener((observable, oldValue, newValue) -> {
                checkTime();
            });

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");

    private void checkTime() {
        LocalDate startDate = startDatePicker.getValue();
        LocalDate endDate = endDatePicker.getValue();

        if (startDate != null && endDate != null) {
            String formattedStartDate = startDate.format(formatter);
            String formattedEndDate = endDate.format(formatter);

            LocalDate threeYearsAgo = LocalDate.now().minusYears(3);

            if (startDate.isAfter(threeYearsAgo)){
                startDatePicker.setTooltip(new Tooltip(""));
                startDatePicker.setStyle("");
                if (endDate.isAfter(startDate)) {
                    if (!employee_field.getText().equals("")){
                        showInfoButton.setDisable(false);
                    }
                    showAllChecksButton.setDisable(false);

                    endDatePicker.setTooltip(new Tooltip(""));
                    endDatePicker.setStyle("");
                } else {
                    endDatePicker.setStyle("-fx-border-color: RED; -fx-border-width: 2px");
                    endDatePicker.setTooltip(new Tooltip("Дата завершення не може бути перед датою початку"));
                    showAllChecksButton.setDisable(true);
                    showInfoButton.setDisable(true);
                }
            } else {
                startDatePicker.setStyle("-fx-border-color: RED; -fx-border-width: 2px");
                startDatePicker.setTooltip(new Tooltip("У базі зберігають чеки за 3 роки"));
                showAllChecksButton.setDisable(true);
                showInfoButton.setDisable(true);
            }

            String periodString = "Період часу: " + formattedStartDate + " - " + formattedEndDate;
            periodLabel.setText(periodString);
        }
    }

    private void findEmployee(){
        try {
            DatabaseConnection connection = new DatabaseConnection();
            Connection connectDB = connection.getConnection();
            Statement statement = connectDB.createStatement();
            // Витягує id працівників для "розумного пошуку"
            ResultSet employees_information = statement.executeQuery(
                    "SELECT CONCAT(empl_surname, ' ', empl_name, ' ', empl_patronymic) AS PIB " +
                            "FROM employee WHERE id_employee = '" + employee_field.getText() + "'");
            String pib = "";
            while (employees_information.next()) {
                pib = (employees_information.getString("PIB"));
            }
            nameLabel.setText(pib);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void initializeProducts(){
        try {
            DatabaseConnection connection = new DatabaseConnection();
            Connection connectDB = connection.getConnection();
            Statement statement = connectDB.createStatement();
            // Витягує UPC товарів для "розумного пошуку"
            ResultSet products_information = statement.executeQuery(
                    "SELECT UPC " +
                            "FROM store_product");
            ArrayList upcs = new ArrayList<>();
            while (products_information.next()) {
                upcs.add(products_information.getString("UPC"));
            }

            TextFields.bindAutoCompletion(employee_field, upcs);

            employee_field.textProperty().addListener((observable, oldValue, newValue) -> {
                if (!newValue.isBlank()) {
                    checkTime();
                    findProduct();
                } else {
                    showInfoButton.setDisable(true);
                }
            });

            startDatePicker.valueProperty().addListener((observable, oldValue, newValue) -> {
                checkTime();
            });

            endDatePicker.valueProperty().addListener((observable, oldValue, newValue) -> {
                checkTime();
            });

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void findProduct(){
        try {
            DatabaseConnection connection = new DatabaseConnection();
            Connection connectDB = connection.getConnection();
            Statement statement = connectDB.createStatement();
            ResultSet products_information = statement.executeQuery(
                    "SELECT * FROM store_product " +
                            "INNER JOIN product " +
                            "ON product.id_product = store_product.id_product " +
                            "WHERE UPC = '" + employee_field.getText() + "'");
            String nameOfProduct = "", promotion = "";
            while (products_information.next()) {
                nameOfProduct = (products_information.getString("product_name"));
                promotion = (products_information.getString("promotional_product"));
            }
            if(promotion.equals("1")){
                nameOfProduct += " (акція)";
            }
            nameLabel.setText(nameOfProduct);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @FXML
    public void printButtonOnAction(ActionEvent event) throws IOException {
        try {
            DatabaseConnection connection = new DatabaseConnection();
            Connection connectDB = connection.getConnection();
            Statement statement = connectDB.createStatement();
            ResultSet check_information = null;
            // 4. Видруковувати звіти з інформацією про усіх чеки;
            check_information = statement.executeQuery(
                    "SELECT * FROM `check`;");

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
                Paragraph title = new Paragraph("Звіт з інформацією про чеки", font);

                title.setAlignment(Element.ALIGN_CENTER);
                title.setSpacingAfter(10);
                document.add(title);

                PdfPTable table = new PdfPTable(6);
                table.setWidthPercentage(100);

                Stream.of("Номер чеку", "id працівника", "Номер карти клієнта", "Дата друку", "Загальна сума", "ПДВ")
                        .forEach(columnTitle -> {
                            PdfPCell header = new PdfPCell();
                            header.setBackgroundColor(BaseColor.LIGHT_GRAY);
                            header.setBorderWidth(1);
                            header.setPhrase(new Phrase(columnTitle, font));
                            table.addCell(header);
                        });

                font.setSize(12);
                font.setStyle(Font.NORMAL);
                while (check_information.next()) {
                    Stream.of(
                            check_information.getString("check_number"),
                            check_information.getString("id_employee"),
                            check_information.getString("card_number"),
                            check_information.getString("print_date"),
                            check_information.getString("sum_total"),
                            check_information.getString("vat")
                    ).forEach(data -> {
                        PdfPCell cell = new PdfPCell();
                        cell.setPhrase(new Phrase(data, font));
                        table.addCell(cell);
                    });
                }

                table.completeRow();
                document.add(table);

                document.close();
                check_information.close();
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

    private void loadTableInfo() throws SQLException {
        ResultSet checks_information = null;
        DatabaseConnection connection = new DatabaseConnection();
        Connection connectDB = connection.getConnection();
        Statement statement = connectDB.createStatement();
        try {
            if (SCENE == 0){
                // 18. Отримати інформацію про усі чеки, створені усіма касирами за певний період
                // часу (з можливістю перегляду куплених товарів у цьому чеку, їх назва, к-сті та ціни);
                checks_information = statement.executeQuery(
                        "SELECT * " +
                                "FROM `check` " +
                                "WHERE print_date BETWEEN '" + startDatePicker.getValue() + "' " +
                                "AND '" + endDatePicker.getValue() + "'");
            } else if (SCENE == 1){
                // 17. Отримати інформацію про усі чеки, створені певним касиром за певний період
                // часу (з можливістю перегляду куплених товарів у цьому чеку, їх назви, к-сті та ціни);
                checks_information = statement.executeQuery(
                        "SELECT * " +
                                "FROM `check` " +
                                "WHERE id_employee = '" + employee_field.getText() + "' " +
                                "AND print_date BETWEEN '" + startDatePicker.getValue() + "' " +
                                "AND '" + endDatePicker.getValue() + "'");
            } else if (SCENE == 2){
                checks_information = statement.executeQuery(
                        "SELECT * " +
                                "FROM `check` " +
                                "INNER JOIN sale ON sale.check_number = `check`.check_number " +
                                "WHERE UPC = '" + employee_field.getText() + "' " +
                                "AND print_date BETWEEN '" + startDatePicker.getValue() + "' " +
                                "AND '" + endDatePicker.getValue() + "'");
            }

            while (checks_information.next()) {
                Object[] rowData = {
                        checks_information.getString("check_number"),
                        checks_information.getString("id_employee"),
                        checks_information.getString("card_number"),
                        checks_information.getString("print_date"),
                        checks_information.getString("sum_total"),
                        checks_information.getString("vat")
                };
                checkInfoTable.getItems().add(rowData);
            }

        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
