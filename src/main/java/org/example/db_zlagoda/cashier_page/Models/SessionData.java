package org.example.db_zlagoda.cashier_page.Models;

import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import org.example.db_zlagoda.cashier_page.Controllers.CashierMenuViewController;
import org.example.db_zlagoda.db_data.DatabaseManager;
import org.example.db_zlagoda.utils.Exceptions.NegativeAmountException;
import org.example.db_zlagoda.utils.receipt_tools.Receipt;
import org.example.db_zlagoda.utils.tableview_tools.ClientItem;
import org.example.db_zlagoda.utils.tableview_tools.ProductItem;

import java.util.Collections;
import java.util.Objects;

public class SessionData {

    private ObservableList<Receipt> receipts;
    private ObservableList<ProductItem> products;
    private ObservableList<ClientItem> clients;

    public SessionData() {
        receipts = FXCollections.observableArrayList();
        products = FXCollections.observableArrayList();
        clients = FXCollections.observableArrayList();
        loadClients();
        loadProducts();
    }

    private void loadProducts() {
        products = DatabaseManager.getProductTableItems(CashierMenuViewController.database);
    }

    private void loadClients() {
        clients = DatabaseManager.getClientTableItems();
    }

    public ObservableList<ClientItem> getClients() {
        return clients;
    }

    public ObservableList<ProductItem> getProducts() {
        return products;
    }

    public ObservableList<Receipt> getReceipts() {
        return receipts;
    }

    public void setClients(ObservableList<ClientItem> clients) {
        this.clients = clients;
    }

    public void setProducts(ObservableList<ProductItem> products) {
        this.products = products;
    }

    public void setReceipts(ObservableList<Receipt> receipts) {
        this.receipts = receipts;
    }

    public void editProduct(ProductItem item) {
        for(int i = 0; i < products.size(); i++) {
            if(products.get(i).equals(item)) {
                products.set(i, item);
            }
        }
    }

    /**
     * Changes product amount;
     * @param item - product to change amount of
     * @param amount - amount of items to add (insert minus to subtract instead)
     */
    public void editProductAmount(ProductItem item, int amount) throws NegativeAmountException {
        for (ProductItem product : products) {
            if (product.equals(item)) {
                amount += product.getAmount();
                if (amount < 0) throw new NegativeAmountException();
                product.setAmount(amount);
                System.out.println("am" + product.getAmount());
                break;
            }
        }
    }

    public void editClient(ClientItem item) {
        for(int i = 0; i < clients.size(); i++) {
            if(clients.get(i).equals(item)) {
                clients.set(i, item);
            }
        }
    }

    public void addReceipt(Receipt receipt) {
        receipts.add(receipt);
        receipts.sort(Collections.reverseOrder());
    }
}
