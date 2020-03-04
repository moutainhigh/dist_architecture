package com.xpay.common.statics.enums.rmq;

/**
 * Author: Cmf
 * Date: 2019.12.16
 * Time: 15:02
 * Description:
 */
public enum MsgDtoHeaderEnum {
    NOTIFY_RECORD_ID("NOTIFY_RECORD_ID"),
    ;
    private String headerKey;

    MsgDtoHeaderEnum(String headerKey) {
        this.headerKey = headerKey;
    }

    public String getHeaderKey() {
        return headerKey;
    }
}
