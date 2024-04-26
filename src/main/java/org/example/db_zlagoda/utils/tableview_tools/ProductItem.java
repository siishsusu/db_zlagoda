package org.example.db_zlagoda.utils.tableview_tools;

import org.example.db_zlagoda.DatabaseConnection;
import org.example.db_zlagoda.cashier_page.Controllers.CashierMenuViewController;
import org.example.db_zlagoda.cashier_page.Controllers.ControllerAccess;
import org.example.db_zlagoda.db_data.DatabaseManager;

import java.sql.SQLException;

public class ProductItem{

    private String name;
    private int amount;
    private String upc;
    private String saleUpc;
    private double price;
    private CategoryItem category;
    private String onSale;
    private int promotionalProduct;

    public ProductItem(String upc, String saleUpc, String name, int amount, double price, int category, int promotionalProduct) throws SQLException {
        this.upc = upc;
        this.saleUpc = saleUpc;
        this.name = name;
        this.amount = amount;
        this.price = price;
        this.category = setCategoryByID(category);
        this.promotionalProduct = promotionalProduct;
        setOnSale();
    }

    public ProductItem(String upc, String saleUpc, String name, int amount, double price, CategoryItem category, int promotionalProduct) {
        this.upc = upc;
        this.name = name;
        this.amount = amount;
        this.price = price;
        this.category = category;
        this.promotionalProduct = promotionalProduct;
        setOnSale();
    }

    public int getPromotionalProduct() {
        return promotionalProduct;
    }

    public void setPromotionalProduct(int promotionalProduct) {
        this.promotionalProduct = promotionalProduct;
    }

    private CategoryItem setCategoryByID(int id) throws SQLException {
        return DatabaseManager.getCategoryByID(new DatabaseConnection().getConnection(), id);
    }
    public CategoryItem getCategory() {
        return category;
    }

    public String getSaleUpc() {
        return saleUpc;
    }

    public String getOnSale() {
        return onSale;
    }

    public int getAmount() {
        return amount;
    }

    public String getName() {
        return name;
    }

    public String getUpc() {
        return upc;
    }

    public double getPrice() {
        return price;
    }

    public void setSaleUpc(String saleUpc) {
        this.saleUpc = saleUpc;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setUpc(String utc) {
        this.upc = utc;
    }

    public void setCategory(CategoryItem category) {
        this.category = category;
    }

    public void setOnSale() {
        if(promotionalProduct == 1) this.onSale = "Акційна ціна";
        else this.onSale = "Повна ціна";
    }

    @Override
    public boolean equals(Object other){
        if (other == null) return false;
        if (other == this) return true;
        if (!(other instanceof ProductItem otherPI)) return false;
        return this.upc.equals(otherPI.upc);
    }
}
