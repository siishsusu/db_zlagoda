package org.example.db_zlagoda.cashier_page.Controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.example.db_zlagoda.utils.tableview_tools.ProductItem;
import org.example.db_zlagoda.utils.tableview_tools.TableViewLoader;

public class CheckoutViewController {
    public VBox viewContainer;

    public TableView receiptTable;

    public TableColumn receipt_upc;
    public TableColumn receipt_name;
    public TableColumn receipt_amount;
    public TableColumn receipt_price;

    public Label receipt_total;
    public Label receipt_vat;
    public Label receipt_discount;
    public Label receipt_sum;

    public void initialize() {
        TableViewLoader.initProductsTable(receiptTable, receipt_upc, receipt_name, receipt_amount, receipt_price);
        receiptTable.setItems(ControllerAccess.cashierMenuViewController.receiptItems);
        double total = 0;
        double vat;
        double totalAndVat;
        double discount = ControllerAccess.cashierMenuViewController.receiptClientCard == null
                ? 0
                : ControllerAccess.cashierMenuViewController.receiptClientCard.getDiscount()/100;
        for(ProductItem item : ControllerAccess.cashierMenuViewController.receiptItems) {
            total += item.getPrice();
        }
        vat = total * 0.2;
        totalAndVat = (total + vat) * (1-discount);

        receipt_total.textProperty().set(String.format("Сума: %.2f грн", total));
        receipt_vat.textProperty().set(String.format("ПДВ: %.2f грн", vat));
        receipt_discount.textProperty().set(String.format("Знижка: %.2f ", discount*100) + "%");
        receipt_sum.textProperty().set(String.format("Сума (з ПДВ): %.2f грн", totalAndVat));
    }
    @FXML
    public void deleteReceipt(ActionEvent actionEvent) {
        ControllerAccess.cashierMenuViewController.cashierMenuContainer.setDisable(false);
        ((Stage)viewContainer.getScene().getWindow()).close();
    }

    @FXML
    public void saveReceipt(ActionEvent actionEvent) {
        deleteReceipt(actionEvent);
        ControllerAccess.cashierMenuViewController.saveReceiptToDB();
    }
}
