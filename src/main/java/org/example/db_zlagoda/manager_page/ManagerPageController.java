package org.example.db_zlagoda.manager_page;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;

public class ManagerPageController {
    @FXML
    private VBox mainContainer;
    @FXML
    private Button employeesButton;
    public void employeesButtonOnAction(ActionEvent event){
        loadView("/org/example/db_zlagoda/employees_list/employee-list-view.fxml");
    }

    @FXML
    private Button clientsButton;
    public void clientsButtonOnAction(ActionEvent event){
        loadView("/org/example/db_zlagoda/customers_list/customers-list-view.fxml");
    }

    @FXML
    private Button productsButton;
    public void productsButtonOnAction(ActionEvent event){
        loadView("/org/example/db_zlagoda/product_page/product-page-view.fxml");
    }

    @FXML
    private Button checksButton;
    public void checksButtonOnAction(ActionEvent event){
        loadView("/org/example/db_zlagoda/checks_page/checks-list-view.fxml");
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
}
