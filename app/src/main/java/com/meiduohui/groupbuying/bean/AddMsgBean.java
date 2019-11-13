package com.meiduohui.groupbuying.bean;

public class AddMsgBean {
    /**
     * order_id : 65
     * table : system_message
     * pay_price : 2.76
     * notify : notify_change_message
     */

    private String order_id;
    private String table;
    private double pay_price;
    private String notify;

    public String getOrder_id() {
        return order_id;
    }

    public void setOrder_id(String order_id) {
        this.order_id = order_id;
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
