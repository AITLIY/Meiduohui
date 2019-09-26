package com.meiduohui.groupbuying.interfaces;

import com.meiduohui.groupbuying.bean.IndexBean;

public interface IMessageItemClink {

    void onItem(IndexBean.MessageInfoBean messageInfoBean);
    void onMedia(IndexBean.MessageInfoBean messageInfoBean);
    void onComment(IndexBean.MessageInfoBean messageInfoBean);
    void onZF(IndexBean.MessageInfoBean messageInfoBean);
    void onZan(IndexBean.MessageInfoBean messageInfoBean);
}
