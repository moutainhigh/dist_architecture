package com.xpay.starter.comp.component;

import com.alibaba.csp.sentinel.node.StatisticNode;

/**
 * qps计数器
 */
public class QpsCounter {
    private String resource;
    private StatisticNode statisticNode = new StatisticNode();

    public QpsCounter(String resource){
        this.resource = resource;
    }

    public void addPass(){
        statisticNode.addPassRequest(1);
    }

    public void addBlock(){
        statisticNode.increaseBlockQps(1);
    }

    public void addException(){
        statisticNode.increaseExceptionQps(1);
    }

    public void addRtAndSuccess(){
        statisticNode.addRtAndSuccess(1, 1);
    }

    public double totalQps(){
        return statisticNode.totalQps();
    }

    public double passQps(){
        return statisticNode.passQps();
    }

    public double blockQps() {
        return statisticNode.blockQps();
    }

    public double exceptionQps(){
        return statisticNode.exceptionQps();
    }

    public double avgRt(){
        return statisticNode.avgRt();
    }

    public String getResource() {
        return resource;
    }
}
