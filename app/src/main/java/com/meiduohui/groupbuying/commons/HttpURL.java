package com.meiduohui.groupbuying.commons;

/**
 * 访问服务器的网址
 * Created by ALIY on 2018/12/9 0009.
 */

public class HttpURL {

    // baseUrl
    public static final String BASE_URL = "https://api.meiduohui.cn/api/";

    // 首页数据
    public static final String INDEX_INDEX = "index/index";

    // 店铺分类 一级
    public static final String CAT_FIRST = "cat/first";

    // 店铺分类 二级
    public static final String CAT_SECOND = "cat/second";

    // 信息详情&&商户详情
    public static final String SHOP_SHOPINFO = "shop/shopInfo";

    // 优惠券列表&&优惠券详情
    public static final String MEM_QUANLIST = "mem/quanList";


    // 个人中心首页
    public static final String MEM_MEMINFO = "mem/memInfo";

    // 商户申请
    public static final String SHOP_APPLY = "shop/apply";

    // 浏览记录
    public static final String MEM_HISTORYLIST = "mem/historyList";

    // 删除浏览记录
    public static final String MEM_HISTORYDEL = "mem/historyDel";

    // 订单列表
    public static final String MEM_ORDERLIST = "mem/orderList";

    // 收藏店家列表
    public static final String MEM_COLLECTLIST = "mem/collectList";

    // 收藏商户
    public static final String MEM_COLLECT = "mem/collect";

    // 删除收藏商户
    public static final String MEM_COLLECTDEL = "mem/collectDel";

    // 点赞
    public static final String ORDER_ADDZAN = "order/addZan";

    // 添加评论
    public static final String COMMENT_ADDCOMMENT = "comment/addComment";

    // 评论列表
    public static final String COMMENT_COMMENTLIST = "comment/commentList";


    // 获取券
    public static final String ORDER_GETQUANL = "order/getQuan";

    // 购买信息详情
    public static final String ORDER_ORDER = "order/order";

    // 购买信息下单
    public static final String ORDER_ADDORDER = "order/addOrder";

    // 发起支付
    public static final String PAY_TOPAY = "pay/toPay";

    // 获取订单状态
    public static final String PAY_GETORDERSTATE = "pay/getOrderState";

    // 取消订单
    public static final String ORDER_CANCELORDER = "order/cancelOrder";

    // 删除订单
    public static final String ORDER_DELORDER = "order/delOrder";


    // 发布信息
    public static final String ORDER_ADDMESSAGE = "order/addMessage";

    // 发布通用券
    public static final String SHOP_ADDQUAN = "shop/addQuan";

    // 获取红包
    public static final String SHOP_REDINFO = "shop/redInfo";

    // 发布红包
    public static final String SHOP_SENDRED = "shop/sendRed";

    // 抢红包
    public static final String SHOP_GETRED = "shop/getRed";


    // 钱包
    public static final String MEM_WALLET = "mem/wallet";

    // 在线充值
    public static final String ORDER_ADDMONEY = "order/addMoney";

    // 提现
    public static final String MEM_WITHDRAWAL = "mem/withdrawal";

    // 提现记录
    public static final String MEM_WITHDRAWALLIST = "mem/withdrawalList";

    // 资金流水
    public static final String MEM_RECORD = "mem/record";


    // 订单核销
    public static final String ORDER_WRITEOFF = "order/writeOff";

    // 通用券核销
    public static final String ORDER_WRITEOFFQUAN = "order/writeOffQuan";

    // 生成订单核销二维码
    public static final String ORDER_ORDERQRCODE = "order/orderQrcode";

    // 生成通用券核销二维码
    public static final String ORDER_QUANQRCODE = "order/quanQrcode";

    // 邀请信息&&生成海报二维码
    public static final String MEM_INVITEINFO = "mem/inviteInfo";


    // 登录
    public static final String LOGIN_LOGIN = "login/login";

    // 微信登录
    public static final String LOGIN_WXLOGIN = "login/wxLogin";

    // 注册
    public static final String LOGIN_REGISTER = "login/register";

    // 绑定手机号
    public static final String LOGIN_BINDMOBILE = "login/bindMobile";

    // 获取验证码
    public static final String LOGIN_GETCAPTCHA = "login/getCaptcha";


    // 忘记密码&&修改密码
    public static final String SET_CHANGEPASS = "set/changePass";

    // 修改信息
    public static final String SET_CHANGEINFO = "set/changeInfo";

    // 获取状态值
    public static final String SET_STATEARRAY = "set/stateArray";

    // 系统配置
    public static final String SET_CONFIG = "set/config";

    // 文章详情
    public static final String SET_ARTICLE = "set/article";

    // 图片视频上传
    public static final String UPLOAD_UPLOAD = "upload/upload";

    // 图片视频删除
    public static final String UPLOAD_DELIMG = "upload/delImg";

}
