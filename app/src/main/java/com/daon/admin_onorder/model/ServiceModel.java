package com.daon.admin_onorder.model;

public class ServiceModel {
    String num;
    String tableNo;
    String service;
    String status;
    String insertTime;

    public ServiceModel(String num, String tableNo, String service, String status, String insertTime) {
        this.num = num;
        this.tableNo = tableNo;
        this.service = service;
        this.status = status;
        this.insertTime = insertTime;
    }

    public String getNum() {
        return num;
    }

    public void setNum(String num) {
        this.num = num;
    }

    public String getTableNo() {
        return tableNo;
    }

    public void setTableNo(String tableNo) {
        this.tableNo = tableNo;
    }

    public String getService() {
        return service;
    }

    public void setService(String service) {
        this.service = service;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getInsertTime() {
        return insertTime;
    }

    public void setInsertTime(String insertTime) {
        this.insertTime = insertTime;
    }
}
