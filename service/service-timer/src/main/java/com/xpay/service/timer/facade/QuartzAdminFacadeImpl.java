package com.xpay.service.timer.facade;

import com.xpay.common.statics.result.PageParam;
import com.xpay.common.statics.result.PageResult;
import com.xpay.facade.timer.entity.Instance;
import com.xpay.facade.timer.entity.Namespace;
import com.xpay.facade.timer.entity.OpLog;
import com.xpay.service.timer.biz.OpLogBiz;
import org.apache.dubbo.config.annotation.Service;
import org.springframework.beans.factory.annotation.Autowired;
import com.xpay.facade.timer.service.QuartzAdminFacade;
import com.xpay.service.timer.biz.NamespaceBiz;
import com.xpay.service.timer.biz.InstanceBiz;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class QuartzAdminFacadeImpl implements QuartzAdminFacade {
    @Autowired
    NamespaceBiz namespaceBiz;
    @Autowired
    InstanceBiz instanceStageBiz;
    @Autowired
    OpLogBiz opLogBiz;

    /**
     * 暂停某个命名空间下的所有实例
     * @return
     */
    @Override
    public boolean pauseAllInstanceAsync(String namespace, String operator){
        return namespaceBiz.pauseNamespace(namespace, operator);
    }

    /**
     * 恢复某个命名空间下被暂停的所有实例
     * @return
     */
    @Override
    public boolean resumeAllInstanceAsync(String namespace, String operator){
        return namespaceBiz.resumeNamespace(namespace, operator);
    }

    /**
     * 取得所有命名空间
     * @return
     */
    @Override
    public List<Namespace> listAllNamespace(){
        return namespaceBiz.listAllNamespace();
    }

    /**
     * 判断命名空间下的所有实例是否都处于暂停状态
     */
    @Override
    public boolean isAllInstancePausing(String namespace){
        return instanceStageBiz.isAllInstancePausing(namespace);
    }

    /**
     * 判断命名空间下的所有实例是否都处于运行状态
     */
    @Override
    public boolean isAllInstanceRunning(String namespace){
        return instanceStageBiz.isAllInstanceRunning(namespace);
    }

    /**
     * 分页查询实例列表
     * @param pageParam
     * @param paramMap
     * @return
     */
    @Override
    public PageResult<List<Instance>> listInstancePage(Map<String, Object> paramMap, PageParam pageParam){
        return instanceStageBiz.listInstancePage(paramMap, pageParam);
    }

    /**
     * 分页查询操作日志
     * @param paramMap
     * @param pageParam
     * @return
     */
    @Override
    public PageResult<List<OpLog>> listOpLogPage(Map<String, Object> paramMap, PageParam pageParam){
        return opLogBiz.listOpLogPage(paramMap, pageParam);
    }
}
