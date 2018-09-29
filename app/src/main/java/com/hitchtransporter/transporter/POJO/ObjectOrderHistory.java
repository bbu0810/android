package com.hitchtransporter.transporter.POJO;

import retrofit2.http.Headers;

/**
 * Created by ebiztrait321 on 7/9/17.
 */

public class ObjectOrderHistory {
    Boolean isDate;
    Boolean isHeader;
    String orderData;


    public ObjectOrderHistory(Boolean isDate, Boolean isHeader, String orderData) {
        this.isDate = isDate;
        this.orderData = orderData;
        this.isHeader = isHeader;
    }

    public String getOrderData() {
        return orderData;
    }

    public void setOrderData(String orderData) {
        this.orderData = orderData;
    }

    public void setIsDate(Boolean isDate) {
        this.isDate = isDate;
    }

    public Boolean getIsDate() {
        return isDate;
    }

    public Boolean getIsHeader() {
        return isHeader;
    }

}
