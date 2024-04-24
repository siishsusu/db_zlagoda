package org.example.db_zlagoda.manager_page;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.example.db_zlagoda.login_page.LoginPage;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class ManagerPageController {
    @FXML
    private VBox mainContainer;
    @FXML
    private Button employeesButton;
    public void employeesButtonOnAction(ActionEvent event) {
        loadView("/org/example/db_zlagoda/employees_list/employee-list-view.fxml");
        handleButtonClicked(employeesButton);
    }

    @FXML
    private Button clientsButton;
    public void clientsButtonOnAction(ActionEvent event){
        loadView("/org/example/db_zlagoda/customers_list/customers-list-view.fxml");
        handleButtonClicked(clientsButton);
    }

    @FXML
    private Button productsButton;
    public void productsButtonOnAction(ActionEvent event){
        loadView("/org/example/db_zlagoda/product_page/product-page-view.fxml");
        handleButtonClicked(productsButton);
    }

    @FXML
    private Button checksButton;
    public void checksButtonOnAction(ActionEvent event){
        loadView("/org/example/db_zlagoda/checks_page/checks-list-view.fxml");
        handleButtonClicked(checksButton);
    }

    @FXML
    private Button leaveButton;
    public void leaveButtonOnAction(ActionEvent event) {
        Stage stage = (Stage) leaveButton.getScene().getWindow();
        stage.close();

        Platform.runLater(() -> {
            try {
                new LoginPage().start(new Stage());
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    private void loadView (String path){
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(path));
            AnchorPane view = loader.load();

            mainContainer.getChildren().setAll(view);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void handleButtonClicked(Button button) {
        employeesButton.setDisable(false);
        clientsButton.setDisable(false);
        productsButton.setDisable(false);
        checksButton.setDisable(false);
        leaveButton.setDisable(false);

        button.setDisable(true);
    }
}
