package com.meiduohui.groupbuying.interfaces;

import com.meiduohui.groupbuying.bean.IndexBean;

public interface IMessageItemClink {

    void onItem(IndexBean.DataBean.MessageInfoBean messageInfoBean);
    void onMedia(IndexBean.DataBean.MessageInfoBean messageInfoBean);
    void onComment(IndexBean.DataBean.MessageInfoBean messageInfoBean);
    void onZF(IndexBean.DataBean.MessageInfoBean messageInfoBean);
    void onZan(IndexBean.DataBean.MessageInfoBean messageInfoBean);
}
