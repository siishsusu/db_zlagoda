package org.example.db_zlagoda.employees_list;

import com.itextpdf.text.*;
import com.itextpdf.text.Font;
import com.lowagie.text.DocumentException;
import com.lowagie.text.pdf.BaseFont;
import javafx.beans.property.SimpleStringProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.print.PrinterJob;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDType0Font;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.controlsfx.control.textfield.TextFields;
import org.example.db_zlagoda.DatabaseConnection;
import org.example.db_zlagoda.create_reports.PDFCreator;
import org.example.db_zlagoda.update_employee_page.UpdateEmployeeController;

import com.itextpdf.text.Document;
import com.itextpdf.text.Rectangle;

import com.itextpdf.text.Font;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.text.FontFactory;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;

import javax.print.*;
import javax.print.attribute.HashPrintRequestAttributeSet;
import java.awt.*;
import java.io.*;
import java.net.URL;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Stream;

public class EmployeesListController implements Initializable {

    private int SCREEN = 0;

    @FXML
    private TableView<Object[]> employeesTable;

    @FXML
    private TableColumn<Object[], String> idColumn;

    @FXML
    private TableColumn<Object[], String> surnameColumn;

    @FXML
    private TableColumn<Object[], String> nameColumn;

    @FXML
    private TableColumn<Object[], String> patronymicColumn;

    @FXML
    private TableColumn<Object[], String> roleColumn;

    @FXML
    private TableColumn<Object[], String> salaryColumn;

    @FXML
    private TableColumn<Object[], String> dobColumn;

    @FXML
    private TableColumn<Object[], String> startColumn;

    @FXML
    private TableColumn<Object[], String> phoneColumn;

    @FXML
    private TableColumn<Object[], String> cityColumn;

    @FXML
    private TableColumn<Object[], String> streetColumn;

    @FXML
    private TableColumn<Object[], String> zipColumn;

    @FXML
    private Button updateButton, deleteButton, cashiersButton, allEmployeesButton, addButton, printButton, printThisEmployeeButton;
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        printButton.setTooltip(new Tooltip("Друкувати звіт про працівників"));
        printThisEmployeeButton.setTooltip(new Tooltip("Друкувати звіт про обраного працівника"));

        idColumn.setCellValueFactory(cellData -> cellData.getValue()[0] != null ? new SimpleStringProperty(cellData.getValue()[0].toString()) : null);
        surnameColumn.setCellValueFactory(cellData -> cellData.getValue()[1] != null ? new SimpleStringProperty(cellData.getValue()[1].toString()) : null);
        nameColumn.setCellValueFactory(cellData -> cellData.getValue()[2] != null ? new SimpleStringProperty(cellData.getValue()[2].toString()) : null);
        patronymicColumn.setCellValueFactory(cellData -> cellData.getValue()[3] != null ? new SimpleStringProperty(cellData.getValue()[3].toString()) : null);
        roleColumn.setCellValueFactory(cellData -> cellData.getValue()[4] != null ? new SimpleStringProperty(cellData.getValue()[4].toString()) : null);
        salaryColumn.setCellValueFactory(cellData -> cellData.getValue()[5] != null ? new SimpleStringProperty(cellData.getValue()[5].toString()) : null);
        dobColumn.setCellValueFactory(cellData -> cellData.getValue()[6] != null ? new SimpleStringProperty(cellData.getValue()[6].toString()) : null);
        startColumn.setCellValueFactory(cellData -> cellData.getValue()[7] != null ? new SimpleStringProperty(cellData.getValue()[7].toString()) : null);
        phoneColumn.setCellValueFactory(cellData -> cellData.getValue()[8] != null ? new SimpleStringProperty(cellData.getValue()[8].toString()) : null);
        cityColumn.setCellValueFactory(cellData -> cellData.getValue()[9] != null ? new SimpleStringProperty(cellData.getValue()[9].toString()) : null);
        streetColumn.setCellValueFactory(cellData -> cellData.getValue()[10] != null ? new SimpleStringProperty(cellData.getValue()[10].toString()) : null);
        zipColumn.setCellValueFactory(cellData -> cellData.getValue()[11] != null ? new SimpleStringProperty(cellData.getValue()[11].toString()) : null);

        idColumn.setSortable(false);
        surnameColumn.setSortable(false);
        nameColumn.setSortable(false);
        patronymicColumn.setSortable(false);
        roleColumn.setSortable(false);
        salaryColumn.setSortable(false);
        dobColumn.setSortable(false);
        startColumn.setSortable(false);
        phoneColumn.setSortable(false);
        cityColumn.setSortable(false);
        streetColumn.setSortable(false);
        zipColumn.setSortable(false);

        loadEmployeesData();

        updateButton.setDisable(true);
        deleteButton.setDisable(true);
        addButton.setDisable(false);
        printThisEmployeeButton.setDisable(true);

        employeesTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection == null) {
                updateButton.setDisable(true);
                deleteButton.setDisable(true);
                printThisEmployeeButton.setDisable(true);
            } else {
                updateButton.setDisable(false);
                deleteButton.setDisable(false);
                printThisEmployeeButton.setDisable(false);

                Object[] selected = employeesTable.getSelectionModel().getSelectedItem();
                surnameField.setText(selected[1].toString());
            }
        });

        surnameCol.prefWidthProperty().bind(address_phone_table.widthProperty().multiply(0.33));
        adressCol.prefWidthProperty().bind(address_phone_table.widthProperty().multiply(0.33));
        phoneCol.prefWidthProperty().bind(address_phone_table.widthProperty().multiply(0.33));

        surnameCol.setCellValueFactory(cellData -> cellData.getValue()[0] != null ? new SimpleStringProperty(cellData.getValue()[0].toString()) : null);
        adressCol.setCellValueFactory(cellData -> cellData.getValue()[1] != null ? new SimpleStringProperty(cellData.getValue()[1].toString()) : null);
        phoneCol.setCellValueFactory(cellData -> cellData.getValue()[2] != null ? new SimpleStringProperty(cellData.getValue()[2].toString()) : null);

        try {
            showInfoButton.setDisable(true);

            // Витягує прізвища працівників з бази даних для "розумного пошуку"
            ResultSet employees_information = executeQuery(
                    "SELECT empl_surname " +
                            "FROM employee");
            ArrayList surnames = new ArrayList<>();
            while (employees_information.next()) {
                surnames.add(employees_information.getString("empl_surname"));
            }

            TextFields.bindAutoCompletion(surnameField, surnames);

            surnameField.textProperty().addListener((observable, oldValue, newValue) -> {
                if (!newValue.isBlank()) {
                    showInfoButton.setDisable(false);
                } else {
                    showInfoButton.setDisable(true);
                }
            });
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void loadEmployeesData() {
        try {
            ResultSet employees_information = null;
            if(SCREEN == 0){
                // 5. Отримати інформацію про усіх працівників, відсортованих за прізвищем;
                employees_information = executeQuery(
                            "SELECT * " +
                                "FROM employee " +
                                "ORDER BY empl_surname");
            }else if(SCREEN == 1){
                // 6. Отримати інформацію про усіх працівників, що займають посаду касира, відсортованих за прізвищем;
                employees_information = executeQuery(
                            "SELECT * " +
                                "FROM employee " +
                                "WHERE empl_role = 'Касир'" +
                                "ORDER BY empl_surname");
            }

            while (employees_information.next()) {
                Object[] rowData = {
                        employees_information.getString("id_employee"),
                        employees_information.getString("empl_surname"),
                        employees_information.getString("empl_name"),
                        employees_information.getString("empl_patronymic"),
                        employees_information.getString("empl_role"),
                        employees_information.getString("salary"),
                        employees_information.getString("date_of_birth"),
                        employees_information.getString("date_of_start"),
                        employees_information.getString("phone_number"),
                        employees_information.getString("city"),
                        employees_information.getString("street"),
                        employees_information.getString("zip_code")
                };
                employeesTable.getItems().add(rowData);
            }

            employees_information.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    String id_empl;
    @FXML
    private void printThisEmployeeButtonOnAction(ActionEvent event) {
        Object[] selectedEmployee = employeesTable.getSelectionModel().getSelectedItem();
        if (selectedEmployee != null) {
            this.id_empl = selectedEmployee[0].toString();
            try {

                // Видруковувати звіти з інформацією про обраного працівника
                ResultSet employees_information = executeQuery(
                        "SELECT id_employee, " +
                                "CONCAT(empl_surname, ' ', empl_name, ' ', empl_patronymic) AS pib, " +
                                "empl_role, " +
                                "salary, " +
                                "date_of_birth, " +
                                "date_of_start, " +
                                "phone_number, " +
                                "CONCAT('м. ', city, ' вул. ', street, ' поштовий індекс: ', zip_code) AS address " +
                                "FROM employee " +
                                "WHERE id_employee = '" + id_empl + "'"
                );

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
                    Paragraph title = new Paragraph("Звіт з інформацією про працівника", font);

                    title.setAlignment(Element.ALIGN_CENTER);
                    title.setSpacingAfter(10);
                    document.add(title);

                    PdfPTable table = new PdfPTable(8);
                    table.setWidthPercentage(100);

                    Stream.of("ID", "ПІБ", "Посада", "Заробітня плата", "Дата народження", "Дата початку роботи", "Номер телефону", "Адреса")
                            .forEach(columnTitle -> {
                                PdfPCell header = new PdfPCell();
                                header.setBackgroundColor(BaseColor.LIGHT_GRAY);
                                header.setBorderWidth(1);
                                header.setPhrase(new Phrase(columnTitle, font));
                                table.addCell(header);
                            });

                    font.setSize(12);
                    font.setStyle(Font.NORMAL);
                    while (employees_information.next()) {
                        Stream.of(
                                employees_information.getString("id_employee"),
                                employees_information.getString("pib"),
                                employees_information.getString("empl_role"),
                                employees_information.getString("salary"),
                                employees_information.getString("date_of_birth"),
                                employees_information.getString("date_of_start"),
                                employees_information.getString("phone_number"),
                                employees_information.getString("address")
                        ).forEach(data -> {
                            PdfPCell cell = new PdfPCell();
                            cell.setPhrase(new Phrase(data, font));
                            table.addCell(cell);
                        });
                    }

                    table.completeRow();
                    document.add(table);

                    document.close();
                    employees_information.close();
                    printerJob.endJob();
                } else {
                    System.out.println("Користувач відмінив збереження файлу.");
                }
            } catch (SQLException | IOException e) {
                e.printStackTrace();
            } catch (com.itextpdf.text.DocumentException e) {
                throw new RuntimeException(e);
            }
        }
    }

    @FXML
    private void printButtonOnAction(ActionEvent event) {
        try {
            ResultSet employees_information = null;
            if(SCREEN == 0){
                // 4. Видруковувати звіти з інформацією про усіх працівників
                employees_information = executeQuery(
                        "SELECT id_employee, " +
                            "CONCAT(empl_surname, ' ', empl_name, ' ', empl_patronymic) AS pib, " +
                            "empl_role, " +
                            "salary, " +
                            "date_of_birth, " +
                            "date_of_start, " +
                            "phone_number, " +
                            "CONCAT('м. ', city, ' вул. ', street, ' поштовий індекс: ', zip_code) AS address " +
                            "FROM employee;");
            } else if (SCREEN == 1){
                // 4. Видруковувати звіти з інформацією про усіх касирів
                employees_information = executeQuery(
                        "SELECT id_employee, " +
                                "CONCAT(empl_surname, ' ', empl_name, ' ', empl_patronymic) AS pib, " +
                                "empl_role, " +
                                "salary, " +
                                "date_of_birth, " +
                                "date_of_start, " +
                                "phone_number, " +
                                "CONCAT('м. ', city, ' вул. ', street, ' поштовий індекс: ', zip_code) AS address " +
                                "FROM employee " +
                                "WHERE empl_role = 'Касир'");
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
                Paragraph title = new Paragraph("Звіт з інформацією про працівників", font);

                title.setAlignment(Element.ALIGN_CENTER);
                title.setSpacingAfter(10);
                document.add(title);

                PdfPTable table = new PdfPTable(8);
                table.setWidthPercentage(100);

                Stream.of("ID", "ПІБ", "Посада", "Заробітня плата", "Дата народження", "Дата початку роботи", "Номер телефону", "Адреса")
                        .forEach(columnTitle -> {
                            PdfPCell header = new PdfPCell();
                            header.setBackgroundColor(BaseColor.LIGHT_GRAY);
                            header.setBorderWidth(1);
                            header.setPhrase(new Phrase(columnTitle, font));
                            table.addCell(header);
                        });

                font.setSize(12);
                font.setStyle(Font.NORMAL);
                while (employees_information.next()) {
                    Stream.of(
                            employees_information.getString("id_employee"),
                            employees_information.getString("pib"),
                            employees_information.getString("empl_role"),
                            employees_information.getString("salary"),
                            employees_information.getString("date_of_birth"),
                            employees_information.getString("date_of_start"),
                            employees_information.getString("phone_number"),
                            employees_information.getString("address")
                    ).forEach(data -> {
                        PdfPCell cell = new PdfPCell();
                        cell.setPhrase(new Phrase(data, font));
                        table.addCell(cell);
                    });
                }

                table.completeRow();
                document.add(table);

                document.close();
                employees_information.close();
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
    public void cashiersButtonOnAction(ActionEvent event) throws IOException {
        SCREEN = 1;
        clearTable();
        loadEmployeesData();
    }

    @FXML
    public void allEmployeesButtonOnAction(ActionEvent event) throws IOException {
        SCREEN = 0;
        clearTable();
        loadEmployeesData();
    }

    @FXML
    public void updateButtonOnAction(ActionEvent event) throws IOException {
        Object[] selectedEmployee = employeesTable.getSelectionModel().getSelectedItem();
        if (selectedEmployee != null) {
            String employeeId = selectedEmployee[0].toString();
            System.out.println(employeeId);

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/db_zlagoda/update_employee_page/update-employee-view.fxml"));
            Parent root = loader.load();
            UpdateEmployeeController controller = loader.getController();
            controller.initData(employeeId);

            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.show();
            stage.setOnHidden(e -> {
                clearTable();
                loadEmployeesData();
            });

        }
    }

    private void clearTable() {
        employeesTable.getItems().clear();
    }

    @FXML
    public void deleteButtonOnAction (ActionEvent event) throws IOException{
        try{
            Object[] selectedEmployee = employeesTable.getSelectionModel().getSelectedItem();
            if (selectedEmployee != null) {
                String employeeId = selectedEmployee[0].toString();

                DatabaseConnection connection = new DatabaseConnection();
                Connection connectDB = connection.getConnection();
                Statement statement = connectDB.createStatement();

                String deleteEmployee = "DELETE FROM employee WHERE id_employee = '" + employeeId + "'";
                statement.executeUpdate(deleteEmployee);

                statement.close();
                connectDB.close();

                clearTable();
                loadEmployeesData();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void addButtonOnAction (ActionEvent event) throws IOException{
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/db_zlagoda/login_page/registration-view.fxml"));
            Parent root = loader.load();

            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.show();
            stage.setOnHidden(e -> {
                clearTable();
                loadEmployeesData();
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

//    @FXML
//    public void findAddressAndPhoneButtonOnAction (ActionEvent event) throws IOException{
//        try {
//            FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/db_zlagoda/search/find-employee-view.fxml"));
//            Parent root = loader.load();
//
//            Stage stage = new Stage();
//            stage.setScene(new Scene(root));
//            stage.show();
//            stage.setOnHidden(e -> {
//                clearTable();
//                loadEmployeesData();
//            });
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }

    @FXML
    private TableView<Object[]> address_phone_table;

    @FXML
    private TableColumn<Object[], String> surnameCol;

    @FXML
    private TableColumn<Object[], String> adressCol;

    @FXML
    private TableColumn<Object[], String> phoneCol;

    @FXML
    private Button showInfoButton;
    @FXML
    private TextField surnameField;

    public void showInfoButtonOnAction(ActionEvent event){
        address_phone_table.getItems().clear();
        try {
            // 11. За прізвищем працівника знайти його телефон та адресу;
            ResultSet employees_information = executeQuery(
                    "SELECT empl_surname, city, street, phone_number " +
                            "FROM employee " +
                            "WHERE empl_surname = '" + surnameField.getText() + "'");


            while (employees_information.next()) {
                String surname = employees_information.getString("empl_surname");
                String phoneNumber = employees_information.getString("phone_number");
                String city = employees_information.getString("city");
                String street = employees_information.getString("street");

                Object[] rowData = {surname, city + ", " + street, phoneNumber};
                address_phone_table.getItems().add(rowData);
            }

            employees_information.close();
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
