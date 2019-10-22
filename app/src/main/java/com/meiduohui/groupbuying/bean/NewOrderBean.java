package com.meiduohui.groupbuying.bean;

import java.util.List;

public class NewOrderBean {

    private MessageInfoBean message_info;
    private List<QuanInfoBean> quan_info;

    public MessageInfoBean getMessage_info() {
        return message_info;
    }

    public void setMessage_info(MessageInfoBean message_info) {
        this.message_info = message_info;
    }

    public List<QuanInfoBean> getQuan_info() {
        return quan_info;
    }

    public void setQuan_info(List<QuanInfoBean> quan_info) {
        this.quan_info = quan_info;
    }

    public static class MessageInfoBean {
        /**
         * order_id : 44
         * title : 一条小团团最新单曲
         * m_price : 0.99
         * m_old_price : 100.00
         * img : []
         * video : http://photo.meiduohui.cn/d165e20190926143846316.mp4
         * intro : 糟起来，不要99，不要9块9，只要9毛9你就能带她回家，还犹豫什么，心动不如行动，抓紧抢购吧
         * state : 1
         * shop_id : 10
         * shop_name : 测试商家001号
         */

        private String order_id;
        private String title;
        private String m_price;
        private String m_old_price;
        private String video;
        private String intro;
        private String state;
        private String shop_id;
        private String shop_name;
        private List<String> img;

        public String getOrder_id() {
            return order_id;
        }

        public void setOrder_id(String order_id) {
            this.order_id = order_id;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
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

        public String getVideo() {
            return video;
        }

        public void setVideo(String video) {
            this.video = video;
        }

        public String getIntro() {
            return intro;
        }

        public void setIntro(String intro) {
            this.intro = intro;
        }

        public String getState() {
            return state;
        }

        public void setState(String state) {
            this.state = state;
        }

        public String getShop_id() {
            return shop_id;
        }

        public void setShop_id(String shop_id) {
            this.shop_id = shop_id;
        }

        public String getShop_name() {
            return shop_name;
        }

        public void setShop_name(String shop_name) {
            this.shop_name = shop_name;
        }

        public List<String> getImg() {
            return img;
        }

        public void setImg(List<String> img) {
            this.img = img;
        }
    }

    public static class QuanInfoBean {
        /**
         * q_id : 515
         * q_content : 0.09元代金券
         * q_type : 1
         * q_state : 0
         * q_price : 0.09
         * m_id : 44
         * shop_id : 10
         * time : 2019-09-26 14:40:57
         * start_time : 1571208348
         * end_time : 1572072348
         * shop_name : 测试商家001号
         * address : 临沭县史丹利科技大楼0.0
         * sjh : 15168982855
         * beizhu : 鸡贼小团团，在线吓人
         注意听歌环境，以免打扰他人
         * title : 一条小团团最新单曲
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
    }
}
