package org.example.db_zlagoda.cashier_page.Controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import org.example.db_zlagoda.utils.tableview_tools.ProductItem;
import org.example.db_zlagoda.utils.tableview_tools.TableViewLoader;
import org.example.db_zlagoda.utils.MenuChanger;

import java.io.IOException;
import java.util.Objects;
import java.util.stream.Stream;

public class CashierMenuViewController {
    public HBox cashierMenuContainer;

    public VBox receiptMenu;
    public VBox searchProductsMenu;
    public VBox searchClientsMenu;
    public VBox userProfileMenu;

    public Button productsButton;
    public Button receiptButton;
    public Button clientsButton;
    public Button userProfileButton;

    public TableColumn product_utc;
    public TableColumn product_name;
    public TableColumn product_amount;
    public TableColumn product_price;

    public TableView productsTable;
    public TableView clientsTable;
    public TableView receiptTable;

    public TableColumn client_id;
    public TableColumn client_name;
    public TableColumn client_phone;
    public TableColumn client_address;
    public TableColumn client_discount;

    public TableColumn receipt_utc;
    public TableColumn receipt_name;
    public TableColumn receipt_amount;
    public TableColumn receipt_price;

    private VBox[] menus;
    private Button[] menuButtons;

    private ObservableList<ProductItem> receiptItems;

    public void initialize() {
        menus = new VBox[] {userProfileMenu, receiptMenu, searchProductsMenu, searchClientsMenu};
        menuButtons = new Button[] {userProfileButton, productsButton, receiptButton, clientsButton};
        removeAllMenus();
        receiptItems = FXCollections.observableArrayList();
    }


    private void initProductsTable() {
        TableViewLoader.initProductsTable(productsTable, product_utc,
                product_name, product_amount, product_price);
    }

    private void initClientsTable() {
        TableViewLoader.initClientsTable(clientsTable, client_id, client_name,
                client_phone, client_address, client_discount);
    }

    private void initReceiptTable() {
        TableViewLoader.initReceiptTable(receiptTable, receipt_utc, receipt_name, receipt_amount, receipt_price);
        receiptTable.setItems(receiptItems);
    }
    //TODO: generalize menu buttons action

    @FXML
    public void openUserProfile(ActionEvent event) {
        removeAllMenus();
        openMenu(userProfileMenu);
        enableAllButtons();
        userProfileButton.setDisable(true);
    }

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
        initReceiptTable();
    }

    @FXML
    public void removeSelectedProduct(ActionEvent event) {
        receiptItems.remove(receiptTable.getSelectionModel().getSelectedItem());
    }
    @FXML
    public void openAddProductMenu(ActionEvent event) throws IOException {
        MenuChanger.changeMenu(MenuChanger.LoaderClass.CashierView,
                "add-product-to-receipt-view.fxml",
                "Додати товар до чеку");
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
        if(cashierMenuContainer == null) return;
        Stream.of(menus).forEach(x ->
                cashierMenuContainer.getChildren().remove(x)
        );
    }

    public void addReceiptProduct(ProductItem product, int amount) {
        addToReceiptList(new ProductItem(
                product.getUtc(),
                product.getName(),
                amount,
                product.getPrice()*amount));

    }

    private void addToReceiptList(ProductItem product) {
        for (int i = 0; i < receiptItems.size(); i++) {
            if(Objects.equals(receiptItems.get(i).getUtc(), product.getUtc())) {
                System.out.println("equals");
                ProductItem item = receiptItems.get(i);
                item.setAmount(receiptItems.get(i).getAmount() + product.getAmount());
                item.setPrice(receiptItems.get(i).getPrice()+product.getPrice());
                receiptItems.set(i, item);
                return;
            }
        }
        receiptItems.add(product);
    }
}
