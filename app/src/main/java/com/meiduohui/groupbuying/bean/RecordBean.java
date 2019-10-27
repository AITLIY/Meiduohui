package com.meiduohui.groupbuying.bean;

import java.util.List;

public class RecordBean {
    /**
     * record_list : [{"mem_id":"1","money_change":"0.10","money_type":"+","money_reason":"用户下单 \u2014 来自测试商家001号","money_time":"2019-10-25 21:12:34"},{"mem_id":"1","money_change":"0.01","money_type":"+","money_reason":"微信在线充值","money_time":"2019-10-23 09:05:42"},{"mem_id":"1","money_change":"16.83","money_type":"+","money_reason":"红包16.83","money_time":"2019-10-22 15:54:31"},{"mem_id":"1","money_change":"0.99","money_type":"-","money_reason":"余额支付 \u2014 给测试商家001号","money_time":"2019-10-21 17:11:20"},{"mem_id":"1","money_change":"0.99","money_type":"-","money_reason":"余额支付 \u2014 给马氏杂货铺-临沂分店2000号","money_time":"2019-10-21 16:49:21"},{"mem_id":"1","money_change":"0.99","money_type":"-","money_reason":"余额支付 \u2014 给马氏杂货铺-临沂分店2000号","money_time":"2019-10-21 16:48:30"},{"mem_id":"1","money_change":"0.10","money_type":"+","money_reason":"用户下单 \u2014 来自马守一","money_time":"2019-10-21 16:47:20"},{"mem_id":"1","money_change":"0.10","money_type":"+","money_reason":"用户下单获得0.1\u2014\u2014木子走刀口","money_time":"2019-10-21 16:29:13"},{"mem_id":"1","money_change":"10.17","money_type":"+","money_reason":"抢红包获得10.17","money_time":"2019-10-16 14:31:01"},{"mem_id":"1","money_change":"0.00","money_type":"+","money_reason":"用户下单获得0.095","money_time":"2019-09-26 14:35:17"}]
     * zhichu : 2.97
     * shouru : 47.72
     */

    private double zhichu;
    private double shouru;
    private List<RecordListBean> record_list;

    public double getZhichu() {
        return zhichu;
    }

    public void setZhichu(double zhichu) {
        this.zhichu = zhichu;
    }

    public double getShouru() {
        return shouru;
    }

    public void setShouru(double shouru) {
        this.shouru = shouru;
    }

    public List<RecordListBean> getRecord_list() {
        return record_list;
    }

    public void setRecord_list(List<RecordListBean> record_list) {
        this.record_list = record_list;
    }

    public static class RecordListBean {
        /**
         * mem_id : 1
         * money_change : 0.10
         * money_type : +
         * money_reason : 用户下单 — 来自测试商家001号
         * money_time : 2019-10-25 21:12:34
         */

        private String mem_id;
        private String money_change;
        private String money_type;
        private String money_reason;
        private String money_time;

        public String getMem_id() {
            return mem_id;
        }

        public void setMem_id(String mem_id) {
            this.mem_id = mem_id;
        }

        public String getMoney_change() {
            return money_change;
        }

        public void setMoney_change(String money_change) {
            this.money_change = money_change;
        }

        public String getMoney_type() {
            return money_type;
        }

        public void setMoney_type(String money_type) {
            this.money_type = money_type;
        }

        public String getMoney_reason() {
            return money_reason;
        }

        public void setMoney_reason(String money_reason) {
            this.money_reason = money_reason;
        }

        public String getMoney_time() {
            return money_time;
        }

        public void setMoney_time(String money_time) {
            this.money_time = money_time;
        }
    }
}
