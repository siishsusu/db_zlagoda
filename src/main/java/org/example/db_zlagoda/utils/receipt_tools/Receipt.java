package org.example.db_zlagoda.utils.receipt_tools;

import org.example.db_zlagoda.utils.tableview_tools.ProductItem;

import java.util.Date;

public class Receipt {
    private final Date date;
    private double sum = 0;
    private double vat = 0;

    public Receipt(Date date, ProductItem[] products) {
        this.date = date;
        initPriceFields(products);
    }

    private void initPriceFields(ProductItem[] products) {
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
}
