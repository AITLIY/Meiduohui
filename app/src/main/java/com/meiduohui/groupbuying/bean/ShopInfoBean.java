package com.meiduohui.groupbuying.bean;

import java.util.List;

public class ShopInfoBean {

    private MInfoBean m_info;
    private List<MessageMoreBean> message_more;

    public MInfoBean getM_info() {
        return m_info;
    }

    public void setM_info(MInfoBean m_info) {
        this.m_info = m_info;
    }

    public List<MessageMoreBean> getMessage_more() {
        return message_more;
    }

    public void setMessage_more(List<MessageMoreBean> message_more) {
        this.message_more = message_more;
    }

    public static class MInfoBean {
        /**
         * order_id : 48
         * yuding : 0
         * beizhu : 宾利豪车
         请爱惜
         不爱，请不要伤害
         * is_deleted : 0
         * state : 1
         * m_price : 999.00
         * m_old_price : 4590000.00
         * title : 豪华宾利欧陆单座敞篷版
         * intro : 厂商指导价459.00万，现在不要99万，不要9万9，只需要999就能上路开回家， 速速来抢购。另附赠5元宾利购车专用券哦
         * img : ["http://photo.meiduohui.cn/47aee201909272356512693.png","http://photo.meiduohui.cn/81fea201909272357402544.png","http://photo.meiduohui.cn/7e08d201909272358273335.png"]
         * video : null
         * com : 0
         * zan : 0
         * zf : 1
         * shop_id : 10
         * start_time : 1569600000
         * end_time : 1577462400
         * juli : 486.78km
         * shop_name : 测试商家001号
         * shop_img : http://photo.meiduohui.cn/94616201909271517064558.png
         * shop_intro : 这是测试商家测试账号，仅限于测试用0.0
         * address : 临沭县史丹利科技大楼0.0
         * sjh : 15168982855
         * shop_collect_id : 0
         * shop_collect_state : 1
         * shop_collect : 未收藏
         * have_quan : 0
         * state_intro : 有效
         * quan_count : 4
         * q_title : 5元代金券
         * r_id : 14
         * s_quan_info : [{"q_content":"15元代金券","r_id":"10","s_quan_count":97},{"q_content":"8折折扣券","r_id":"13","s_quan_count":7}]
         */

        private String order_id;
        private String yuding;
        private String beizhu;
        private String is_deleted;
        private String state;
        private String m_price;
        private String m_old_price;
        private String title;
        private String intro;
        private Object video;
        private String com;
        private String zan;
        private String zf;
        private String shop_id;
        private String start_time;
        private String end_time;
        private String juli;
        private String shop_name;
        private String shop_img;
        private String shop_intro;
        private String address;
        private String sjh;
        private int shop_collect_id;
        private int shop_collect_state;
        private String shop_collect;
        private int have_quan;
        private String state_intro;
        private int quan_count;
        private String q_title;
        private String r_id;
        private List<String> img;
        private List<SQuanInfoBean> s_quan_info;

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

        public String getBeizhu() {
            return beizhu;
        }

        public void setBeizhu(String beizhu) {
            this.beizhu = beizhu;
        }

        public String getIs_deleted() {
            return is_deleted;
        }

        public void setIs_deleted(String is_deleted) {
            this.is_deleted = is_deleted;
        }

        public String getState() {
            return state;
        }

        public void setState(String state) {
            this.state = state;
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

        public Object getVideo() {
            return video;
        }

        public void setVideo(Object video) {
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

        public String getShop_intro() {
            return shop_intro;
        }

        public void setShop_intro(String shop_intro) {
            this.shop_intro = shop_intro;
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

        public int getShop_collect_id() {
            return shop_collect_id;
        }

        public void setShop_collect_id(int shop_collect_id) {
            this.shop_collect_id = shop_collect_id;
        }

        public int getShop_collect_state() {
            return shop_collect_state;
        }

        public void setShop_collect_state(int shop_collect_state) {
            this.shop_collect_state = shop_collect_state;
        }

        public String getShop_collect() {
            return shop_collect;
        }

        public void setShop_collect(String shop_collect) {
            this.shop_collect = shop_collect;
        }

        public int getHave_quan() {
            return have_quan;
        }

        public void setHave_quan(int have_quan) {
            this.have_quan = have_quan;
        }

        public String getState_intro() {
            return state_intro;
        }

        public void setState_intro(String state_intro) {
            this.state_intro = state_intro;
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

        public String getR_id() {
            return r_id;
        }

        public void setR_id(String r_id) {
            this.r_id = r_id;
        }

        public List<String> getImg() {
            return img;
        }

        public void setImg(List<String> img) {
            this.img = img;
        }

        public List<SQuanInfoBean> getS_quan_info() {
            return s_quan_info;
        }

        public void setS_quan_info(List<SQuanInfoBean> s_quan_info) {
            this.s_quan_info = s_quan_info;
        }

        public static class SQuanInfoBean {
            /**
             * q_content : 15元代金券
             * r_id : 10
             * s_quan_count : 97
             */

            private String q_content;
            private String r_id;
            private int s_quan_count;

            public String getQ_content() {
                return q_content;
            }

            public void setQ_content(String q_content) {
                this.q_content = q_content;
            }

            public String getR_id() {
                return r_id;
            }

            public void setR_id(String r_id) {
                this.r_id = r_id;
            }

            public int getS_quan_count() {
                return s_quan_count;
            }

            public void setS_quan_count(int s_quan_count) {
                this.s_quan_count = s_quan_count;
            }
        }
    }

    public static class MessageMoreBean {
        /**
         * order_id : 44
         * m_price : 0.99
         * m_old_price : 100.00
         * title : 一条小团团最新单曲
         * intro : 糟起来，不要99，不要9块9，只要9毛9你就能带她回家，还犹豫什么，心动不如行动，抓紧抢购吧
         * img : []
         * video : http://photo.meiduohui.cn/d165e20190926143846316.mp4
         * com : 9
         * zan : 2
         * zf : 4
         * shop_id : 10
         * state : 1
         * quan_count : 9
         * q_title : 0.09元代金券
         * state_intro : 有效
         */

        private String order_id;
        private String m_price;
        private String m_old_price;
        private String title;
        private String intro;
        private String video;
        private String com;
        private String zan;
        private String zf;
        private String shop_id;
        private String state;
        private int quan_count;
        private String q_title;
        private String state_intro;
        private List<?> img;

        public String getOrder_id() {
            return order_id;
        }

        public void setOrder_id(String order_id) {
            this.order_id = order_id;
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

        public String getState() {
            return state;
        }

        public void setState(String state) {
            this.state = state;
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

        public List<?> getImg() {
            return img;
        }

        public void setImg(List<?> img) {
            this.img = img;
        }
    }
}
