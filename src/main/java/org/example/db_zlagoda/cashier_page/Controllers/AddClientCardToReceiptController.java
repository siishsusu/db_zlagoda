package org.example.db_zlagoda.cashier_page.Controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.example.db_zlagoda.db_data.DatabaseManager;
import org.example.db_zlagoda.utils.tableview_tools.ClientItem;
import org.example.db_zlagoda.utils.tableview_tools.TableViewLoader;

import java.io.IOException;

public class AddClientCardToReceiptController {
    public TextArea searchArea;
    public TextArea searchPercentArea;
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
    private FilteredList<ClientItem> filteredList;
    private ObservableList<ClientItem> observableList;
    private String percent = null;
    private String query = null;

    public void initialize() {
        TableViewLoader.initClientsTable(clientsTable, id, name, phone, address, discount);
        observableList = FXCollections.observableArrayList(DatabaseManager.getClientTableItems());
        filteredList = new FilteredList<>(observableList);
        clientsTable.setItems(filteredList);
        addCardButton.setDisable(true);
        clientsTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                addCardButton.setDisable(false);
            } else {
                addCardButton.setDisable(true);
            }
        });

        searchArea.textProperty().addListener(_ ->{
            query = searchArea.getText();
            filterProducts();
        });

        searchPercentArea.textProperty().addListener(_ ->{
            percent = searchPercentArea.getText();
            System.out.println(percent);
            filterProducts();
        });


    }


    public void filterProducts() {
        if(query == null || query.isEmpty()) {
            filteredList.setPredicate(s ->
                    (percent == null || percent.isEmpty() || String.valueOf(s.getDiscount()).startsWith(percent)));
        }
        else {
            filteredList.setPredicate(s ->
                    s.getName().toLowerCase().contains(query.toLowerCase())
                    && (percent == null || percent.isEmpty() || String.valueOf(s.getDiscount()).startsWith(percent)));
        }
    }

    private void initStage() {
        stage = (Stage)viewContainer.getScene().getWindow();
    }


    @FXML
    public void closeMenu(ActionEvent event) {
        if(stage == null) initStage();
        stage.close();
        ControllerAccess.cashierMenuViewController.cashierMenuContainer.setDisable(false);
    }

    @FXML
    public void addCard(ActionEvent event) throws IOException {
        clientSelected = (ClientItem) clientsTable.getSelectionModel().getSelectedItem();
        if(stage == null) initStage();
        ControllerAccess.cashierMenuViewController.addReceiptClientCard(clientSelected);
        stage.close();
    }


}
