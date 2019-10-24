package com.meiduohui.groupbuying.bean;

import java.io.Serializable;

public class UserInfoBean {
    /**
     * mem_info : {"id":"1","name":"1","money":"3030.27","mobile":"18865391811","img":"http://photo.meiduohui.cn/ed09d05f37d15cee/619c64e67ab8fb24.png","shop_id":"1","order_count":1,"quan_count":6,"history_count":4}
     * shop_info : {"id":"1","name":"马氏杂货铺-临沂分店2000号","address":"北园路与沂蒙路交汇北100米路东0.0","sjh":"0539-4005289","intro":"马氏杂货铺，常来哟","img":"http://photo.meiduohui.cn/bdd587e4016f3f8c/975189daa1f61d38.jpg","state":"1","shop_quan_count":152}
     */

    private MemInfoBean mem_info;
    private ShopInfoBean shop_info;

    public MemInfoBean getMem_info() {
        return mem_info;
    }

    public void setMem_info(MemInfoBean mem_info) {
        this.mem_info = mem_info;
    }

    public ShopInfoBean getShop_info() {
        return shop_info;
    }

    public void setShop_info(ShopInfoBean shop_info) {
        this.shop_info = shop_info;
    }

    public static class MemInfoBean implements Serializable {
        /**
         * id : 1
         * name : 1
         * money : 3030.27
         * mobile : 18865391811
         * img : http://photo.meiduohui.cn/ed09d05f37d15cee/619c64e67ab8fb24.png
         * shop_id : 1
         * order_count : 1
         * quan_count : 6
         * history_count : 4
         */

        private String id;
        private String name;
        private String money;
        private String mobile;
        private String img;
        private String shop_id;
        private int order_count;
        private int quan_count;
        private int history_count;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getMoney() {
            return money;
        }

        public void setMoney(String money) {
            this.money = money;
        }

        public String getMobile() {
            return mobile;
        }

        public void setMobile(String mobile) {
            this.mobile = mobile;
        }

        public String getImg() {
            return img;
        }

        public void setImg(String img) {
            this.img = img;
        }

        public String getShop_id() {
            return shop_id;
        }

        public void setShop_id(String shop_id) {
            this.shop_id = shop_id;
        }

        public int getOrder_count() {
            return order_count;
        }

        public void setOrder_count(int order_count) {
            this.order_count = order_count;
        }

        public int getQuan_count() {
            return quan_count;
        }

        public void setQuan_count(int quan_count) {
            this.quan_count = quan_count;
        }

        public int getHistory_count() {
            return history_count;
        }

        public void setHistory_count(int history_count) {
            this.history_count = history_count;
        }
    }

    public static class ShopInfoBean implements Serializable {
        /**
         * id : 1
         * name : 马氏杂货铺-临沂分店2000号
         * address : 北园路与沂蒙路交汇北100米路东0.0
         * sjh : 0539-4005289
         * intro : 马氏杂货铺，常来哟
         * img : http://photo.meiduohui.cn/bdd587e4016f3f8c/975189daa1f61d38.jpg
         * state : 1
         * shop_quan_count : 152
         */

        private String id;
        private String name;
        private String address;
        private String sjh;
        private String intro;
        private String img;
        private String state;
        private int shop_quan_count;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
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

        public String getIntro() {
            return intro;
        }

        public void setIntro(String intro) {
            this.intro = intro;
        }

        public String getImg() {
            return img;
        }

        public void setImg(String img) {
            this.img = img;
        }

        public String getState() {
            return state;
        }

        public void setState(String state) {
            this.state = state;
        }

        public int getShop_quan_count() {
            return shop_quan_count;
        }

        public void setShop_quan_count(int shop_quan_count) {
            this.shop_quan_count = shop_quan_count;
        }
    }
}
