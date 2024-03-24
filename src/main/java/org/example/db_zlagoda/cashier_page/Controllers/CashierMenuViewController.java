package org.example.db_zlagoda.cashier_page.Controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import org.example.db_zlagoda.db_data.DatabaseManager;
import org.example.db_zlagoda.tableview_tools.ClientTableItem;
import org.example.db_zlagoda.tableview_tools.ProductTableItem;

import java.util.stream.Stream;

public class CashierMenuViewController {
    public VBox receiptMenu;
    public VBox searchProductsMenu;
    public HBox cashierMenuContainer;
    public VBox searchClientsMenu;
    public Button productsButton;
    public Button receiptButton;
    public Button clientsButton;
    public TableColumn product_utc;
    public TableColumn product_name;
    public TableColumn product_amount;
    public TableColumn product_availability;
    public TableView productsTable;
    public TableView clientsTable;
    public TableColumn client_id;
    public TableColumn client_name;
    public TableColumn client_phone;
    public TableColumn client_address;
    public TableColumn client_discount;
    private VBox[] menus;
    private Button[] menuButtons;

    public void initialize() {
        menus = new VBox[] {receiptMenu, searchProductsMenu, searchClientsMenu};
        menuButtons = new Button[] {productsButton, receiptButton, clientsButton};
        removeAllMenus();
    }


    private void initProductsTable() {
        product_utc.setCellValueFactory(new PropertyValueFactory<ProductTableItem, String>("utc"));
        product_name.setCellValueFactory(new PropertyValueFactory<ProductTableItem, String>("name"));
        product_amount.setCellValueFactory(new PropertyValueFactory<ProductTableItem, Integer>("amount"));
        product_availability.setCellValueFactory(new PropertyValueFactory<ProductTableItem, String>("availability"));
        productsTable.setItems(DatabaseManager.getProductTableItems());
    }

    private void initClientsTable() {
        client_id.setCellValueFactory(new PropertyValueFactory<ClientTableItem, String>("card_id"));
        client_name.setCellValueFactory(new PropertyValueFactory<ClientTableItem, String>("name"));
        client_phone.setCellValueFactory(new PropertyValueFactory<ClientTableItem, String>("phone"));
        client_address.setCellValueFactory(new PropertyValueFactory<ClientTableItem, String>("address"));
        client_discount.setCellValueFactory(new PropertyValueFactory<ClientTableItem, Double>("discount"));
        clientsTable.setItems(DatabaseManager.getClientTableItems());
    }
    //TODO: generalize menu buttons action
    @FXML
    public void openProducts(ActionEvent event) {
        removeAllMenus();
        openMenu(searchProductsMenu);
        enableAllButtons();
        productsButton.setDisable(true);
        initProductsTable();
    }

    @FXML
    public void openClients(ActionEvent event) {
        removeAllMenus();
        openMenu(searchClientsMenu);
        enableAllButtons();
        clientsButton.setDisable(true);
        initClientsTable();
    }

    @FXML
    public void openReceipt(ActionEvent event) {
        removeAllMenus();
        openMenu(receiptMenu);
        enableAllButtons();
        receiptButton.setDisable(true);
    }

    private void openMenu(VBox menu) {
        cashierMenuContainer.getChildren().add(menu);
    }
    private void enableAllButtons() {
        Stream.of(menuButtons).forEach(x -> {
            if(x.isDisable()) x.setDisable(false);
        });

    }
    private void removeAllMenus() {
        Stream.of(menus).forEach(x -> cashierMenuContainer.getChildren().remove(x));
    }
}
