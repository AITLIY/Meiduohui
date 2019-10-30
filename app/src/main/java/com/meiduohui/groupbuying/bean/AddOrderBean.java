package com.meiduohui.groupbuying.bean;

import java.io.Serializable;

public class AddOrderBean implements Serializable {
    /**
     * order_id : 55
     * state : 0
     * state_intro : 未付款
     * table : system_order
     * pay_price : 0.99
     * notify : notify_change_order
     */

    private String order_id;
    private int state;
    private String state_intro;
    private String table;
    private double pay_price;
    private String notify;

    public String getOrder_id() {
        return order_id;
    }

    public void setOrder_id(String order_id) {
        this.order_id = order_id;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    public String getState_intro() {
        return state_intro;
    }

    public void setState_intro(String state_intro) {
        this.state_intro = state_intro;
    }

    public String getTable() {
        return table;
    }

    public void setTable(String table) {
        this.table = table;
    }

    public double getPay_price() {
        return pay_price;
    }

    public void setPay_price(double pay_price) {
        this.pay_price = pay_price;
    }

    public String getNotify() {
        return notify;
    }

    public void setNotify(String notify) {
        this.notify = notify;
    }
}
