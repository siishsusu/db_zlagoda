package org.example.db_zlagoda.login_page;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import org.example.db_zlagoda.DatabaseConnection;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

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
        DatabaseConnection connection = new DatabaseConnection();
        Connection connectDB = connection.getConnection();

        String username_str = userNameTextField.getText(), password_str = RegistrationController.hashPassword(PasswordTextField.getText());
        String verifyLogin = "SELECT COUNT(1) FROM login_table WHERE username = '" + username_str +
                "' AND password = '" + password_str + "'";

        try {
            Statement statement = connectDB.createStatement();
            ResultSet resultQuery = statement.executeQuery(verifyLogin);

            while (resultQuery.next()) {
                if (resultQuery.getInt(1) == 1) {
                    invalidUserInfoError.setText("CONGRATS");
                } else {
                    invalidUserInfoError.setText("Invalid username or password.");
                }
            }
        } catch (Exception error) {
            error.printStackTrace();
            error.getCause();
        }
    }

}
