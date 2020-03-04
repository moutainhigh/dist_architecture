package com.xpay.middleware.leaf.segment.dao;

import com.xpay.middleware.leaf.segment.model.LeafAlloc;

import java.util.List;

public interface IDAllocDao {
     List<LeafAlloc> getAllLeafAllocs();
     LeafAlloc updateMaxIdAndGetLeafAlloc(String tag);
     LeafAlloc updateMaxIdByCustomStepAndGetLeafAlloc(LeafAlloc leafAlloc);
     List<String> getAllTags();
     int addBizTag(LeafAlloc leafAlloc);
}
