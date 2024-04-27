package org.example.db_zlagoda.login_page;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import org.example.db_zlagoda.DatabaseConnection;
import org.example.db_zlagoda.cashier_page.Controllers.CashierMenuViewController;

import java.sql.*;

public class LoginController {
    @FXML
    private Button cancelButton;
    public void cancelButtonOnAction(ActionEvent event){
        Stage stage = (Stage) cancelButton.getScene().getWindow();
        stage.close();
    }
    private Button LoginButton;
    @FXML
    private Label invalidUserInfoError;
    public void loginButtonOnAction(ActionEvent event){
        if (userNameTextField.getText().isBlank() == false && PasswordTextField.getText().isBlank() == false){
            validateLogin();
        }
        else{
            invalidUserInfoError.setText("Please enter your user information.");
        }
    }

    @FXML
    private TextField userNameTextField;

    @FXML
    private PasswordField PasswordTextField;

    public void validateLogin() {
        try (Connection connectDB = DatabaseConnection.getConnection()) {
            String username_str = userNameTextField.getText();
            String password_str = RegistrationController.hashPassword(PasswordTextField.getText());

            // Верифікація працівників
            String verifyLogin = "SELECT * FROM login_table WHERE username = ? AND password = ?";
            try (PreparedStatement statement = connectDB.prepareStatement(verifyLogin)) {
                statement.setString(1, username_str);
                statement.setString(2, password_str);
                ResultSet resultQuery = statement.executeQuery();

                if (resultQuery.next()) {
                    String id_employee = resultQuery.getString("id_employee");
                    String positionQuery = "SELECT empl_role FROM employee WHERE id_employee = ?";
                    try (PreparedStatement positionStatement = connectDB.prepareStatement(positionQuery)) {
                        positionStatement.setString(1, id_employee);
                        ResultSet positionResult = positionStatement.executeQuery();

                        if (positionResult.next()) {
                            String role = positionResult.getString("empl_role");
                            if(role.equals("Касир")){
                                Stage stage = (Stage) cancelButton.getScene().getWindow();
                                stage.close();
                                cashierScreen(id_employee);
                            } else {
                                Stage stage = (Stage) cancelButton.getScene().getWindow();
                                stage.close();
                                managerScreen();
                            }
                        } else {
                            invalidUserInfoError.setText("Помилка при пошуку посади.");
                        }
                    }
                } else {
                    invalidUserInfoError.setText("Неправильний username або пароль.");
                }
            }
        } catch (SQLException error) {
            error.printStackTrace();
        }
    }

    public void managerScreen(){
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/db_zlagoda/manager_page/manager-page-view.fxml"));
            Parent root = loader.load();

            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void cashierScreen(String id_employee){
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/db_zlagoda/cashier_page/Views/cashier-menu-view.fxml"));
            Parent root = loader.load();

            try (Connection connectDB = DatabaseConnection.getConnection()) {
                String cashierInfo = "SELECT * FROM employee WHERE id_employee = ?";
                try (PreparedStatement statement = connectDB.prepareStatement(cashierInfo)) {
                    statement.setString(1, id_employee);
                    ResultSet resultQuery = statement.executeQuery();

                    if (resultQuery.next()) {
                        String id_cashier = resultQuery.getString("id_employee");
                        String cashierSurname = resultQuery.getString("empl_surname");
                        String cashierName = resultQuery.getString("empl_name");
                        String cashierPatronymic = resultQuery.getString("empl_patronymic");
                        String PIB = cashierSurname + " " + cashierName + " " + cashierPatronymic;
                        String cashierRole = resultQuery.getString("empl_role");
                        String cashierSalary = resultQuery.getString("salary") + " грн";
                        String cashierFirstDay = resultQuery.getString("date_of_start");
                        String cashierBirthDate = resultQuery.getString("date_of_birth");
                        String cashierPhoneNumber = resultQuery.getString("phone_number");
                        String cashierCity = resultQuery.getString("city");
                        String cashierStreet = resultQuery.getString("street");
                        String cashierZipCode = resultQuery.getString("zip_code");
                        String address = "м. " + cashierCity + ", вул. " + cashierStreet + ", індекс: " + cashierZipCode;

                        CashierMenuViewController controller = loader.getController();
                        controller.setUserInfo(id_cashier, PIB, cashierRole, cashierSalary, cashierFirstDay, cashierBirthDate, cashierPhoneNumber, address);
                    } else {
                        invalidUserInfoError.setText("Invalid username or password.");
                    }
                }
        } catch (SQLException error) {
                error.printStackTrace();
            }

            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
