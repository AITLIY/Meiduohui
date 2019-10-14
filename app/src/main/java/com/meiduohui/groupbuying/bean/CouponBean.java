package com.meiduohui.groupbuying.bean;

import java.io.Serializable;

public class CouponBean implements Serializable {

    /**
     * q_id : 353
     * q_content : 10元代金券
     * q_type : 1
     * q_state : 0
     * q_price : 10.00
     * m_id : 0
     * shop_id : null
     * time : 2019-09-24 14:56:31
     * start_time : 1569308191
     * end_time : 1571900191
     * shop_name : null
     * address : null
     * sjh : null
     * beizhu : null
     * title : null
     * state_intro : 未使用
     * shop_intro:
     */

    private String q_id;
    private String q_content;
    private String q_type;
    private String q_state;
    private String q_price;
    private String m_id;
    private String shop_id;
    private String time;
    private String start_time;
    private String end_time;
    private String shop_name;
    private String address;
    private String sjh;
    private String beizhu;
    private String title;
    private String state_intro;
    private String shop_intro;

    public String getShop_intro() {
        return shop_intro;
    }

    public void setShop_intro(String shop_intro) {
        this.shop_intro = shop_intro;
    }

    public String getQ_id() {
        return q_id;
    }

    public void setQ_id(String q_id) {
        this.q_id = q_id;
    }

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

    public String getQ_state() {
        return q_state;
    }

    public void setQ_state(String q_state) {
        this.q_state = q_state;
    }

    public String getQ_price() {
        return q_price;
    }

    public void setQ_price(String q_price) {
        this.q_price = q_price;
    }

    public String getM_id() {
        return m_id;
    }

    public void setM_id(String m_id) {
        this.m_id = m_id;
    }

    public String getShop_id() {
        return shop_id;
    }

    public void setShop_id(String shop_id) {
        this.shop_id = shop_id;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getStart_time() {
        return start_time;
    }

    public void setStart_time(String start_time) {
        this.start_time = start_time;
    }

    public String getEnd_time() {
        return end_time;
    }

    public void setEnd_time(String end_time) {
        this.end_time = end_time;
    }

    public String getShop_name() {
        return shop_name;
    }

    public void setShop_name(String shop_name) {
        this.shop_name = shop_name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getSjh() {
        return sjh;
    }

    public void setSjh(String sjh) {
        this.sjh = sjh;
    }

    public String getBeizhu() {
        return beizhu;
    }

    public void setBeizhu(String beizhu) {
        this.beizhu = beizhu;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getState_intro() {
        return state_intro;
    }

    public void setState_intro(String state_intro) {
        this.state_intro = state_intro;
    }
}