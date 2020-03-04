package com.xpay.common.statics.constants.rmqdest;

import com.xpay.common.statics.dto.merchant.notify.MerchantNotifyDto;

/**
 * Author: Cmf
 * Date: 2019.12.17
 * Time: 15:17
 * Description:商户异步通知
 */
public class MerchantNotifyDest {
    public static final String TOPIC_MERCHANT_NOTIFY = "topic_merchant_notify";
    public static final String TAG_MERCHANT_NOTIFY = "tag_merchant_notify";

    /**
     * 分账完成商户异步通知
     */
    public static final MqNotifyType<MerchantNotifyDto> NOTIFY_TYPE_ALLOCATE_COMPLETE = new MqNotifyType<>(TOPIC_MERCHANT_NOTIFY, TAG_MERCHANT_NOTIFY, 1, "分账完成商户异步通知", MerchantNotifyDto.class);

}
