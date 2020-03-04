package com.xpay.common.statics.constants.rmqdest;

public class AccountMsgDest {
    public static final String TOPIC_ACCOUNT_MCH_PROCESS = "topic-accountmch";
    public static final String TAG_ACCOUNT_MCH_PROCESS_ASYNC = "accountMchProcess";//平台商户账务处理
    public static final String TAG_ACCOUNT_MCH_CALLBACK = "accountMchCallback";//平台商户账务处理结果回调
    public static final String TAG_ACCOUNT_MCH_SCHEDULE_TASK = "accountMchScheduleTask"; //平台商户定时任务

    public static final String TOPIC_ACCOUNT_SUB_PROCESS = "topic-accountsub";
    public static final String TAG_ACCOUNT_SUB_PROCESS_ASYNC = "accountSubProcess";//子商户账务处理
    public static final String TAG_ACCOUNT_SUB_CALLBACK = "accountSubCallback"; //子商户账务处理结果回调
    public static final String TAG_ACCOUNT_SUB_SCHEDULE_TASK = "accountSubScheduleTask"; //子商户定时任务


    public static final String TOPIC_ACCOUNT_TRANSIT_PROCESS = "topic-accounttransit";
    public static final String TAG_ACCOUNT_TRANSIT_PROCESS_ASYNC = "accountTransitProcess";//在途账户账务处理
    public static final String TAG_ACCOUNT_TRANSIT_CALLBACK = "accountTransitCallback"; //在途账户账务处理结果回调
    public static final String TAG_ACCOUNT_TRANSIT_SCHEDULE_TASK = "accountTransitScheduleTask"; //在途账户账务定时任务
}
