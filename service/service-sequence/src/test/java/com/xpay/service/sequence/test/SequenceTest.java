package com.xpay.service.sequence.test;

import com.xpay.common.util.utils.JsonUtil;
import com.xpay.facade.sequence.service.SequenceFacade;
import org.apache.dubbo.config.annotation.Reference;
import org.junit.Test;

import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;

public class SequenceTest extends BaseTestCase {
    @Reference
    SequenceFacade sequenceFacade;

    @Test
    public void testSnowSeq(){
        int i = 1, max = 3000000;

        long start = System.currentTimeMillis();
        while(i <= max){
            i++;
            Long id = sequenceFacade.nextSnowId();
//            System.out.println("index="+i+",id="+id);
        }
        System.out.println("testSnowSeq max = " + max + ", time cost mills = " + (System.currentTimeMillis() - start));
    }

    @Test
    public void testSegmentSeq(){
        String key = "TEST_DB_SEQ";
        int i = 1, max = 30000;

        long start = System.currentTimeMillis();
        while(i <= max){
            i++;
            Long id = sequenceFacade.nextSegmentId(key);
            System.out.println("index="+i+",id="+id);
        }
        System.out.println("testSegmentSeq max = " + max + ", time cost mills = " + (System.currentTimeMillis() - start));
    }

    @Test
    public void testSegmentSeq2(){
        String key = "TEST_DB_SEQ";

        long start = System.currentTimeMillis();
        int threadCount = 8, maxPerThread = 30000;
        CountDownLatch countDown = new CountDownLatch(threadCount);
        AtomicInteger totalCount = new AtomicInteger(0);
        while(threadCount-- > 0){
            new Thread(new Runnable() {
                @Override
                public void run() {
                    int i = 1;
                    while(i++ <= maxPerThread){
                        Long id = sequenceFacade.nextSegmentId(key);
                        System.out.println("totalCount="+totalCount.incrementAndGet()+",id="+id);
                    }
                    countDown.countDown();;
                }
            }).start();
        }

        try{
            countDown.await();
        }catch(Exception e){
            e.printStackTrace();
        }

        long timeCost = System.currentTimeMillis() - start;
        double qps = totalCount.get() / (timeCost/1000);
        System.out.println("testSegmentSeq totalCount = " + totalCount.get() + " timeCost = " + timeCost + " qps = " + qps);
    }

    @Test
    public void testBatchSegmentSeq(){
        String key = "TEST_DB_SEQ";
        List<Long> idList = sequenceFacade.nextSegmentId(key, 5000);
        System.out.println("idList = " + JsonUtil.toString(idList));
    }

}
