package org.example.db_zlagoda.utils.tableview_tools;

public class ProductItem{

    private String name;
    private int amount;
    private String upc;
    private double price;
    public ProductItem(String utc, String name, int amount, double price) {
        this.upc = utc;
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

    public String getUpc() {
        return upc;
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

    public void setUpc(String utc) {
        this.upc = utc;
    }

    @Override
    public boolean equals(Object other){
        if (other == null) return false;
        if (other == this) return true;
        if (!(other instanceof ProductItem otherPI)) return false;
        return this.upc.equals(otherPI.upc);
    }
}
