package org.example.db_zlagoda.cashier_page.Controllers;

import javafx.event.ActionEvent;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.example.db_zlagoda.utils.receipt_tools.Receipt;
import org.example.db_zlagoda.utils.tableview_tools.ProductItem;
import org.example.db_zlagoda.utils.tableview_tools.TableViewLoader;

public class CheckViewController {

    public static Receipt receipt;

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
    public TableColumn receipt_category;
    public TableColumn receipt_prom;

    public void initialize() {
        TableViewLoader.initProductsTable(receiptTable, receipt_upc, receipt_name, receipt_category, receipt_amount, receipt_price, receipt_prom);
        receiptTable.setItems(receipt.getProducts());
        double total = 0;
        double vat;
        double totalAndVat;
        double discount = receipt.getCard() == null
                ? 0
                : receipt.getCard().getDiscount()/100;
        for(ProductItem item : receipt.getProducts()) {
            total += item.getPrice() * item.getAmount();
        }
        vat = total * 0.2;
        totalAndVat = (total + vat) * (1-discount);

        receipt_total.textProperty().set(String.format("Сума: %.2f грн", total));
        receipt_vat.textProperty().set(String.format("ПДВ: %.2f грн", vat));
        receipt_discount.textProperty().set(String.format("Знижка: %.2f ", discount*100) + "%");
        receipt_sum.textProperty().set(String.format("Сума (з ПДВ): %.2f грн", totalAndVat));
    }


    public void closeWindow(ActionEvent actionEvent) {
        ((Stage)viewContainer.getScene().getWindow()).close();
    }
}
