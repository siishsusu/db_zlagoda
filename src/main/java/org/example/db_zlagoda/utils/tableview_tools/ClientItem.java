package org.example.db_zlagoda.utils.tableview_tools;

public class ClientItem {

    private String card_id, name, phone, address;
    public double discount;

    public ClientItem(String card_id, String name, String phone, String address, double discount) {
        this.card_id = card_id;
        this.name = name;
        this.phone = phone;
        this.address = address;
        this.discount = discount;
    }

    public String getName() {
        return name;
    }

    public double getDiscount() {
        return discount;
    }

    public String getAddress() {
        return address;
    }

    public String getCard_id() {
        return card_id;
    }

    public String getPhone() {
        return phone;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public void setCard_id(String card_id) {
        this.card_id = card_id;
    }

    public void setDiscount(double discount) {
        this.discount = discount;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    @Override
    public boolean equals(Object other){
        if (other == null) return false;
        if (other == this) return true;
        if (!(other instanceof ClientItem otherCI)) return false;
        return this.card_id.equals(otherCI.card_id);
    }
}