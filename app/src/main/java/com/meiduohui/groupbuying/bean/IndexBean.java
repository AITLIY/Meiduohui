package com.meiduohui.groupbuying.bean;

import java.util.List;

public class IndexBean {

    /**
     * status : 0
     * msg : 获取成功
     * data : {"banner_info":[{"url":"111","img":"http://photo.meiduohui.cn/de7865f6d583de02/b3238ab96da6df11.jpg"},{"url":"222","img":"http://photo.meiduohui.cn/bbe7f6a80cef23fb/e581a91d64615d0a.JPG"},{"url":"mikasa","img":"http://photo.meiduohui.cn/e3f6e2dbe902660c/6329fbd7ace982b3.jpg"}],"cat_info":[{"id":"9","name":"吃喝","img":"http://photo.meiduohui.cn/cdb8d1e98f976383/c76139045d22184c.png"},{"id":"10","name":"玩乐","img":"http://photo.meiduohui.cn/6512598f6c7e7c8b/f9a96c3030ab87cf.png"},{"id":"13","name":"服饰","img":"http://photo.meiduohui.cn/fffc72ddfa4fe447/5ffe1665d9680607.png"},{"id":"14","name":"酒店","img":"http://photo.meiduohui.cn/27c99046afe531ae/63f659bc662fdf62.png"}],"adv_info":[{"content":"这是一条公告测试内容111"},{"content":"这是一条公告测试内容222"},{"content":"这是一条公告测试内容333333"},{"content":"这是一条公告测试内容444"},{"content":"这是一条公告测试内容555555"}],"message_info":[{"order_id":"29","m_price":"10.00","m_old_price":"30.00","title":"吐血大酬宾，速速来抢","intro":"吃一顿饭只要十块钱，随便吃，随便选，统统只要十块，赶紧来看看吧","img":["http://photo.meiduohui.cn/118fd201909181552121360.png","http://photo.meiduohui.cn/e013a201909181552191997.png","http://photo.meiduohui.cn/9171020190918155225484.png","http://photo.meiduohui.cn/92000201909181552323810.png"],"video":null,"com":"0","zan":"0","zf":"0","shop_id":"1","juli":"35.13km","shop_name":"阿迪达斯北园路店","shop_img":"http://photo.zhongqiukonggu.com/de7865f6d583de02/b3238ab96da6df11.jpg","quan_count":0,"q_title":null},{"order_id":"33","m_price":"10.00","m_old_price":"10.00","title":"想吃什么就吃什么，自己随心选择","intro":"该吃吃该喝喝，屁事不往心里搁，啧啧啧","img":[],"video":"http://photo.meiduohui.cn/0609720190918162219381.mp4","com":"0","zan":"0","zf":"0","shop_id":"1","juli":"35.13km","shop_name":"阿迪达斯北园路店","shop_img":"http://photo.zhongqiukonggu.com/de7865f6d583de02/b3238ab96da6df11.jpg","quan_count":100,"q_title":"5元代金券"}]}
     */

    private int status;
    private String msg;
    private DataBean data;

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public DataBean getData() {
        return data;
    }

    public void setData(DataBean data) {
        this.data = data;
    }

    public static class DataBean {
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
             * url : 111
             * img : http://photo.meiduohui.cn/de7865f6d583de02/b3238ab96da6df11.jpg
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
             * img : http://photo.meiduohui.cn/cdb8d1e98f976383/c76139045d22184c.png
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
             * order_id : 29
             * m_price : 10.00
             * m_old_price : 30.00
             * title : 吐血大酬宾，速速来抢
             * intro : 吃一顿饭只要十块钱，随便吃，随便选，统统只要十块，赶紧来看看吧
             * img : ["http://photo.meiduohui.cn/118fd201909181552121360.png","http://photo.meiduohui.cn/e013a201909181552191997.png","http://photo.meiduohui.cn/9171020190918155225484.png","http://photo.meiduohui.cn/92000201909181552323810.png"]
             * video : null
             * com : 0
             * zan : 0
             * zf : 0
             * shop_id : 1
             * juli : 35.13km
             * shop_name : 阿迪达斯北园路店
             * shop_img : http://photo.zhongqiukonggu.com/de7865f6d583de02/b3238ab96da6df11.jpg
             * quan_count : 0
             * q_title : null
             */

            private String order_id;
            private String m_price;
            private String m_old_price;
            private String title;
            private String intro;
            private Object video;
            private String com;
            private String zan;
            private String zf;
            private String shop_id;
            private String juli;
            private String shop_name;
            private String shop_img;
            private int quan_count;
            private String q_title;
            private List<String> img;

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

            public void String(String q_title) {
                this.q_title = q_title;
            }

            public List<String> getImg() {
                return img;
            }

            public void setImg(List<String> img) {
                this.img = img;
            }
        }
    }
}
