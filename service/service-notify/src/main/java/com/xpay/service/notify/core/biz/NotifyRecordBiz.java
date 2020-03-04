package com.xpay.service.notify.core.biz;

import com.xpay.common.statics.exceptions.BizException;
import com.xpay.common.statics.result.PageParam;
import com.xpay.common.statics.result.PageResult;
import com.xpay.common.util.utils.JsonUtil;
import com.xpay.common.util.utils.MD5Util;
import com.xpay.facade.notify.entity.NotifyRecord;
import com.xpay.facade.notify.entity.NotifyUnique;
import com.xpay.service.notify.core.dao.NotifyRecordDao;
import com.xpay.service.notify.core.dao.NotifyUniqueDao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
public class NotifyRecordBiz {
    private Logger logger = LoggerFactory.getLogger(NotifyRecordBiz.class);

    @Autowired
    NotifyRecordDao notifyRecordDao;

    @Autowired
    NotifyUniqueDao notifyUniqueDao;

    @Autowired
    NotifyResponseBiz notifyResponseBiz;


    @Transactional(rollbackFor = Exception.class)
    public void createNotifyRecord(NotifyRecord notifyRecord) {
        try {
            NotifyUnique unique = new NotifyUnique();
            String strUnique = "NotifyRecord_" +
                    notifyRecord.getMerchantNo() + "_" +
                    notifyRecord.getTrxNo() + "_" +
                    Optional.ofNullable(notifyRecord.getSubTrxNo()).orElse("") + "_" +
                    Optional.ofNullable(notifyRecord.getThirdTrxNo()).orElse("") + "_" +
                    notifyRecord.getMsgTopic() + "_" +
                    notifyRecord.getMsgTag() + "_" +
                    notifyRecord.getMsgEventType();
            unique.setUniqueKey(MD5Util.getMD5Hex(strUnique));
            unique.setCreateDate(new Date());
            notifyUniqueDao.insert(unique);
            notifyRecordDao.insert(notifyRecord);
        } catch (Exception e) {
            if (e.getMessage().contains("Duplicate") || e.getMessage().contains("UNIQUE_KEY")) {
                logger.info("通知记录唯一约束已存在，不再重复创建,notifyRecord={}", JsonUtil.toString(notifyRecord));
            } else {
                logger.error("创建通知记录失败", e);
                throw new BizException(BizException.BIZ_INVALIDATE, "NotifyRecord保存数据库失败");
            }
        }
    }

    public NotifyRecord getNotifyRecordById(long notifyRecordId) {
        return notifyRecordDao.getById(notifyRecordId);
    }


    public void updateNotifyRecord(NotifyRecord record) {
        notifyRecordDao.update(record);
    }

    /**
     * 更新状态
     *
     * @param id     notifyRecord.id
     * @param status {@link com.xpay.common.statics.constants.common.PublicStatus}
     */
    public void updateNotifyRecordStatus(long id, int status) {
        NotifyRecord record = notifyRecordDao.getById(id);
        if (record == null) {
            logger.error("通知记录不存在,notifyRecord.id={}", id);
            return;
        }
        record.setStatus(status);
        notifyRecordDao.update(record);
    }

    public PageResult<List<NotifyRecord>> listNotifyRecordPage(Map<String, Object> paramMap, PageParam pageParam) {
        if (paramMap == null) {
            paramMap = new HashMap<>();
        }
        return notifyRecordDao.listPage(paramMap, pageParam);
    }



//
//    public NotifyRecord getBy(String merchantNo, String trxNo, Integer notifyType) {
//        return getBy(merchantNo, trxNo, null, notifyType, null);
//    }
//
//    public NotifyRecord getBy(String merchantNo, String trxNo, Integer notifyType, Integer status) {
//        return getBy(merchantNo, trxNo, null, notifyType, status);
//    }
//
//    public NotifyRecord getBy(String merchantNo, String trxNo, String subTrxNo, Integer notifyType, Integer status) {
//        if (StringUtil.isEmpty(trxNo) || notifyType == null) {
//            return null;
//        }
//        Map<String, Object> paramMap = Maps.newHashMap();
//        paramMap.put("merchantNo", merchantNo);
//        paramMap.put("trxNo", trxNo);
//        paramMap.put("subTrxNo", subTrxNo);
//        paramMap.put("notifyType", notifyType);
//        paramMap.put("status", status);
//        return getBy(paramMap);
//    }
//
//    public PageBean listPageNoCount(PageParam pageParam, Map<String, Object> paramMap) {
//        PageBean pageBean = super.listPageNoCount(pageParam, paramMap);
//        List<Object> list = pageBean.getRecordList();
//        if (list != null && !list.isEmpty()) {
//            for (Object obj : list) {
//                NotifyRecord record = (NotifyRecord) obj;
//                record.setResponse(notifyResponseBiz.listByNotifyId(record.getId()));
//            }
//        }
//        return pageBean;
//    }
//
//    /**
//     * 保存通知记录
//     *
//     * @param merchantNo
//     * @param trxNo
//     * @param notifyType
//     * @param destinationName
//     * @param destinationType
//     * @param msg
//     * @param isOverride
//     * @return
//     */
//    public NotifyRecordLog saveRecord(String merchantNo, String trxNo, String subTrxNo, int notifyType, String destinationName,
//                                      DestinationTypeEnum destinationType, String msg, boolean isOverride) {
//        NotifyRecord record = new NotifyRecord();
//        record.setLastNotifyTime(new Date());
//        record.setTrxNo(trxNo);
//        record.setSubTrxNo(subTrxNo);
//        record.setMerchantNo(merchantNo);
//        record.setNotifyType(notifyType);
//        record.setDestination(destinationName);
//        record.setDestinationType(destinationType.getValue());
//        record.setContent(msg);
//        record.setStatus(PublicStatus.ACTIVE);
//        create(record, isOverride);
//
//        NotifyRecordLog recordLog = new NotifyRecordLog();
//        recordLog.setNotifyId(record.getId());
//        recordLog.setStatus(PublicStatus.ACTIVE);
//        recordLog.setContent(record.getContent());
//        notifyRecordLogBiz.insert(recordLog);
//
//        return recordLog;
//    }
//
//    /**
//     * 创建消息报文VO
//     *
//     * @param merchantNo
//     * @param trxNo
//     * @param notifyType
//     * @param destinationName
//     * @param destinationType
//     * @param msg
//     * @return
//     */
//    @Transactional(rollbackFor = Exception.class)
//    public MessageVO createMessageVO(String merchantNo, String trxNo, String subTrxNo, int notifyType, String destinationName,
//                                     DestinationTypeEnum destinationType, String msg, boolean isOverride) {
//        NotifyRecordLog recordLog = saveRecord(merchantNo, trxNo, subTrxNo, notifyType, destinationName, destinationType, msg, isOverride);
//
//        MessageVO message = new MessageVO();
//        message.setMsgId(recordLog.getId());
//        message.setNotifyType(notifyType);
//        message.setContent(recordLog.getContent());
//
//        return message;
//    }
//


//
//    /**
//     * 获取响应
//     *
//     * @param merchantNo
//     * @param trxNo
//     * @param notifyType
//     * @return
//     */
//    public List<NotifyResponse> getNotifyResponseList(String merchantNo, String trxNo, int notifyType) {
//        return Optional.ofNullable(getBy(merchantNo, trxNo, notifyType))
//                .map(notifyRecord -> notifyResponseBiz.listByNotifyId(notifyRecord.getId()))
//                .orElse(null);
//    }
//
//    /**
//     * 检查消息响应，true-成功响应，false-无成功响应
//     *
//     * @param msgId
//     * @return
//     */
//    public boolean checkResponse(long msgId, String rspSign) {
//        return Optional.ofNullable(notifyRecordLogBiz.getById(msgId))
//                .map(recordLog -> notifyResponseBiz.getBy(ImmutableMap.of(
//                        "notifyId", recordLog.getNotifyId(),
//                        "rspSign", rspSign,
//                        "rspStatus", PublicStatus.ACTIVE)))
//                .isPresent();
//    }
}
