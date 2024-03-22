package org.example.db_zlagoda.tableview_tools;

public class ProductTableItem {

    private String name;
    private int amount;
    private String utc;
    private String availability;
    public ProductTableItem(String utc, String name, int amount, String availability) {
        this.utc = utc;
        this.name = name;
        this.amount = amount;
        this.availability = availability;
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

    public String getAvailability() {
        return availability;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public void setAvailability(String availability) {
        this.availability = availability;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setUtc(String utc) {
        this.utc = utc;
    }
}
