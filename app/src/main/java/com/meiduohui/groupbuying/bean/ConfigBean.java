package com.meiduohui.groupbuying.bean;

import java.util.List;

public class ConfigBean {
    private List<Config> config;

    public List<Config> getConfig() {
        return config;
    }

    public void setConfig(List<Config> config) {
        this.config = config;
    }

    public static class Config {
        /**
         * app_name : 美多惠
         * app_version : 1.0.0(1)
         * site_mobile : 05396388888
         * site_name : 山东美多惠信息技术有限公司
         * site_copy : Copyright©2019
         */

        private String app_name;
        private String app_version;
        private String site_mobile;
        private String site_name;
        private String site_copy;

        public String getApp_name() {
            return app_name;
        }

        public void setApp_name(String app_name) {
            this.app_name = app_name;
        }

        public String getApp_version() {
            return app_version;
        }

        public void setApp_version(String app_version) {
            this.app_version = app_version;
        }

        public String getSite_mobile() {
            return site_mobile;
        }

        public void setSite_mobile(String site_mobile) {
            this.site_mobile = site_mobile;
        }

        public String getSite_name() {
            return site_name;
        }

        public void setSite_name(String site_name) {
            this.site_name = site_name;
        }

        public String getSite_copy() {
            return site_copy;
        }

        public void setSite_copy(String site_copy) {
            this.site_copy = site_copy;
        }
    }
}
