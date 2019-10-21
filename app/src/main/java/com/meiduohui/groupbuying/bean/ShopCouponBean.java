package com.meiduohui.groupbuying.bean;

public class ShopCouponBean {
    /**
     * q_content : 15元代金券
     * q_type : 1
     * q_price : 15.00
     * r_id : 5
     * s_w_quan_count : 6
     * s_y_quan_count : 2
     */

    private String q_content;
    private String q_type;
    private String q_price;
    private String r_id;
    private int s_w_quan_count;
    private int s_y_quan_count;

    public String getQ_content() {
        return q_content;
    }

    public void setQ_content(String q_content) {
        this.q_content = q_content;
    }

    public String getQ_type() {
        return q_type;
    }

    public void setQ_type(String q_type) {
        this.q_type = q_type;
    }

    public String getQ_price() {
        return q_price;
    }

    public void setQ_price(String q_price) {
        this.q_price = q_price;
    }

    public String getR_id() {
        return r_id;
    }

    public void setR_id(String r_id) {
        this.r_id = r_id;
    }

    public int getS_w_quan_count() {
        return s_w_quan_count;
    }

    public void setS_w_quan_count(int s_w_quan_count) {
        this.s_w_quan_count = s_w_quan_count;
    }

    public int getS_y_quan_count() {
        return s_y_quan_count;
    }

    public void setS_y_quan_count(int s_y_quan_count) {
        this.s_y_quan_count = s_y_quan_count;
    }
}