package com.xpay.service.sequence.facade;

import com.xpay.facade.sequence.service.SequenceFacade;
import com.xpay.service.sequence.service.SequenceService;
import org.apache.dubbo.config.annotation.Service;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

@Service
public class SequenceFacadeImpl implements SequenceFacade {
    @Autowired
    SequenceService sequenceService;

    @Override
    public Long nextSnowId() {
        return sequenceService.nextSnowId();
    }

    @Override
    public List<Long> nextSnowId(int count) {
        return sequenceService.nextSnowId(count);
    }

    @Override
    public String nextSnowId(String prefix, boolean isWithDate) {
        return sequenceService.nextSnowId(prefix, isWithDate);
    }

    @Override
    public List<String> nextSnowId(int count, String prefix, boolean isWithDate){
        return sequenceService.nextSnowId(count, prefix, isWithDate);
    }

    public Long nextSegmentId(String bizKey){
        return sequenceService.nextSegmentId(bizKey);
    }

    /**
     * 使用数据库批量生成Id序列号
     * @see #nextSegmentId(String)
     * @param bizKey
     * @param count
     * @return
     */
    public List<Long> nextSegmentId(String bizKey, int count){
        return sequenceService.nextSegmentId(bizKey, count);
    }
}
