package org.example.db_zlagoda.utils.tableview_tools;

public class ProductItem {

    private String name;
    private int amount;
    private String utc;
    private double price;
    public ProductItem(String utc, String name, int amount, double price) {
        this.utc = utc;
        this.name = name;
        this.amount = amount;
        this.price = price;
    }

    public int getAmount() {
        return amount;
    }

    public String getName() {
        return name;
    }

    public String getUtc() {
        return utc;
    }

    public double getPrice() {
        return price;
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

    public void setUtc(String utc) {
        this.utc = utc;
    }
}
