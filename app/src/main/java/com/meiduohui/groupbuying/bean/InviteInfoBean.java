package com.meiduohui.groupbuying.bean;

public class InviteInfoBean {
    /**
     * invite_money : 0.00
     * invite_mem : 0
     * invite_shop : 0
     * qrcode : https://photo.meiduohui.cn/qrc/b629b0e213061356/3c8d98601020f41b.png
     * content1 : 邀请好友并使用美多惠线上消费,邀请人可获得好友实际消费金额的百分之二的佣会,好友每次消费,邀请者每次都会获得佣金
     * content2 : 邀请商家入驻美多惠,商家在平台发布信息,邀请人可获得商家支付费用的百分之二十的佣金。
     */

    private String invite_money;
    private int invite_mem;
    private int invite_shop;
    private String qrcode;
    private String content1;
    private String content2;

    public String getInvite_money() {
        return invite_money;
    }

    public void setInvite_money(String invite_money) {
        this.invite_money = invite_money;
    }

    public int getInvite_mem() {
        return invite_mem;
    }

    public void setInvite_mem(int invite_mem) {
        this.invite_mem = invite_mem;
    }

    public int getInvite_shop() {
        return invite_shop;
    }

    public void setInvite_shop(int invite_shop) {
        this.invite_shop = invite_shop;
    }

    public String getQrcode() {
        return qrcode;
    }

    public void setQrcode(String qrcode) {
        this.qrcode = qrcode;
    }

    public String getContent1() {
        return content1;
    }

    public void setContent1(String content1) {
        this.content1 = content1;
    }

    public String getContent2() {
        return content2;
    }

    public void setContent2(String content2) {
        this.content2 = content2;
    }
}
