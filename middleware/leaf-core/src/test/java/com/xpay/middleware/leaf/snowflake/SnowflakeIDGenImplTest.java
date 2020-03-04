package com.xpay.middleware.leaf.snowflake;

import com.xpay.middleware.leaf.IDGen;
import com.xpay.middleware.leaf.common.PropertyFactory;
import com.xpay.middleware.leaf.common.Result;
import org.junit.Test;

import java.util.Properties;

public class SnowflakeIDGenImplTest {
    @Test
    public void testGetId() {
        Properties properties = PropertyFactory.getProperties();

        IDGen idGen = new SnowflakeIDGenImpl(properties.getProperty("leaf.zk.list"), 8080, "leafName");
        for (int i = 1; i < 1000; ++i) {
            Result r = idGen.get("a");
            System.out.println(r);
        }
    }
}
