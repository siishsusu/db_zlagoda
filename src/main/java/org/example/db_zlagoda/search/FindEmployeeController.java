package org.example.db_zlagoda.search;

import javafx.beans.property.SimpleStringProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import org.controlsfx.control.textfield.TextFields;
import org.example.db_zlagoda.DatabaseConnection;

import java.net.URL;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.temporal.Temporal;
import java.util.ArrayList;
import java.util.ResourceBundle;

public class FindEmployeeController implements Initializable {
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
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        surnameCol.setCellValueFactory(cellData -> cellData.getValue()[0] != null ? new SimpleStringProperty(cellData.getValue()[0].toString()) : null);
        adressCol.setCellValueFactory(cellData -> cellData.getValue()[1] != null ? new SimpleStringProperty(cellData.getValue()[1].toString()) : null);
        phoneCol.setCellValueFactory(cellData -> cellData.getValue()[2] != null ? new SimpleStringProperty(cellData.getValue()[2].toString()) : null);

        try {
            showInfoButton.setDisable(true);

            DatabaseConnection connection = new DatabaseConnection();
            Connection connectDB = connection.getConnection();
            Statement statement = connectDB.createStatement();
            // Витягує прізвища працівників з бази даних для "розумного пошуку"
            ResultSet employees_information = statement.executeQuery(
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
    public void showInfoButtonOnAction(ActionEvent event){
        address_phone_table.getItems().clear();
        try {
            DatabaseConnection connection = new DatabaseConnection();
            Connection connectDB = connection.getConnection();
            Statement statement = connectDB.createStatement();
            // 11. За прізвищем працівника знайти його телефон та адресу;
            ResultSet employees_information = statement.executeQuery(
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
            statement.close();
            connectDB.close();
        } catch (
        SQLException e) {
            e.printStackTrace();
        }
    }
}
