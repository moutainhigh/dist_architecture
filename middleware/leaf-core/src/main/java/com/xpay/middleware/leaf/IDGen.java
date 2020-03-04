package com.xpay.middleware.leaf;

import com.xpay.middleware.leaf.common.Result;

public interface IDGen {
    Result get(String key);
    boolean init();
}
