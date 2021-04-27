package com.daon.admin_onorder.model;

public class OrderModel {
    private String table;
    private String menu;
    private String price;
    private String payment;
    private String status;
    private String num;
    private String count;
    private String time;
    private String auth_num;
    private String auth_date;
    private String vantr;
    private String cardbin;

    public OrderModel(String num, String table, String menu, String price, String payment, String status, String count, String time, String auth_num,
                      String auth_date, String vantr, String cardbin) {
        this.num = num;
        this.table = table;
        this.menu = menu;
        this.price = price;
        this.payment = payment;
        this.status = status;
        this.count = count;
        this.time = time;
        this.auth_num = auth_num;
        this.auth_date = auth_date;
        this.vantr = vantr;
        this.cardbin = cardbin;
    }

    public String getAuth_date() {
        return auth_date;
    }

    public void setAuth_date(String auth_date) {
        this.auth_date = auth_date;
    }

    public String getVantr() {
        return vantr;
    }

    public void setVantr(String vantr) {
        this.vantr = vantr;
    }

    public String getCardbin() {
        return cardbin;
    }

    public void setCardbin(String cardbin) {
        this.cardbin = cardbin;
    }

    public String getAuth_num() {
        return auth_num;
    }

    public void setAuth_num(String auth_num) {
        this.auth_num = auth_num;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getCount() {
        return count;
    }

    public void setCount(String count) {
        this.count = count;
    }

    public String getNum() {
        return num;
    }

    public void setNum(String num) {
        this.num = num;
    }

    public String getTable() {
        return table;
    }

    public void setTable(String table) {
        this.table = table;
    }

    public String getMenu() {
        return menu;
    }

    public void setMenu(String menu) {
        this.menu = menu;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getPayment() {
        return payment;
    }

    public void setPayment(String payment) {
        this.payment = payment;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
