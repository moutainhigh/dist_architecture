package com.xpay.service.timer.biz;

import com.xpay.common.statics.exceptions.BizException;
import com.xpay.common.util.utils.StringUtil;
import com.xpay.facade.timer.entity.Namespace;
import com.xpay.facade.timer.enums.OpTypeEnum;
import com.xpay.facade.timer.enums.TimerStatus;
import com.xpay.service.timer.dao.NamespaceDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class NamespaceBiz {
    @Autowired
    OpLogBiz opLogBiz;
    @Autowired
    NamespaceDao namespaceDao;

    /**
     * 把命名空间的状态设为'挂起中'
     *
     * @return
     * @see #resumeNamespace(String, String)
     */
    public boolean pauseNamespace(String namespace, String operator) {
        if (StringUtil.isEmpty(namespace) || StringUtil.isEmpty(operator)) {
            throw new BizException("namespace和operator不能为空");
        }
        boolean isSuccess = namespaceDao.updateStatus(TimerStatus.STAND_BY.getValue(), namespace);
        if (isSuccess) {
            opLogBiz.addAsync(operator, namespace, OpTypeEnum.PAUSE, "挂起分区");
        }
        return isSuccess;
    }

    /**
     * 把命名空间的状态恢复为'运行中'
     *
     * @return
     * @see #pauseNamespace(String, String)
     */
    public boolean resumeNamespace(String namespace, String operator) {
        if (StringUtil.isEmpty(namespace) || StringUtil.isEmpty(operator)) {
            throw new BizException("namespace和operator不能为空");
        }

        boolean isSuccess = namespaceDao.updateStatus(TimerStatus.RUNNING.getValue(), namespace);
        if (isSuccess) {
            opLogBiz.addAsync(operator, namespace, OpTypeEnum.RESUME, "恢复分区");
        }
        return isSuccess;
    }

    public List<Namespace> listAllNamespace() {
        return namespaceDao.listAll();
    }

    public Namespace getByName(String name) {
        if (StringUtil.isEmpty(name)) {
            return null;
        }
        return namespaceDao.getById(name);
    }

    public boolean addNamespace(String name, String nameCn, String operator) {
        Namespace namespace = new Namespace();
        namespace.setNamespace(name);
        namespace.setNameCn(nameCn);
        namespace.setStatus(TimerStatus.RUNNING.getValue());
        try {
            namespaceDao.insert(namespace);
        } catch (Exception ex) {
            throw new BizException("namespace=" + name + "命名空间添加失败");
        }
        opLogBiz.addAsync(operator, name, OpTypeEnum.ADD, "新增分区");
        return true;
    }
}
