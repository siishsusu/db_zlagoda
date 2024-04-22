package org.example.db_zlagoda.cashier_page.Models;

import javafx.beans.Observable;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import org.example.db_zlagoda.cashier_page.Controllers.CashierMenuViewController;
import org.example.db_zlagoda.db_data.DatabaseManager;
import org.example.db_zlagoda.utils.Exceptions.NegativeAmountException;
import org.example.db_zlagoda.utils.receipt_tools.Receipt;
import org.example.db_zlagoda.utils.tableview_tools.CategoryItem;
import org.example.db_zlagoda.utils.tableview_tools.ClientItem;
import org.example.db_zlagoda.utils.tableview_tools.ProductInfo;
import org.example.db_zlagoda.utils.tableview_tools.ProductItem;

import java.util.Collections;
import java.util.Objects;

public class SessionData {

    private ObservableList<Receipt> receipts;
    private ObservableList<ProductItem> products;
    private FilteredList<ProductItem> filteredProducts;
    private FilteredList<ProductInfo> filteredAllProducts;
    private FilteredList<CategoryItem> filteredCategories;
    private ObservableList<ProductInfo> allProducts;
    private ObservableList<CategoryItem> categories;
    private ObservableList<ClientItem> clients;

    public SessionData() {
        receipts = FXCollections.observableArrayList();
        products = FXCollections.observableArrayList();
        allProducts = FXCollections.observableArrayList();
        categories = FXCollections.observableArrayList();
        clients = FXCollections.observableArrayList();

        filteredProducts = new FilteredList<>(products);
        filteredAllProducts = new FilteredList<>(allProducts);
        filteredCategories = new FilteredList<>(categories);

//        products.addListener((ListChangeListener<ProductItem>) c -> {
//            System.out.println("jdlkasjd");
//            filteredProducts.clear();
//            filteredProducts.addAll(c.getList());
//
//        });
//        allProducts.addListener((ListChangeListener<ProductInfo>) c -> {
//            filteredAllProducts.clear();
//            filteredAllProducts.addAll(c.getList());
//
//        });
//        categories.addListener((ListChangeListener<CategoryItem>) c -> {
//            filteredCategories.clear();
//            filteredCategories.addAll(c.getList());
//
//        });

        loadClients();
        loadProducts();
    }

    public void loadProducts() {
        products.clear();
        allProducts.clear();
        categories.clear();

        products.addAll(DatabaseManager.getProductTableItems(CashierMenuViewController.database));
        allProducts.addAll(DatabaseManager.getAllProductTableItems(CashierMenuViewController.database));
        categories.addAll(DatabaseManager.getCategoryTableItems(CashierMenuViewController.database));
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

    public ObservableList<CategoryItem> getCategories() {
        return categories;
    }

    public ObservableList<ProductInfo> getAllProducts() {
        return allProducts;
    }

    public FilteredList<CategoryItem> getFilteredCategories() {
        return filteredCategories;
    }

    public FilteredList<ProductInfo> getFilteredAllProducts() {
        return filteredAllProducts;
    }

    public FilteredList<ProductItem> getFilteredProducts() {
        return filteredProducts;
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

    public void setAllProducts(ObservableList<ProductInfo> allProducts) {
        this.allProducts = allProducts;
    }

    public void setCategories(ObservableList<CategoryItem> categories) {
        this.categories = categories;
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
