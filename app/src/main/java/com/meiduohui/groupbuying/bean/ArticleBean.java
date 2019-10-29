package com.meiduohui.groupbuying.bean;

public class ArticleBean {
    /**
     * article_info : {"title":"使用帮助","intro":"使用帮助","content":"<p><span style=\"color: rgb(102, 102, 102); font-family: &quot;Microsoft YaHei&quot;, Arial, Helvetica, sans-serif; font-size: 13px;\">在知识经济已经到来，科学技术迅猛发展的今天，社会亟需高素质的优秀人才。我们各类学校是培养技术人才的摇篮，而对新的形势必须转变观念，即由应试教育转向素质教育，由重视单纯的知识传授转到重视学生能力的培养。要想实现这一转变，必须注意教学方法的更新<\/span><\/p>","img":"http://photo.zhongqiukonggu.com/de7865f6d583de02/b3238ab96da6df11.jpg"}
     */

    private ArticleInfoBean article_info;

    public ArticleInfoBean getArticle_info() {
        return article_info;
    }

    public void setArticle_info(ArticleInfoBean article_info) {
        this.article_info = article_info;
    }

    public static class ArticleInfoBean {
        /**
         * title : 使用帮助
         * intro : 使用帮助
         * content : <p><span style="color: rgb(102, 102, 102); font-family: &quot;Microsoft YaHei&quot;, Arial, Helvetica, sans-serif; font-size: 13px;">在知识经济已经到来，科学技术迅猛发展的今天，社会亟需高素质的优秀人才。我们各类学校是培养技术人才的摇篮，而对新的形势必须转变观念，即由应试教育转向素质教育，由重视单纯的知识传授转到重视学生能力的培养。要想实现这一转变，必须注意教学方法的更新</span></p>
         * img : http://photo.zhongqiukonggu.com/de7865f6d583de02/b3238ab96da6df11.jpg
         */

        private String title;
        private String intro;
        private String content;
        private String img;

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

        public String getContent() {
            return content;
        }

        public void setContent(String content) {
            this.content = content;
        }

        public String getImg() {
            return img;
        }

        public void setImg(String img) {
            this.img = img;
        }
    }
}
