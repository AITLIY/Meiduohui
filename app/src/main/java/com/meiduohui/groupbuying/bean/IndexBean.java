package com.meiduohui.groupbuying.bean;

import java.util.List;

public class IndexBean {

    /**
     * status : 0
     * msg : 获取成功
     * data : {"banner_info":[{"url":"111","img":"http://photo.meiduohui.cn/de7865f6d583de02/b3238ab96da6df11.jpg"},{"url":"222","img":"http://photo.meiduohui.cn/bbe7f6a80cef23fb/e581a91d64615d0a.JPG"},{"url":"mikasa","img":"http://photo.meiduohui.cn/e3f6e2dbe902660c/6329fbd7ace982b3.jpg"}],"cat_info":[{"id":"9","name":"吃喝","img":"https://photo.meiduohui.cn/420ee590ddb72cc3/2dde3757b9a6e789.png"},{"id":"10","name":"玩乐","img":"https://photo.meiduohui.cn/99a5e2bbf51a503f/638f490742fdff56.png"},{"id":"13","name":"游/住","img":"https://photo.meiduohui.cn/d6a4a24dff5497a9/5cc77a74bdcc1608.png"},{"id":"14","name":"衣/鞋","img":"https://photo.meiduohui.cn/928dfccb126f0250/bb0d9f82711526a7.png"},{"id":"29","name":"电器","img":"https://photo.meiduohui.cn/a34e98fc786e0612/7d4e4af207a95e24.png"},{"id":"32","name":"美妆","img":"https://photo.meiduohui.cn/36287d7b746779cb/6ab22913d5d4f6a4.png"},{"id":"33","name":"家/装","img":"https://photo.meiduohui.cn/ede75b75348e4c3b/f0d245504cb3cacf.png"},{"id":"34","name":"生活","img":"https://photo.meiduohui.cn/ef70cc76b4691f8c/3d6a14251316b83b.png"},{"id":"35","name":"车","img":"https://photo.meiduohui.cn/424505f100a0d325/c0c7b6debc2049d4.png"}],"adv_info":[{"content":"这是一条公告测试内容111"},{"content":"这是一条公告测试内容222"},{"content":"这是一条公告测试内容333333"},{"content":"这是一条公告测试内容444"},{"content":"这是一条公告测试内容555555"}],"message_info":[{"order_id":"30","yuding":"0","beizhu":null,"m_price":"0.10","m_old_price":"0.10","title":"可爱妹妹在线跳舞～","intro":"赶快来瞧瞧吧，蔡依林附体，未来的舞蹈天后，希望大家直观","img":[],"video":"http://photo.meiduohui.cn/aa2a2201909181602175060.mp4","com":"0","zan":"0","zf":"0","shop_id":"1","juli":"35.13km","shop_name":"阿迪达斯北园路店","shop_img":"http://photo.meiduohui.cn/bdd587e4016f3f8c/975189daa1f61d38.jpg","quan_count":0,"q_title":null,"zan_info":0}]}
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
             * img : https://photo.meiduohui.cn/420ee590ddb72cc3/2dde3757b9a6e789.png
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
             * order_id : 30
             * yuding : 0
             * beizhu : null
             * m_price : 0.10
             * m_old_price : 0.10
             * title : 可爱妹妹在线跳舞～
             * intro : 赶快来瞧瞧吧，蔡依林附体，未来的舞蹈天后，希望大家直观
             * img : []
             * video : http://photo.meiduohui.cn/aa2a2201909181602175060.mp4
             * com : 0
             * zan : 0
             * zf : 0
             * shop_id : 1
             * juli : 35.13km
             * shop_name : 阿迪达斯北园路店
             * shop_img : http://photo.meiduohui.cn/bdd587e4016f3f8c/975189daa1f61d38.jpg
             * quan_count : 0
             * q_title : null
             * zan_info : 0
             */

            private String order_id;
            private String yuding;
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
}
