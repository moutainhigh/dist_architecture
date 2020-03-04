package com.xpay.service.migration.listener;

import com.xpay.common.statics.constants.rmqdest.MigrateMsgDest;
import com.xpay.common.statics.dto.migrate.MigrateParamDto;
import com.xpay.common.statics.dto.rmq.MsgDto;
import com.xpay.common.statics.exceptions.BizException;
import com.xpay.common.util.utils.JsonUtil;
import com.xpay.service.migration.biz.DataMigrateHelper;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class MigrateMsgListener {
    @Autowired
    DataMigrateHelper dataMigrateHelper;

    @Component
    @RocketMQMessageListener(topic = MigrateMsgDest.TOPIC_DATA_MIGRATION, selectorExpression = "*", consumerGroup = "service-migration", consumeThreadMax = 60)
    public class dataMigrateConsumer implements RocketMQListener<MsgDto> {
        public void onMessage(MsgDto msgDto) {
            try {
                dataMigrateHelper.doMigration(JsonUtil.toBean(msgDto.getJsonParam(), MigrateParamDto.class));
            } catch (BizException e) {
                if (e.getSysErrCode() != BizException.MQ_DISCARDABLE) {
                    throw e;
                }
            }
        }
    }
}
