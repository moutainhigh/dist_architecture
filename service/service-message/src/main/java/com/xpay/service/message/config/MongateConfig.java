package com.xpay.service.message.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

@Configuration
@ConfigurationProperties("mongate")
public class MongateConfig {
    // 错误码和错误描述映射
    public static Map<String, String> errCodeMap = new HashMap<>();

    private String url;
    private String userid;
    private String password;
    private String subprot;
    private String channelName;
    private String statusUrl;
    private String statusReqType;

    static {
        errCodeMap.put("-1", "参数为空。信息、电话号码等有空指针，登陆失败");
        errCodeMap.put("-12", "有异常电话号码");
        errCodeMap.put("-14", "实际号码个数超过1000");
        errCodeMap.put("-999", "服务器内部错误");
        errCodeMap.put("-10001", "用户登陆不成功(帐号不存在/停用/密码错误)");
        errCodeMap.put("-10003", "用户余额不足");
        errCodeMap.put("-10011", "信息内容超长");
        errCodeMap.put("-10029", "此用户没有权限从此通道发送信息");
        errCodeMap.put("-10030", "不能发送移动号码");
        errCodeMap.put("-10031", "手机号码(段)非法");
        errCodeMap.put("-10057", "IP受限");
        errCodeMap.put("-10056", "连接数超限");
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getSubprot() {
        return subprot;
    }

    public void setSubprot(String subprot) {
        this.subprot = subprot;
    }

    public String getChannelName() {
        return channelName;
    }

    public void setChannelName(String channelName) {
        this.channelName = channelName;
    }

    public String getStatusUrl() {
        return statusUrl;
    }

    public void setStatusUrl(String statusUrl) {
        this.statusUrl = statusUrl;
    }

    public String getStatusReqType() {
        return statusReqType;
    }

    public void setStatusReqType(String statusReqType) {
        this.statusReqType = statusReqType;
    }
}
