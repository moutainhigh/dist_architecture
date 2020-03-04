package com.xpay.common.statics.constants.rmqdest;

import java.util.Map;

/**
 * Author: Cmf
 * Date: 2019.12.2
 * Time: 15:11
 * Description:
 */
public class NotifyTestDest {
    public static final String TOPIC_NOTIFY_TEST = "topic_notify_test";

    public static final String TAG_NOTIFY_TEST = "tag_notify_test";

    public static final MqNotifyType<Map> NOTIFY_TYPE_TEST = new MqNotifyType<>(TOPIC_NOTIFY_TEST, TAG_NOTIFY_TEST, 1, "测试使用", Map.class);
}
