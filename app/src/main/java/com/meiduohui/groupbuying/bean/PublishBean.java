package com.meiduohui.groupbuying.bean;

import java.util.List;

public class PublishBean {
    /**
     * order_id : 22
     * yuding : 0
     * cat_id1 : 13
     * cat_id2 : 15
     * beizhu : 你不配
     * m_price : 99999999.99
     * m_old_price : 99999999.99
     * title : 你们这群弟弟
     * intro : 买的起么
     * img : ["http://photo.meiduohui.cn/8ac1b201911261413535779.jpg"]
     * video : null
     * shop_id : 1
     * start_time : 1574697600
     * end_time : 1606320000
     * state : 1
     * shop_name : 河口老老字号
     * shop_img : http://photo.meiduohui.cn/3d98b201911152246355126.JPEG
     * quan_count : 6
     * q_title : 5元代金券
     * state_intro : 有效
     */

    private String order_id;
    private String yuding;
    private String cat_id1;
    private String cat_id2;
    private String beizhu;
    private String m_price;
    private String m_old_price;
    private String title;
    private String intro;
    private String video;
    private String shop_id;
    private String start_time;
    private String end_time;
    private String state;
    private String shop_name;
    private String shop_img;
    private int quan_count;
    private String q_title;
    private String state_intro;
    private List<String> img;

    public String getOrder_id() {
        return order_id;
    }

    public void setOrder_id(String order_id) {
        this.order_id = order_id;
    }

    public String getYuding() {
        return yuding;
    }

    public void setYuding(String yuding) {
        this.yuding = yuding;
    }

    public String getCat_id1() {
        return cat_id1;
    }

    public void setCat_id1(String cat_id1) {
        this.cat_id1 = cat_id1;
    }

    public String getCat_id2() {
        return cat_id2;
    }

    public void setCat_id2(String cat_id2) {
        this.cat_id2 = cat_id2;
    }

    public String getBeizhu() {
        return beizhu;
    }

    public void setBeizhu(String beizhu) {
        this.beizhu = beizhu;
    }

    public String getM_price() {
        return m_price;
    }

    public void setM_price(String m_price) {
        this.m_price = m_price;
    }

    public String getM_old_price() {
        return m_old_price;
    }

    public void setM_old_price(String m_old_price) {
        this.m_old_price = m_old_price;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getIntro() {
        return intro;
    }

    public void setIntro(String intro) {
        this.intro = intro;
    }

    public String getVideo() {
        return video;
    }

    public void setVideo(String video) {
        this.video = video;
    }

    public String getShop_id() {
        return shop_id;
    }

    public void setShop_id(String shop_id) {
        this.shop_id = shop_id;
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

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getShop_name() {
        return shop_name;
    }

    public void setShop_name(String shop_name) {
        this.shop_name = shop_name;
    }

    public String getShop_img() {
        return shop_img;
    }

    public void setShop_img(String shop_img) {
        this.shop_img = shop_img;
    }

    public int getQuan_count() {
        return quan_count;
    }

    public void setQuan_count(int quan_count) {
        this.quan_count = quan_count;
    }

    public String getQ_title() {
        return q_title;
    }

    public void setQ_title(String q_title) {
        this.q_title = q_title;
    }

    public String getState_intro() {
        return state_intro;
    }

    public void setState_intro(String state_intro) {
        this.state_intro = state_intro;
    }

    public List<String> getImg() {
        return img;
    }

    public void setImg(List<String> img) {
        this.img = img;
    }
}
