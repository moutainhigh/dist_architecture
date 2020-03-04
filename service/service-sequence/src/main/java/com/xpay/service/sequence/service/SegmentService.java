package com.xpay.service.sequence.service;

import com.alibaba.druid.pool.DruidDataSource;
import com.xpay.common.statics.exception.BizException;
import com.xpay.common.statics.exception.SequenceExceptions;
import com.xpay.middleware.leaf.IDGen;
import com.xpay.middleware.leaf.common.Result;
import com.xpay.middleware.leaf.common.ZeroIDGen;
import com.xpay.middleware.leaf.segment.SegmentIDGenImpl;
import com.xpay.middleware.leaf.segment.dao.IDAllocDao;
import com.xpay.middleware.leaf.segment.dao.IDAllocDaoImpl;
import com.xpay.service.sequence.config.LeafProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.sql.SQLException;

@Service("SegmentService")
public class SegmentService {
    private Logger logger = LoggerFactory.getLogger(SegmentService.class);
    IDGen idGen;
    DruidDataSource dataSource;

    @Autowired
    LeafProperties leafProperties;

    @PostConstruct
    public void init() throws SQLException, BizException {
        boolean flag = leafProperties.getSegment().getEnable();
        if (flag) {
            // Config dataSource
            dataSource = new DruidDataSource();
            dataSource.setUrl(leafProperties.getSegment().getJdbcUrl());
            dataSource.setUsername(leafProperties.getSegment().getJdbcUsername());
            dataSource.setPassword(leafProperties.getSegment().getJdbcPassword());
            dataSource.init();

            // Config Dao
            IDAllocDao dao = new IDAllocDaoImpl(dataSource);

            // Config ID Gen
            idGen = new SegmentIDGenImpl();
            ((SegmentIDGenImpl) idGen).setDao(dao);
            if (idGen.init()) {
                logger.info("Segment Service Init Successfully");
            } else {
                throw SequenceExceptions.SEGMENT_SERVICE_INIT_FAIL.newWithErrMsg("Segment Service Init Fail");
            }
        } else {
            idGen = new ZeroIDGen();
            logger.info("Zero ID Gen Service Init Successfully");
        }
    }

    public Result getId(String key) {
        return idGen.get(key);
    }

    public SegmentIDGenImpl getIdGen() {
        if (idGen instanceof SegmentIDGenImpl) {
            return (SegmentIDGenImpl) idGen;
        }
        return null;
    }
}
