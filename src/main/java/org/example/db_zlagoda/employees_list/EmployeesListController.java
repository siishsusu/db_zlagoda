package org.example.db_zlagoda.employees_list;

import javafx.beans.property.SimpleStringProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.stage.Stage;
import org.example.db_zlagoda.DatabaseConnection;
import org.example.db_zlagoda.update_employee_page.UpdateEmployeeController;

import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ResourceBundle;

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
    private Button updateButton, deleteButton, cashiersButton, allEmployeesButton, addButton;
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
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

        loadEmployeesData();

        updateButton.setDisable(true);
        deleteButton.setDisable(true);
        addButton.setDisable(false);

        employeesTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection == null) {
                updateButton.setDisable(true);
                deleteButton.setDisable(true);
            } else {
                updateButton.setDisable(false);
                deleteButton.setDisable(false);
            }
        });
    }

    private void loadEmployeesData() {
        try {
            DatabaseConnection connection = new DatabaseConnection();
            Connection connectDB = connection.getConnection();
            Statement statement = connectDB.createStatement();
            ResultSet employees_information = null;
            if(SCREEN == 0){
                // 5. Отримати інформацію про усіх працівників, відсортованих за прізвищем;
                employees_information = statement.executeQuery(
                            "SELECT * " +
                                "FROM employee " +
                                "ORDER BY empl_surname");
            }else if(SCREEN == 1){
                // 6. Отримати інформацію про усіх працівників, що займають посаду касира, відсортованих за прізвищем;
                employees_information = statement.executeQuery(
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
            statement.close();
            connectDB.close();
        } catch (SQLException e) {
            e.printStackTrace();
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

    @FXML
    public void findAddressAndPhoneButtonOnAction (ActionEvent event) throws IOException{
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/db_zlagoda/search/find-employee-view.fxml"));
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
}
