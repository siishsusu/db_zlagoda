package org.example.db_zlagoda.cashier_page.Models;

public class UserData {
    private String id;
    private String PIB;
    private String role;
    private String salary;
    private String firstDay;
    private String birthDate;
    private String phone;
    private String address;

    public UserData(String id, String PIB, String role, String salary, String firstDay,
                    String birthDate, String phone, String address) {
        this.id = id;
        this.PIB = PIB;
        this.role = role;
        this.salary = salary;
        this.firstDay = firstDay;
        this.birthDate = birthDate;
        this.phone = phone;
        this.address = address;
    }

    public String getId() {
        return id;
    }

    public String getAddress() {
        return address;
    }

    public String getBirthDate() {
        return birthDate;
    }

    public String getFirstDay() {
        return firstDay;
    }

    public String getPhone() {
        return phone;
    }

    public String getPIB() {
        return PIB;
    }

    public String getRole() {
        return role;
    }

    public String getSalary() {
        return salary;
    }
}
