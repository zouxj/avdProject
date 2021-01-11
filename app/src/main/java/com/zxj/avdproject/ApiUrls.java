package com.zxj.avdproject;

/**
 * @des:
 * @data: 1/11/21 2:31 PM
 * @Version: 1.0.0
 */
public interface ApiUrls {

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

}
