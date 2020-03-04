package com.xpay.service.sequence.service;

import com.xpay.common.statics.exception.BizException;
import com.xpay.common.statics.exception.SequenceExceptions;
import com.xpay.middleware.leaf.IDGen;
import com.xpay.middleware.leaf.common.Result;
import com.xpay.middleware.leaf.common.ZeroIDGen;
import com.xpay.middleware.leaf.snowflake.SnowflakeIDGenImpl;
import com.xpay.service.sequence.config.LeafProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;

@Service("SnowflakeService")
public class SnowflakeService {
    private Logger logger = LoggerFactory.getLogger(SnowflakeService.class);
    IDGen idGen;

    @Autowired
    LeafProperties leafProperties;

    @PostConstruct
    public void init() throws BizException {
        boolean flag = leafProperties.getSnowflake().getEnable();
        if (flag) {
            String zkAddress = leafProperties.getSnowflake().getZkAddress();
            int port = leafProperties.getSnowflake().getZkPort();
            String leafName = leafProperties.getSnowflake().getName();
            idGen = new SnowflakeIDGenImpl(zkAddress, port, leafName);
            if (idGen.init()) {
                logger.info("Snowflake Service Init Successfully");
            } else {
                throw SequenceExceptions.SEQUENCE_COMMON_EXCEPTION.newWithErrMsg("Snowflake Service Init Fail");
            }
        } else {
            idGen = new ZeroIDGen();
            logger.info("Zero ID Gen Service Init Successfully");
        }
    }

    public Result getId() {
        return idGen.get("key");
    }
}
