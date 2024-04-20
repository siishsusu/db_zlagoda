package org.example.db_zlagoda.cashier_page.Controllers;

import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.example.db_zlagoda.DatabaseConnection;
import org.example.db_zlagoda.cashier_page.Models.SessionData;
import org.example.db_zlagoda.cashier_page.Models.UserData;
import org.example.db_zlagoda.db_data.DatabaseManager;
import org.example.db_zlagoda.utils.Exceptions.NegativeAmountException;
import org.example.db_zlagoda.utils.receipt_tools.Receipt;
import org.example.db_zlagoda.utils.tableview_tools.ClientItem;
import org.example.db_zlagoda.utils.tableview_tools.ProductItem;
import org.example.db_zlagoda.utils.tableview_tools.TableViewLoader;
import org.example.db_zlagoda.utils.MenuChanger;

import java.io.IOException;
import java.sql.Date;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.stream.Stream;


public class CashierMenuViewController {
    @FXML
    public Label idField, PIBField, roleField, salaryField, firstDayField, birthDateField, phoneField, addressField;

    public HBox cashierMenuContainer;

    public VBox receiptMenu;
    public VBox searchProductsMenu;
    public VBox searchClientsMenu;
    public VBox userProfileMenu;
    public VBox receiptHistoryMenu;

    public Button productsButton;
    public Button receiptButton;
    public Button clientsButton;
    public Button userProfileButton;
    public Button removeFromReceiptButton;
    public Button saveReceiptButton;
    public Button receiptHistoryButton;

    public TableColumn product_upc;
    public TableColumn product_name;
    public TableColumn product_amount;
    public TableColumn product_price;

    public TableView productsTable;
    public TableView clientsTable;
    public TableView receiptTable;
    public TableView receiptHistoryTable;

    public TableColumn client_id;
    public TableColumn client_name;
    public TableColumn client_phone;
    public TableColumn client_address;
    public TableColumn client_discount;

    public TableColumn receipt_upc;
    public TableColumn receipt_name;
    public TableColumn receipt_amount;
    public TableColumn receipt_price;

    public TableColumn history_num;
    public TableColumn history_date;
    public TableColumn history_sum;
    public TableColumn history_vat;

    public Label receipt_sum;

    private VBox[] menus;
    private Button[] menuButtons;
    private TableView[] tableViews;

    public ObservableList<ProductItem> receiptItems;
    public ClientItem receiptClientCard;
    public SessionData data;
    public static Statement database;
    public static UserData user;

    public void initialize() {
        ControllerAccess.cashierMenuViewController = this;
        try{
            CashierMenuViewController.database = new DatabaseConnection().getConnection().createStatement();
        } catch (SQLException e) {
            database = null;
            e.printStackTrace();
        }
        menus = new VBox[] {userProfileMenu, receiptMenu, searchProductsMenu, searchClientsMenu, receiptHistoryMenu};
        menuButtons = new Button[] {userProfileButton, productsButton, receiptButton, clientsButton, receiptHistoryButton};
        tableViews = new TableView[] {productsTable, clientsTable, receiptTable, receiptHistoryTable};
        removeAllMenus();
        receiptItems = FXCollections.observableArrayList();
        receiptItems.addListener((ListChangeListener<ProductItem>) _ -> saveReceiptButton.setDisable(receiptItems.isEmpty()));
        saveReceiptButton.setDisable(true);
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

    private void initReceiptHistoryTable() {
        TableViewLoader.initReceiptHistoryTable(receiptHistoryTable, history_num, history_date,
                history_sum, history_vat);
        receiptHistoryTable.setItems(data.getReceipts());
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
        CashierMenuViewController.user = new UserData(id, PIB, role, salary, firstDay, birthDate, phone, address);
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
    public void openHistory(ActionEvent event) {
        openMenu(receiptHistoryMenu, receiptHistoryButton);
        initReceiptHistoryTable();
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
//            removeFromReceipt(item);
            updateAmount(item, false);
        }
        receiptItems.clear();
        updateSum();
    }

    @FXML
    public void deleteReceipt(ActionEvent actionEvent) {
        //TODO: Add multiple receipt system
        clearReceipt();
    }

    @FXML
    public void saveReceipt(ActionEvent actionEvent) {
        addClientCardPromptWindow();
    }

    public void saveReceiptToDB() {
        try {
            Receipt receipt = new Receipt(Date.valueOf(LocalDate.now()), receiptItems, CashierMenuViewController.user.getId(), receiptClientCard == null ? null : receiptClientCard.getCard_id());
            DatabaseManager.removeReceiptProducts(receiptItems);
            DatabaseManager.addReceiptToDB(receipt);
            receipt.setId(DatabaseManager.getCheckID(receipt));
            data.addReceipt(receipt);
            clearReceipt();
            data.setProducts(DatabaseManager.getProductTableItems(database));
        } catch (SQLException e) {
            e.printStackTrace();
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

    public void addReceiptClientCard(ClientItem item) throws IOException {
        receiptClientCard = item;
        proceedToCheckout();
    }

    private void proceedToCheckout() throws IOException {
        MenuChanger.changeMenu(MenuChanger.LoaderClass.CashierView,
                "checkout-view.fxml",
                "Огляд чеку");
    }


    public void addClientCardPromptWindow() {
        cashierMenuContainer.setDisable(true);

        Label promptLabel = new Label("Додати карту клієнта?");
        Button okButton = new Button("Додати");
        Button cancelButton = new Button("Скасувати");

        okButton.setAlignment(Pos.CENTER);
        okButton.setPrefWidth(175);

        cancelButton.setAlignment(Pos.CENTER);
        cancelButton.setPrefWidth(175);

        HBox buttons = new HBox();
        buttons.setPadding(new Insets(5));
        buttons.setPrefWidth(350);
        buttons.setAlignment(Pos.BASELINE_CENTER);
        buttons.getChildren().addAll(okButton, cancelButton);

        VBox box = new VBox();
        box.setPadding(new Insets(20));
        box.setPrefWidth(350);
        box.setAlignment(Pos.BASELINE_CENTER);
        box.getChildren().addAll(promptLabel, buttons);

        Scene scene = new Scene(box, 350, 100);
        Stage promptStage = new Stage();
        promptStage.setScene(scene);
        promptStage.setTitle("");
        promptStage.show();


        cancelButton.setOnAction(x -> {
            promptStage.close();
            receiptClientCard = null;
            try {
                proceedToCheckout();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });

        okButton.setOnAction(x -> {
            promptStage.close();
            receiptClientCard = null;
            try {
                MenuChanger.changeMenu(MenuChanger.LoaderClass.CashierView,
                        "add-clientcard-to-receipt-view.fxml",
                        "Додати карту клієнта до чеку");
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
    }

    private void addToReceiptList(ProductItem product) {
        updateAmount(product, true);
        System.out.println(receiptItems.contains(product));
        if(receiptItems.contains(product)) {
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
        } else {
            receiptItems.add(product);
            updateSum();
        }


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
        updateSum();
    }

    private void updateSum() {
        double sum = 0;
        for(ProductItem item : receiptItems) {
            sum += item.getPrice();
        }
        sum*=1.2;
        receipt_sum.textProperty().set(String.format("Сума (з ПДВ): %.2f грн", sum));
    }

    private void updateTable(TableView table) {
        table.refresh();
    }

    private void updateAllTables() {
        for (TableView tableView : tableViews) {
            tableView.refresh();
        }
    }

    public void addMoreReceipsMenu(ActionEvent actionEvent) {
        cashierMenuContainer.setDisable(true);

        Label promptLabel = new Label("Введіть дату:");
        DatePicker datePicker = new DatePicker();
        Button okButton = new Button("Ок");
        Button cancelButton = new Button("Скасувати");

        okButton.setAlignment(Pos.CENTER);
        okButton.setPrefWidth(175);
        okButton.setDisable(true);

        cancelButton.setAlignment(Pos.CENTER);
        cancelButton.setPrefWidth(175);

        GridPane gridPane = new GridPane();
        gridPane.setPadding(new Insets(20));
        gridPane.setVgap(10);
        gridPane.setHgap(10);
        gridPane.setPrefWidth(350);
        gridPane.setAlignment(Pos.BASELINE_CENTER);

        gridPane.add(promptLabel, 0, 0);
        gridPane.add(datePicker, 1, 0);
        gridPane.add(okButton, 0, 1);
        gridPane.add(cancelButton, 1, 1);

        Scene scene = new Scene(gridPane, 350, 100);
        Stage promptStage = new Stage();
        promptStage.setScene(scene);
        promptStage.setTitle("");
        promptStage.show();

        datePicker.setOnAction((EventHandler) t -> {
            LocalDate date = datePicker.getValue();
            okButton.setDisable(date.isAfter(LocalDate.now()));
        });

        cancelButton.setOnAction(x -> {
            promptStage.close();
            cashierMenuContainer.setDisable(false);
        });

        okButton.setOnAction(x -> {
            cancelButton.fire();
            try {
                addReceiptsFromDB(Date.valueOf(datePicker.getValue()));
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        });
    }

    public void addReceiptsFromDB(Date date) throws SQLException {
        ObservableList<Receipt> list = DatabaseManager.selectReceipts(database, date, user.getId());
        list.removeAll(data.getReceipts());
        data.getReceipts().addAll(list);
        data.getReceipts().sort(Comparator.reverseOrder());
    }
}
