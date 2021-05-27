package com.zxj.avdproject;

/**
 * @des:
 * @data: 1/11/21 2:31 PM
 * @Version: 1.0.0
 */

public interface ApiUrls {

    //    /**
//     * 系统类错误，4开头
//     */
//    const ERROR_CODE_40000 = 40000;     //系统繁忙
//    const ERROR_CODE_40001 = 40001;     //非法操作
//    const ERROR_CODE_40002 = 40002;     //缺少参数
//    const ERROR_CODE_40003 = 40003;     //参数异常
//    const ERROR_CODE_40004 = 40004;     //数据不存在
//    const ERROR_CODE_40005 = 40005;     //身份认证失败
//
//    /**
//     * 业务类错误，5开头
//     */
//    const ERROR_CODE_50000 = 50000;     //用户名或密码错误
//    const ERROR_CODE_50001 = 50001;     //账户已禁用
//    const ERROR_CODE_50002 = 50002;     //登录超时
//    const ERROR_CODE_50003 = 50003;     //手机已注册
//    const ERROR_CODE_50004 = 50004;     //渠道不存在
//    const ERROR_CODE_50005 = 50005;     //机器设备异常
//    const ERROR_CODE_50006 = 50006;     //计量已满
//    const ERROR_CODE_50007 = 50007;     //无投放广告
//    const ERROR_CODE_50008 = 50008;     //机器不存在或未激活
//    const ERROR_CODE_50009 = 50009;     //订单未支付
//    const ERROR_CODE_50010 = 50010;     //无待出货订单
//    const ERROR_CODE_50011 = 50011;     //订单状态异常
//    const ERROR_CODE_50012 = 50012;     //机器未绑定，请在小程序绑定机器
//    const ERROR_CODE_50013 = 50013;     //设备不在线
    //重置计量
    String resetNum = "device/resetNum";
    //上报错误
    String reportError = "device/reportError";
    //根据账户获取token
    String accountToken = "device/accountToken";
    //上报数量
    String increase = "device/increase";
    //激活机器
    String active = "device/active";
    //获取规格尺寸
    String getSize = "device/getSize";
    //获取广告
    String getAd = "ad/putAd";
    //获取出货
    String  goods="device/goods";
    //修改机器状态
    String status="device/status";
    //注册机器
    String register="device/register";
    //设置已经出货
    String sell= "device/sell";
    //二维码
    String qrCode= "device/qrCode";
}
