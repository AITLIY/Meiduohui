package com.meiduohui.groupbuying.bean;

public class RecordBean {
    /**
     * mem_id : 1
     * money_change : 10.17
     * money_type : +
     * money_reason : 抢红包获得10.17
     * money_time : 2019-10-16 14:31:01
     */

    private String mem_id;
    private String money_change;
    private String money_type;
    private String money_reason;
    private String money_time;

    public String getMem_id() {
        return mem_id;
    }

    public void setMem_id(String mem_id) {
        this.mem_id = mem_id;
    }

    public String getMoney_change() {
        return money_change;
    }

    public void setMoney_change(String money_change) {
        this.money_change = money_change;
    }

    public String getMoney_type() {
        return money_type;
    }

    public void setMoney_type(String money_type) {
        this.money_type = money_type;
    }

    public String getMoney_reason() {
        return money_reason;
    }

    public void setMoney_reason(String money_reason) {
        this.money_reason = money_reason;
    }

    public String getMoney_time() {
        return money_time;
    }

    public void setMoney_time(String money_time) {
        this.money_time = money_time;
    }
}
