package org.example.db_zlagoda.cashier_page.Controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import org.example.db_zlagoda.cashier_page.Models.SessionData;
import org.example.db_zlagoda.db_data.DatabaseManager;
import org.example.db_zlagoda.utils.Exceptions.NegativeAmountException;
import org.example.db_zlagoda.utils.receipt_tools.Receipt;
import org.example.db_zlagoda.utils.tableview_tools.ProductItem;
import org.example.db_zlagoda.utils.tableview_tools.TableViewLoader;
import org.example.db_zlagoda.utils.MenuChanger;

import java.io.IOException;
import java.sql.Date;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.stream.Stream;

public class CashierMenuViewController {
    @FXML
    public Label idField, PIBField, roleField, salaryField, firstDayField, birthDateField, phoneField, addressField;

    public HBox cashierMenuContainer;

    public VBox receiptMenu;
    public VBox searchProductsMenu;
    public VBox searchClientsMenu;
    public VBox userProfileMenu;

    public Button productsButton;
    public Button receiptButton;
    public Button clientsButton;
    public Button userProfileButton;
    public Button removeFromReceiptButton;


    public TableColumn product_upc;
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

    public TableColumn receipt_upc;
    public TableColumn receipt_name;
    public TableColumn receipt_amount;
    public TableColumn receipt_price;
    public Label receipt_sum;

    private VBox[] menus;
    private Button[] menuButtons;
    private TableView[] tableViews;

    private ObservableList<ProductItem> receiptItems;
    public SessionData data;

    public void initialize() {
        menus = new VBox[] {userProfileMenu, receiptMenu, searchProductsMenu, searchClientsMenu};
        menuButtons = new Button[] {userProfileButton, productsButton, receiptButton, clientsButton};
        tableViews = new TableView[] {productsTable, clientsTable, receiptTable};
        removeAllMenus();
        receiptItems = FXCollections.observableArrayList();
        data = new SessionData();

    }


    private void initProductsTable() {
        TableViewLoader.initProductsTable(productsTable, product_upc,
                product_name, product_amount, product_price);
        productsTable.setItems(data.getProducts());
    }

    private void initClientsTable() {
        TableViewLoader.initClientsTable(clientsTable, client_id, client_name,
                client_phone, client_address, client_discount);
        clientsTable.setItems(data.getClients());
    }

    private void initReceiptTable() {
        TableViewLoader.initProductsTable(receiptTable, receipt_upc, receipt_name, receipt_amount, receipt_price);
        receiptTable.setItems(receiptItems);
        updateSum();

        removeFromReceiptButton.setDisable(true);
        receiptTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                removeFromReceiptButton.setDisable(false);
            } else {
                removeFromReceiptButton.setDisable(true);
            }
        });
    }

    @FXML
    public void openUserProfile(ActionEvent event) {
        openMenu(userProfileMenu, userProfileButton);
    }

    public void setUserInfo(String id, String PIB, String role, String salary, String firstDay,
                            String birthDate, String phone, String address){
        idField.setText(id);
        PIBField.setText(PIB);
        roleField.setText(role);
        salaryField.setText(salary);
        firstDayField.setText(firstDay);
        birthDateField.setText(birthDate);
        phoneField.setText(phone);
        addressField.setText(address);
    }

    @FXML
    public void openProducts(ActionEvent event) {
        openMenu(searchProductsMenu, productsButton);
        initProductsTable();
    }

    @FXML
    public void openClients(ActionEvent event) {
        openMenu(searchClientsMenu, clientsButton);
        initClientsTable();
    }

    @FXML
    public void openReceipt(ActionEvent event) {
        openMenu(receiptMenu, receiptButton);
        initReceiptTable();
    }

    private void openMenu(VBox menu, Button button) {
        removeAllMenus();
        openMenu(menu);
        enableAllButtons();
        button.setDisable(true);
    }

    @FXML
    public void removeSelectedProduct(ActionEvent event) {

        removeFromReceipt((ProductItem)receiptTable.getSelectionModel().getSelectedItem());
    }
    @FXML
    public void openAddProductMenu(ActionEvent event) throws IOException {
        MenuChanger.changeMenu(MenuChanger.LoaderClass.CashierView,
                "add-product-to-receipt-view.fxml",
                "Додати товар до чеку");
    }

    @FXML
    public void removeAllProducts(ActionEvent actionEvent) {
        for(ProductItem item : receiptItems) {
            removeFromReceipt(item);
        }    }

    @FXML
    public void deleteReceipt(ActionEvent actionEvent) {
        //TODO: Add multiple receipt system
        clearReceipt();
    }

    @FXML
    public void saveReceipt(ActionEvent actionEvent) {
        try {
            Receipt receipt = new Receipt(Date.valueOf(LocalDate.now()), receiptItems);
            DatabaseManager.addReceiptToDB(receipt);
            data.addReceipt(receipt);
            clearReceipt();
        } catch (SQLException e) {
            e.printStackTrace();
            //TODO: Add error message
        }


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
                product.getUpc(),
                product.getName(),
                amount,
                product.getPrice()*amount));

    }

    private void addToReceiptList(ProductItem product) {
        updateAmount(product, true);
        for (int i = 0; i < receiptItems.size(); i++) {
            if(receiptItems.get(i).equals(product)) {
                ProductItem item = receiptItems.get(i);
                item.setAmount(receiptItems.get(i).getAmount() + product.getAmount());
                item.setPrice(receiptItems.get(i).getPrice()+product.getPrice());
                receiptItems.set(i, item);
                updateSum();
                return;
            }
        }
        receiptItems.add(product);
        updateSum();
    }


    private void updateAmount(ProductItem product, boolean subtract) {
        try {
            int amount = product.getAmount();
            amount = subtract ? amount * -1 : amount;
            data.editProductAmount(product, amount);
        } catch (NegativeAmountException e) {
            //TODO: show error message on screen
            e.printStackTrace();
        }
        updateAllTables();
    }

    private void removeFromReceipt(ProductItem item) {
        receiptItems.remove(item);
        updateAmount(item, false);
        updateSum();

    }

    private void clearReceipt() {
        receiptItems.clear();
    }

    private void updateSum() {
        double sum = 0;
        for(ProductItem item : receiptItems) {
            sum += item.getPrice();
        }
        receipt_sum.textProperty().set(String.format("Сума: %.2f грн", sum));
    }

    private void updateTable(TableView table) {
        table.refresh();
    }

    private void updateAllTables() {
        for (TableView tableView : tableViews) {
            tableView.refresh();
        }
    }
}
