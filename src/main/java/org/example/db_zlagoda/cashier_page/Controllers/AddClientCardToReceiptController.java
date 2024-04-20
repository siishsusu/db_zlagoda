package org.example.db_zlagoda.cashier_page.Controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.example.db_zlagoda.utils.tableview_tools.ClientItem;
import org.example.db_zlagoda.utils.tableview_tools.TableViewLoader;

import java.io.IOException;

public class AddClientCardToReceiptController {
    private Stage stage;
    public TableView clientsTable;
    public VBox viewContainer;
    public TableColumn id;
    public TableColumn name;
    public TableColumn phone;
    public TableColumn address;
    public TableColumn discount;
    private ClientItem clientSelected;
    public Button addCardButton;

    public void initialize() {
        TableViewLoader.initClientsTable(clientsTable, id, name, phone, address, discount);
        clientsTable.setItems(ControllerAccess.cashierMenuViewController.data.getClients());
        addCardButton.setDisable(true);
        clientsTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                addCardButton.setDisable(false);
            } else {
                addCardButton.setDisable(true);
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
    public void addCard(ActionEvent event) throws IOException {
        clientSelected = (ClientItem) clientsTable.getSelectionModel().getSelectedItem();
        if(stage == null) initStage();
        ControllerAccess.cashierMenuViewController.addReceiptClientCard(clientSelected);
        closeMenu(event);
    }


}
