package org.example.db_zlagoda.cashier_page.Controllers;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import org.example.db_zlagoda.cashier_page.Views.CashierMenuView;
import org.example.db_zlagoda.utils.tableview_tools.ProductItem;
import org.example.db_zlagoda.utils.tableview_tools.TableViewLoader;

import java.util.Arrays;

public class AddProductToReceiptMenuController {
    public Button selectAmountButton;
    private Stage stage;
    public TableView productsTable;
    public HBox viewContainer;
    public TableColumn product_utc;
    public TableColumn product_name;
    public TableColumn product_amount;
    public TableColumn product_price;
    private EventHandler<ActionEvent> okButtonHandler, cancelButtonHandler;
    private int amountSelected = 1;
    private ProductItem productSelected;

    public void initialize() {
        TableViewLoader.initProductsTable(productsTable, product_utc, product_name, product_amount, product_price);
        selectAmountButton.setDisable(true);
        productsTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                selectAmountButton.setDisable(false);
            }
        });
    }

    private void initStage() {
        stage = (Stage)viewContainer.getScene().getWindow();
    }


    @FXML
    public void closeMenu(ActionEvent event) {
        if(stage == null) initStage();
        stage.close();
    }

    @FXML
    public void selectAmount(ActionEvent event) {
        viewContainer.setDisable(true);
        productSelected = (ProductItem)productsTable.getSelectionModel().getSelectedItem();
        if(stage == null) initStage();
        addSelectAmountPromptScene();
    }

    private void addSelectAmountPromptScene() {
        Label promptLabel = new Label("Введіть кількість:");
        TextField amountTextField = new TextField();
        Button okButton = new Button("Додати");
        Button cancelButton = new Button("Скасувати");

        amountTextField.textProperty().set("1");

        okButton.setAlignment(Pos.CENTER);
        okButton.setPrefWidth(175);
        okButton.setDisable(false);

        cancelButton.setAlignment(Pos.CENTER);
        cancelButton.setPrefWidth(175);

        GridPane gridPane = new GridPane();
        gridPane.setPadding(new Insets(20));
        gridPane.setVgap(10);
        gridPane.setHgap(10);
        gridPane.setPrefWidth(350);
        gridPane.setAlignment(Pos.BASELINE_CENTER);

        gridPane.add(promptLabel, 0, 0);
        gridPane.add(amountTextField, 1, 0);
        gridPane.add(okButton, 0, 1);
        gridPane.add(cancelButton, 1, 1);



        Scene scene = new Scene(gridPane, 350, 100);
        Stage promptStage = new Stage();
        promptStage.setScene(scene);
        promptStage.setTitle("");
        promptStage.show();

        amountTextField.textProperty().addListener((observable, oldValue, newValue) -> {
            try {
                amountSelected = verifyAmount(newValue);
                okButton.setDisable(false);
            } catch (Exception e) {
                okButton.setDisable(true);
            }
        });
        okButton.setOnAction(x -> {
            promptStage.close();
            stage.close();
            viewContainer.setDisable(false);
            CashierMenuView.controller.addReceiptProduct(productSelected, amountSelected);
        });

        cancelButton.setOnAction(x -> {
            promptStage.close();
            viewContainer.setDisable(false);
        });
    }


    private int verifyAmount(String input) throws Exception {
        int result = Integer.parseInt(input);
        if(result < 1 || result > productSelected.getAmount()) {
            throw new Exception();
        }
        return result;
    }


}
