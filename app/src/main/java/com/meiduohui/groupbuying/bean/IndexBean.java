package com.meiduohui.groupbuying.bean;

import java.util.List;

public class IndexBean {

    private List<BannerInfoBean> banner_info;
    private List<CatInfoBean> cat_info;
    private List<AdvInfoBean> adv_info;
    private List<MessageInfoBean> message_info;

    public List<BannerInfoBean> getBanner_info() {
        return banner_info;
    }

    public void setBanner_info(List<BannerInfoBean> banner_info) {
        this.banner_info = banner_info;
    }

    public List<CatInfoBean> getCat_info() {
        return cat_info;
    }

    public void setCat_info(List<CatInfoBean> cat_info) {
        this.cat_info = cat_info;
    }

    public List<AdvInfoBean> getAdv_info() {
        return adv_info;
    }

    public void setAdv_info(List<AdvInfoBean> adv_info) {
        this.adv_info = adv_info;
    }

    public List<MessageInfoBean> getMessage_info() {
        return message_info;
    }

    public void setMessage_info(List<MessageInfoBean> message_info) {
        this.message_info = message_info;
    }

    public static class BannerInfoBean {
        /**
         * url : 49
         * img : https://photo.meiduohui.cn/947aeadb7914a8a1/c4daedd82359dcf8.png
         */

        private String url;
        private String img;

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }

        public String getImg() {
            return img;
        }

        public void setImg(String img) {
            this.img = img;
        }
    }

    public static class CatInfoBean {
        /**
         * id : 9
         * name : 吃喝
         * img : https://photo.meiduohui.cn/5822fb5daee0abf3/a271e148424cbe20.png
         */

        private String id;
        private String name;
        private String img;
        private int img2;

        public int getImg2() {
            return img2;
        }

        public void setImg2(int img2) {
            this.img2 = img2;
        }

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

    public static class AdvInfoBean {
        /**
         * content : 这是一条公告测试内容111
         */

        private String content;

        public String getContent() {
            return content;
        }

        public void setContent(String content) {
            this.content = content;
        }
    }

    public static class MessageInfoBean {
        /**
         * order_id : 64
         * yuding : 0
         * cat_id1 : 0
         * cat_id2 : 0
         * beizhu : 来吧
         * m_price : 10.00
         * m_old_price : 20.00
         * title : 辣
         * intro : 来吃
         * img : ["http://photo.meiduohui.cn/c95c4201911131625198538.jpg"]
         * video : null
         * com : 0
         * zan : 0
         * zf : 3
         * shop_id : 11
         * juli : 1.15km
         * shop_name : 河口老字号
         * shop_img : http://photo.meiduohui.cn/4c462201911052314281667.jpg
         * quan_count : 0
         * q_title : null
         * zan_info : 0
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
        private String com;
        private String zan;
        private String zf;
        private String shop_id;
        private String juli;
        private String shop_name;
        private String shop_img;
        private int quan_count;
        private String q_title;
        private int zan_info;
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

        public String getCom() {
            return com;
        }

        public void setCom(String com) {
            this.com = com;
        }

        public String getZan() {
            return zan;
        }

        public void setZan(String zan) {
            this.zan = zan;
        }

        public String getZf() {
            return zf;
        }

        public void setZf(String zf) {
            this.zf = zf;
        }

        public String getShop_id() {
            return shop_id;
        }

        public void setShop_id(String shop_id) {
            this.shop_id = shop_id;
        }

        public String getJuli() {
            return juli;
        }

        public void setJuli(String juli) {
            this.juli = juli;
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

        public int getZan_info() {
            return zan_info;
        }

        public void setZan_info(int zan_info) {
            this.zan_info = zan_info;
        }

        public List<String> getImg() {
            return img;
        }

        public void setImg(List<String> img) {
            this.img = img;
        }
    }
}
