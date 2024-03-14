package org.example.db_zlagoda.login_page;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

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
            if(userNameTextField.getText().equals("admin") && PasswordTextField.getText().equals("password")){
                invalidUserInfoError.setText("Logging you in");
            }
            else{
                invalidUserInfoError.setText("You are trying to login. " + userNameTextField.getText() + " " + PasswordTextField.getText());
            }
        }
        else{
            invalidUserInfoError.setText("Please enter your user information.");
        }
    }

    @FXML
    private TextField userNameTextField;

    @FXML
    private PasswordField PasswordTextField;

}
