package com.xpay.common.statics.result;

import com.xpay.common.statics.dto.es.EsAggDto;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * ES聚合统计结果
 */
public class EsAggResult implements Serializable {
    private static final long serialVersionUID = 1L;
    /**
     * 没有分组时的统计结果，其中key为字段名，value为统计结果
     */
    Map<String, EsAggDto> aggMap = new HashMap<>();

    /**
     * 有分组时的统计结果，其中key为字段名，value为统计结果列表
     */
    Map<String, List<EsAggDto>> aggListMap = new HashMap<>();

    public Map<String, EsAggDto> getAggMap() {
        return aggMap;
    }

    public void setAggMap(Map<String, EsAggDto> aggMap) {
        this.aggMap = aggMap;
    }

    public Map<String, List<EsAggDto>> getAggListMap() {
        return aggListMap;
    }

    public void setAggListMap(Map<String, List<EsAggDto>> aggListMap) {
        this.aggListMap = aggListMap;
    }
}
