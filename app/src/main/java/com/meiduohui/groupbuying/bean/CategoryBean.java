package com.meiduohui.groupbuying.bean;

import java.util.List;

public class CategoryBean {

    /**
     * id : 9
     * name : 吃喝
     * img : https://photo.meiduohui.cn/420ee590ddb72cc3/2dde3757b9a6e789.png
     * second_info : [{"id":"16","name":"美食","img":"https://photo.meiduohui.cn/179c2c79b2e3fe88/a8983315c82004ed.png"},{"id":"17","name":"水果","img":"https://photo.meiduohui.cn/3a29749b79d405fc/0347bfd21dd048c5.png"},{"id":"18","name":"生鲜","img":"https://photo.meiduohui.cn/f881c7982d4310f2/11e5e761a63aac18.png"},{"id":"19","name":"奶茶","img":"https://photo.meiduohui.cn/9eecfdcdeac1167f/c1f88ddfe01fa29e.png"}]
     */

    private String id;
    private String name;
    private String img;
    private List<SecondInfoBean> second_info;

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

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }

    public List<SecondInfoBean> getSecond_info() {
        return second_info;
    }

    public void setSecond_info(List<SecondInfoBean> second_info) {
        this.second_info = second_info;
    }

    public static class SecondInfoBean {
        /**
         * id : 16
         * name : 美食
         * img : https://photo.meiduohui.cn/179c2c79b2e3fe88/a8983315c82004ed.png
         */

        private String id;
        private String name;
        private String img;

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

        public String getImg() {
            return img;
        }

        public void setImg(String img) {
            this.img = img;
        }
    }
}
