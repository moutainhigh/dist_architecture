package com.xpay.starter.comp.component;

import com.xpay.common.util.utils.JsonUtil;
import com.xpay.common.util.utils.RandomUtil;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

/**
 * redis分布式锁
 */
public class RedisLock {
    private Logger logger = LoggerFactory.getLogger(this.getClass());
    private String lockNamePrefix = "lock-";
    private RedissonClient client;
    private ConcurrentHashMap<String, RLock> longLockMap = new ConcurrentHashMap<>();

    public RedisLock(RedissonClient client){
        if(client == null){
            throw new RuntimeException("RedissonClient 不能为null");
        }
        this.client = client;
    }

    public String getLockNamePrefix() {
        return lockNamePrefix;
    }

    public void setLockNamePrefix(String lockNamePrefix) {
        this.lockNamePrefix = lockNamePrefix;
    }

    public RedissonClient getClient(){
        return client;
    }

    /**
     * 获取锁，适合锁定时间短的业务场景
     * @param lockName             锁名称
     * @param waitMills            获取锁的等待时间
     * @param expireMills          锁的有效时间，单位(毫秒)
     * @return
     */
    public RLock tryLock(String lockName, int waitMills, long expireMills){
        lockName = getRealLockName(lockName);
        RLock lock = getClient().getLock(lockName);
        try{
            if(lock.tryLock(waitMills, expireMills, TimeUnit.MILLISECONDS)){
                return lock;
            }
        }catch(Throwable e){
            logger.error("lockName={} 获取锁时出现异常", lockName, e);
        }
        return null;
    }

    /**
     * 获取锁(长时间锁)，适合锁定时间长的业务场景，如：离线任务处理等
     *  注意：处理完业务逻辑之后，一定要记得调用 {@link #unlockLong(String)} 进行锁释放，否则可能造成死锁又或者内存溢出
     *
     * @param lockName
     * @param waitMills
     * @param expireMinutes 锁定的分钟数
     * @return  如果成功获取到锁，则返回一个clientId，在{@link #unlockLong(String)}释放锁时候需要传入此值
     */
    public String tryLockLong(String lockName, int waitMills, int expireMinutes){
        long expireMills = 0;
        if(expireMinutes < 0){
            expireMills = -1;
        }else{
            expireMills = expireMinutes * 60 * 1000L;
        }

        RLock lock = tryLock(lockName, waitMills, expireMills);
        if(lock == null){
            return null;
        }
        String clientId = RandomUtil.get32LenStr();
        longLockMap.put(clientId, lock);
        return clientId;
    }

    /**
     * 批量获取锁，要么全部获取成功，要么全部获取失败，适合锁定时间短的业务场景
     * @param lockNameList          锁名称
     * @param waitMills             获取锁的等待时间
     * @param expireMills           锁的有效时间
     * @return
     */
    public List<RLock> tryLock(Set<String> lockNameList, int waitMills, long expireMills){
        List<RLock> lockList = new ArrayList<>(lockNameList.size());
        TimeUnit unit = TimeUnit.MILLISECONDS;
        //开始时间
        long startTime = System.currentTimeMillis();
        //剩余时间
        long remainTime = waitMills;
        //获取锁时的真正等待时间
        long awaitTime = 0;

        for(String lockName : lockNameList){
            try{
                lockName = getRealLockName(lockName);
                RLock lock = getClient().getLock(lockName);
                remainTime -= System.currentTimeMillis() - startTime;
                startTime = System.currentTimeMillis();
                awaitTime = Math.max(remainTime, 0);

                if(awaitTime <= 0){
                    logger.error("lockName={} lockNameList = {} 获取锁超时", lockName, JsonUtil.toString(lockNameList));
                    break;
                }else if(lock.tryLock(awaitTime, expireMills, unit)){
                    lockList.add(lock);
                }else{
                    logger.error("lockName={} 获取锁失败", lockName);
                    break;
                }
            }catch(Throwable e){
                logger.error("lockName={} 获取锁时出现异常", lockName, e);
                break;
            }
        }

        //如果其中有任何一个账户获取锁失败，则全部锁释放
        if(lockList.size() != lockNameList.size()){
            unlock(lockList);
            //返回空List
            return new ArrayList<>();
        }
        return lockList;
    }

    /**
     * 批量释放锁，释放锁的线程和加锁的线程必须是同一个才行，如果需要强行释放锁，请查看 {@link #forceUnlock(RLock)}
     * @param lockList
     * @return 如果全部解锁成功，则返回true，否则，返回false
     */
    public void unlock(List<RLock> lockList){
        if(lockList == null || lockList.isEmpty()){
            return;
        }

        for(RLock lock : lockList){
            try{
                unlock(lock);
            }catch(Throwable t){
                logger.error("释放锁异常", t);
            }
        }
    }

    /**
     * 释放锁，释放锁的线程和加锁的线程必须是同一个才行，如果需要强行释放锁，请查看 {@link #forceUnlock(RLock)}
     * @param lock
     * @return
     */
    public void unlock(RLock lock) throws RuntimeException {
        try{
            lock.unlock();
        }catch(Throwable t){
            throw new RuntimeException("lockName = "+lock.getName()+" 释放锁时出现异常", t);
        }
    }

    /**
     * 释放长时间锁
     * @param clientId
     * @throws RuntimeException
     */
    public void unlockLong(String clientId) {
        RLock lock = longLockMap.getOrDefault(clientId, null);
        if(lock != null){
            longLockMap.remove(clientId);
            forceUnlock(lock);//强制释放锁，因为长时间任务往往都是后台线程在处理，所以很有可能获取锁的线程和释放锁的线程不是同一个
        }
    }

    /**
     * 强行释放锁，不管释放锁的线程是不是跟加锁时的线程一样，都可以释放锁
     * @param lock
     * @return
     */
    public void forceUnlock(RLock lock){
        try{
            lock.forceUnlock();
        }catch(Throwable t){
            logger.error("lockName = {} 强制释放锁时出现异常", lock.getName(), t);
        }
    }

    /**
     * 在应用关闭前释放长时间锁
     */
    public void destroy(){
        if(client != null){
            try{
                //waiting for rpc shutdown
                Thread.sleep(2000);
            }catch(Exception e){}

            for(Map.Entry<String, RLock> entry : longLockMap.entrySet()){
                logger.info("lockName={} 应用关闭前强制释放锁", entry.getKey());
                forceUnlock(entry.getValue());
            }
            client.shutdown(3, 7, TimeUnit.SECONDS);
        }
    }

    private String getRealLockName(String lockName){
        return getLockNamePrefix() + lockName;
    }
}
