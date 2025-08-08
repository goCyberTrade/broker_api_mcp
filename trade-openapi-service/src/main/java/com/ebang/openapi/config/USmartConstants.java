package com.ebang.openapi.config;

/**
 * @title:
 * @projectName:
 * @description: TODO
 * @author: shizhibiao
 * @date: 2021/5/12 20:10
 */
public class USmartConstants {

    //UAT交易域名
    public static final String base_url_jy = "https://open-jy-uat.yxzq.com";

    //UAT行情域名
    public static final String base_url_hq = "https://open-hz-uat.yxzq.com";


    // 1.1渠道密码登录
    public static final String login = "/user-server/open-api/login";
    // 1.2获取手机验证码
    public static final String send_phone_captcha = "/user-server/open-api/send-phone-captcha";
    // 1.3渠道验证码登录
    public static final String login_captcha = "/user-server/open-api/loginCaptcha";
    // 1.4设置交易密码
    public static final String set_trade_password = "/user-server/open-api/set-trade-password";
    // 1.5校验交易密码
    public static final String check_trade_password = "/user-server/open-api/check-trade-password";
    // 1.6重置登录密码
    public static final String reset_login_password = "/user-server/open-api/reset-login-password";
    // 1.7解锁交易
    public static final String trade_login = "/user-server/open-api/trade-login";
    // 1.8获取交易解锁状态
    public static final String get_trade_status = "/user-server/open-api/get-trade-status";
    // 1.9修改交易密码
    public static final String update_trade_password = "/user-server/open-api/update-trade-password";
    // 1.10重置交易密码 (该接口经常提示验证码不正确，也会提示非法请求。正常应该都是提示非法请求)
    public static final String reset_trade_password = "/user-server/open-api/reset-trade-password";
    //1.11修改登录密码
    public static final String update_login_password = "/user-server/open-api/update-login-password";
    //1.12根据市场查询账户类型
    public static final String get_user_stock_type = "/user-server/open-api/get-user-info-with-market-for-stock/v1";
    //1.13 根据资金账号查询融资利率
    public static final String get_rate_by_fund_account = "/user-server/open-api/get-rate-info-by-fund-account/v1";


    // 2.1下单
    public static final String order = "/stock-order-server/open-api/entrust-order";
    // 2.2委托改单/撤单
    public static final String modify_order = "/stock-order-server/open-api/modify-order";
    // 2.3改单范围
    public static final String modified_range = "/stock-order-server/open-api/modified-range";
    // 2.4碎股下单
    public static final String odd_entrust = "/stock-order-server/open-api/odd-entrust";
    // 2.5碎股撤单
    public static final String odd_modify = "/stock-order-server/open-api/odd-modify";
    // 2.6最大可买、可卖数量
    public static final String trade_quantity = "/stock-order-server/open-api/trade-quantity";
    // 2.7今日订单-分页查询
    public static final String today_entrust = "/stock-order-server/open-api/today-entrust";
    // 2.8全部订单-分页查询
    public static final String his_entrust = "/stock-order-server/open-api/his-entrust";
    // 2.9查询订单明细
    public static final String order_detail = "/stock-order-server/open-api/order-detail";
    // 2.10查询成交流水-分页查询
    public static final String stock_record = "/stock-order-server/open-api/stock-record";
    // 2.11查询持仓
    public static final String stock_holding = "/stock-order-server/open-api/stock-holding";
    // 2.12查询资产
    public static final String stock_asset = "/stock-order-server/open-api/stock-asset";
    // 2.13客户股票资产查询批量
    public static final String stock_asset_list = "/stock-order-server/open-api/stock-asset-list";
    // 2.14查询聚合资产信息
    public static final String user_asset_aggregation = "/aggregation-server/open-api/user-asset-aggregation/v1";
    // 2.15获取融资股数
    public static final String trade_margin_quantity = "/stock-order-server/open-api/trade-margin-quantity";
    // 2.16客户融资账户详情
    public static final String margin_detail = "/stock-order-server/open-api/margin-detail";


    // 3.1获取IPO列表-分页查询
    public static final String ipo_list = "/stock-order-server/open-api/ipo-list";
    // 3.2获取新股详细信息
    public static final String ipo_info = "/stock-order-server/open-api/ipo-info";
    // 3.3ipo新股认购
    public static final String apply_ipo = "/stock-order-server/open-api/apply-ipo";
    //ipo改单/撤单
    public static final String modify_ipo = "/stock-order-server/open-api/modify-ipo";
    //获取客户ipo申购列表-分页查询
    public static final String ipo_record_list = "/stock-order-server/open-api/ipo-record-list";
    //获取客户ipo申购明细
    public static final String ipo_record = "/stock-order-server/open-api/ipo-record";

    //获取股票抵押比率列表
    public static final String mortgage_list = "/stock-order-server/open-api/mortgage-list";
    //查询汇率
    public static final String currency_exchange_info = " /stock-capital-server/open-api/currency-exchange-info";


    /**********************************  基础行情开放API start  ******************************************/
    // 市场状态接口
    public static final String marketstate = "/quotes-openservice/api/v1/marketstate";

    // 基础信息接口
    public static final String basicinfo = "/quotes-openservice/api/v1/basicinfo";

    // 实时行情接口
    public static final String realtime = "/quotes-openservice/api/v1/realtime";

    // 分时接口
    public static final String timeline = "/quotes-openservice/api/v1/timeline";

    // K线接口
    public static final String kline = "/quotes-openservice/api/v1/kline";

    // 逐笔接口
    public static final String tick = "/quotes-openservice/api/v1/tick";

    // 买卖盘接口
    public static final String orderbook = "/quotes-openservice/api/v1/orderbook";
    /**********************************  基础行情开放API end  ******************************************/

    // 行情推送接入协议接入地址
    public static final String quote_push_url = "wss://open-hz-uat.yxzq.com/wss/v1";


}
