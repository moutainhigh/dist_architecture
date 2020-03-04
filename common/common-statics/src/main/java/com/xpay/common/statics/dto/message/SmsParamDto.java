package com.xpay.common.statics.dto.message;

import com.xpay.common.statics.enums.message.SmsPlatformEnum;
import com.xpay.common.statics.enums.message.SmsTplLocalEnum;
import com.xpay.common.statics.enums.message.SmsTplPlatEnum;
import com.xpay.common.statics.enums.message.SmsTplTypeEnum;

import java.io.Serializable;
import java.util.Map;

public class SmsParamDto implements Serializable {
    /**
     * 收信人手机号
     */
    private String phone;

    /**
     * 模版类型
     */
    private SmsTplTypeEnum tplType = SmsTplTypeEnum.PLATFORM;

    /**
     * 模版名称
     * @see SmsTplLocalEnum
     * @see SmsTplPlatEnum
     */
    private String tplName;

    /**
     * 模版中的参数
     */
    private Map<String, Object> tplParam;

    /**
     * 短信运营平台
     */
    private SmsPlatformEnum platform = SmsPlatformEnum.ALI_YUN;

    /**
     * 业务编码或流水号
     */
    private String bizKey;

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public SmsTplTypeEnum getTplType() {
        return tplType;
    }

    public void setTplType(SmsTplTypeEnum tplType) {
        this.tplType = tplType;
    }

    public String getTplName() {
        return tplName;
    }

    public void setTplName(String tplName) {
        this.tplName = tplName;
    }

    public Map<String, Object> getTplParam() {
        return tplParam;
    }

    public void setTplParam(Map<String, Object> tplParam) {
        this.tplParam = tplParam;
    }

    public SmsPlatformEnum getPlatform() {
        return platform;
    }

    public void setPlatform(SmsPlatformEnum platform) {
        this.platform = platform;
    }

    public String getBizKey() {
        return bizKey;
    }

    public void setBizKey(String bizKey) {
        this.bizKey = bizKey;
    }
}
