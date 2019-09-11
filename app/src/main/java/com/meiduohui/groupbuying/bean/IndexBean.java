package com.meiduohui.groupbuying.bean;

import java.util.List;

public class IndexBean {

    /**
     * status : 0
     * msg : 获取成功
     * data : {"banner_info":[{"url":"111","img":"http://photo.meiduohui.cn/de7865f6d583de02/b3238ab96da6df11.jpg"},{"url":"222","img":"http://photo.meiduohui.cn/bbe7f6a80cef23fb/e581a91d64615d0a.JPG"}],"cat_info":[{"id":"3","name":"美食","img":"http://photo.meiduohui.cn/c3f211927af1c526/4d60a122958fe309.jpg"},{"id":"1","name":"服装鞋帽","img":"http://photo.meiduohui.cn/ddff8690efb7c8c4/a8de546a1d6eea96.jpg"},{"id":"6","name":"汽车","img":"http://photo.meiduohui.cn/bbe7f6a80cef23fb/e581a91d64615d0a.JPG"},{"id":"8","name":"体育","img":"http://photo.meiduohui.cn/29e6d14737062a76/bdbc2b49462f8ad6.png"}],"adv_info":[{"content":"这是一条公告测试内容111"},{"content":"这是一条公告测试内容222"},{"content":"这是一条公告测试内容333333"},{"content":"这是一条公告测试内容444"},{"content":"这是一条公告测试内容555555"}],"message_info":[{"order_id":"1","m_price":"8.00","m_old_price":"10.00","title":"100代金券","intro":"111","img":["http://photo.meiduohui.cn/de7865f6d583de02/b3238ab96da6df11.jpg","http://photo.meiduohui.cn/de7865f6d583de02/b3238ab96da6df11.jpg"],"video":"","com":"0","zan":"0","zf":"0","shop_id":"1","juli":"35.13km","shop_name":"111","shop_img":"http://photo.meiduohui.cn/de7865f6d583de02/b3238ab96da6df11.jpg","quan_count":10},{"order_id":"2","m_price":"45.00","m_old_price":"50.00","title":null,"intro":"1111","img":[],"video":"http://photo.meiduohui.cn/24679201908291653077422.mp4","com":"0","zan":"0","zf":"0","shop_id":"3","juli":"35.92km","shop_name":null,"shop_img":null,"quan_count":0}]}
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
             * id : 3
             * name : 美食
             * img : http://photo.meiduohui.cn/c3f211927af1c526/4d60a122958fe309.jpg
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
             * order_id : 1
             * m_price : 8.00
             * m_old_price : 10.00
             * title : 100代金券
             * intro : 111
             * img : ["http://photo.meiduohui.cn/de7865f6d583de02/b3238ab96da6df11.jpg","http://photo.meiduohui.cn/de7865f6d583de02/b3238ab96da6df11.jpg"]
             * video :
             * com : 0
             * zan : 0
             * zf : 0
             * shop_id : 1
             * juli : 35.13km
             * shop_name : 111
             * shop_img : http://photo.meiduohui.cn/de7865f6d583de02/b3238ab96da6df11.jpg
             * quan_count : 10
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
            private String juli;
            private String shop_name;
            private String shop_img;
            private int quan_count;
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

            public List<String> getImg() {
                return img;
            }

            public void setImg(List<String> img) {
                this.img = img;
            }
        }
    }
}
