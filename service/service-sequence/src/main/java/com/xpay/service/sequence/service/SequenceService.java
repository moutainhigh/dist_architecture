package com.xpay.service.sequence.service;

import com.xpay.common.statics.exception.SequenceExceptions;
import com.xpay.middleware.leaf.common.Result;
import com.xpay.middleware.leaf.common.Status;
import com.xpay.common.util.utils.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service("SequenceService")
public class SequenceService {
    public final static String SNOW_BIZ_KEY = "key";
    @Autowired
    private SnowflakeService snowflakeService;
    @Autowired
    private SegmentService segmentService;

    public Long nextSnowId() {
        return get(SNOW_BIZ_KEY, snowflakeService.getId());
    }

    public List<Long> nextSnowId(int count) {
        List<Long> idList = new ArrayList<>(count);
        while (count-- > 0) {
            idList.add(nextSnowId());
        }
        return idList;
    }

    public String nextSnowId(String prefix, boolean isWithDate) {
        Long id = nextSnowId();
        if (isWithDate) {
            return prefix + DateUtil.formatShortDate(new Date()) + id;
        } else {
            return prefix + id;
        }
    }

    public List<String> nextSnowId(int count, String prefix, boolean isWithDate) {
        List<String> idStrList = new ArrayList<>(count);
        while (count-- > 0) {
            idStrList.add(nextSnowId(prefix, isWithDate));
        }
        return idStrList;
    }

    public Long nextSegmentId(String bizKey) {
        return get(bizKey, segmentService.getId(bizKey));
    }

    /**
     * 使用数据库批量生成Id序列号
     *
     * @param bizKey
     * @param count
     * @return
     * @see #nextSegmentId(String)
     */
    public List<Long> nextSegmentId(String bizKey, int count) {
        List<Long> idList = new ArrayList<>(count);
        while (count-- > 0) {
            idList.add(nextSegmentId(bizKey));
        }
        return idList;
    }

    private long get(String key, Result id) {
        Result result;
        if (key == null || key.isEmpty()) {
            throw SequenceExceptions.SEQUENCE_COMMON_EXCEPTION.newWithErrMsg("noKey");
        }

        result = id;
        if (result.getStatus().equals(Status.EXCEPTION)) {
            throw SequenceExceptions.SEQUENCE_COMMON_EXCEPTION.newWithErrMsg(result.toString());
        }
        return result.getId();
    }
}
