package com.xpay.service.message.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

@Configuration
@ConfigurationProperties("yunxin")
public class YunXinConfig {
    private String sign;
    private String url;
    private String userCode;
    private String userPass;
    private String channel;
    private String channelName;
    private String statusUrl;

    // 错误码和错误描述映射
    public static Map<String, String> errCodeMap = new HashMap<>();
    static {
        errCodeMap.put("-1", "提交接口错误");
        errCodeMap.put("-3", "用户名或密码错误");
        errCodeMap.put("-4", "短信内容和备案的模板不一样");
        errCodeMap.put("-5", "签名不正确");
        errCodeMap.put("-7", "余额不足");
        errCodeMap.put("-8 ", "通道错误");
        errCodeMap.put("-9 ", "无效号码");
        errCodeMap.put("-10", "签名内容不符合长度");
        errCodeMap.put("-11", "用户有效期过期");
        errCodeMap.put("-12", "黑名单");
        errCodeMap.put("-13", "语音验证码的Amount参数必须是整形字符串");
        errCodeMap.put("-14 ", "语音验证码的内容只能为数字");
        errCodeMap.put("-15", "语音验证码的内容最长为6位");
        errCodeMap.put("-16", "余额请求过于频繁，5秒才能取余额一次");
        errCodeMap.put("-17", "非法IP");
        errCodeMap.put("-23", "解密失败");
    }

    public String getSign() {
        return sign;
    }

    public void setSign(String sign) {
        this.sign = sign;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getUserCode() {
        return userCode;
    }

    public void setUserCode(String userCode) {
        this.userCode = userCode;
    }

    public String getUserPass() {
        return userPass;
    }

    public void setUserPass(String userPass) {
        this.userPass = userPass;
    }

    public String getChannel() {
        return channel;
    }

    public void setChannel(String channel) {
        this.channel = channel;
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
}
