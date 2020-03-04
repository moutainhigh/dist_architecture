package com.xpay.common.statics.constants.rmqdest;

/**
 * 清结算消息目的地
 */
public class SettleMsgDest {
    public static final String TOPIC_SETTLE_NAME = "topic-settle";
    public static final String TAG_ADVANCE_CLEAR_FINISH = "advanceClearFinish";//日清零
    public static final String TAG_DAILY_SUMMARY = "dailySummary";//日汇总

    public static final long EVENT_DAILY_SUMMARY_ACCOUNT_CALLBACK = 1;//日汇总账务回调
}
