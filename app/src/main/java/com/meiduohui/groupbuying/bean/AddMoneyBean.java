package com.meiduohui.groupbuying.bean;

import java.io.Serializable;

public class AddMoneyBean implements Serializable {
    /**
     * order_id : 8
     * table : system_chongzhi
     * pay_price : 0.01
     * notify : notify_change_cz
     */

    private String order_id;
    private String table;
    private String pay_price;
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

    public String getPay_price() {
        return pay_price;
    }

    public void setPay_price(String pay_price) {
        this.pay_price = pay_price;
    }

    public String getNotify() {
        return notify;
    }

    public void setNotify(String notify) {
        this.notify = notify;
    }
}
