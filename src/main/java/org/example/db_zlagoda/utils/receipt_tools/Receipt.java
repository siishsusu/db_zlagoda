package org.example.db_zlagoda.utils.receipt_tools;

import javafx.collections.ObservableList;
import org.example.db_zlagoda.utils.tableview_tools.ClientItem;
import org.example.db_zlagoda.utils.tableview_tools.ProductItem;

import java.util.Date;

public class Receipt implements Comparable<Receipt> {
    private final Date date;
    private double sum = 0;
    private double vat = 0;
    private String employee;
    private String card;
    private ObservableList<ProductItem> products;
    private int id = 0;

    public Receipt(Date date, ObservableList<ProductItem> products, String employee, String card) {
        this.date = date;
        this.employee = employee;
        this.card = card;
        this.products = products;
        initPriceFields(products);
    }

    public Receipt(int id, String employee, String card, double sum, double vat, Date date) {
        this.id = id;
        this.employee = employee;
        this.card = card;
        this.sum = sum;
        this.vat = vat;
        this.date = date;
    }

    private void initPriceFields(ObservableList<ProductItem> products) {
        for(ProductItem product : products) {
            sum += product.getPrice();
        }
        vat = sum * 0.2;
    }

    public Date getDate() {
        return date;
    }

    public double getSum() {
        return sum;
    }

    public double getVat() {
        return vat;
    }

    public String getEmployee() {
        return employee;
    }

    public String getCard() {
        return card;
    }

    public ObservableList<ProductItem> getProducts() {
        return products;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Override
    public int compareTo(Receipt o) {
        return Integer.compare(id, o.id);
    }

    @Override
    public boolean equals(Object other){
        if (other == null) return false;
        if (other == this) return true;
        if (!(other instanceof Receipt otherCI)) return false;
        return this.id == otherCI.id;
    }
}
