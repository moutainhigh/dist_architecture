package com.xpay.facade.sequence.service;

import java.util.List;

public interface SequenceFacade {

    /**
     * 使用雪花算法生成id序列号(雪花算法id的字符串长度最大为18)
     * 特点：
     *     1、效率高
     *     2、趋势递增，无法绝对单调递增
     *     3、如果系统时钟回拨或者机器Id设置不当，会引起Id序列号重复
     * @return
     */
    public Long nextSnowId();

    /**
     * 使用雪花算法批量生成id序列号(雪花算法id的字符串长度最大为18)
     * @see #nextSnowId()
     * @param count     生成id的个数
     * @return
     */
    public List<Long> nextSnowId(int count);

    /**
     * 使用雪花算法生成id序列号，并可以拼接前缀和日期，适用于业务流水号(雪花算法id的字符串长度最大为18)
     * @see #nextSnowId()
     * @param prefix
     * @param isWithDate
     * @return
     */
    public String nextSnowId(String prefix, boolean isWithDate);

    /**
     * 使用雪花算法批量生成id序列号，并可以拼接前缀和日期，适用于业务流水号(雪花算法id的字符串长度最大为18)
     * @see #nextSnowId(String, boolean)
     * @param count
     * @param prefix
     * @param isWithDate
     * @return
     */
    public List<String> nextSnowId(int count, String prefix, boolean isWithDate);

    /**
     * 使用数据库生成Id序列号
     * 特点：
     *      1、效率没有雪花算法和redis的高
     *      2、绝对单调递增，最大值是long类型的最大长度
     *      3、如果数据库是单机部署，会有单点故障问题，若机器宕机重启，不会存在序列号重复
     *      4、如果要解决单点故障，需要通过PXC等高可用、强一致性集群部署才可解决此问题，门槛较高
     *
     * @param bizTag    业务标识(不要使用中文)
     * @return
     */
    public Long nextSegmentId(String bizTag);

    /**
     * 使用数据库批量生成Id序列号
     * @see #nextSegmentId(String)
     * @param bizTag    业务标识(不要使用中文)
     * @param count     生成id的个数
     * @return
     */
    public List<Long> nextSegmentId(String bizTag, int count);
}
