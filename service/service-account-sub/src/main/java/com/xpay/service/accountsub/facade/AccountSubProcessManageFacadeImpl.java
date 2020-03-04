package com.xpay.service.accountsub.facade;

import com.xpay.common.statics.result.PageParam;
import com.xpay.common.statics.result.PageResult;
import com.xpay.facade.accountsub.entity.AccountSubProcessDetail;
import com.xpay.facade.accountsub.entity.AccountSubProcessPending;
import com.xpay.facade.accountsub.entity.AccountSubProcessResult;
import com.xpay.facade.accountsub.service.AccountSubProcessManageFacade;
import com.xpay.service.accountsub.biz.AccountSubProcessBiz;
import com.xpay.service.accountsub.biz.AccountSubProcessPendingBiz;
import com.xpay.service.accountsub.biz.AccountSubProcessResultBiz;
import com.xpay.service.accountsub.biz.AccountSubQueryBiz;
import org.apache.dubbo.config.annotation.Service;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Author: Cmf
 * Date: 2019.12.31
 * Time: 18:08
 * Description:子商户账务处理管理接口
 */
@Service
public class AccountSubProcessManageFacadeImpl implements AccountSubProcessManageFacade {
    @Autowired
    private AccountSubProcessBiz accountSubProcessBiz;
    @Autowired
    private AccountSubProcessPendingBiz accountSubProcessPendingBiz;
    @Autowired
    private AccountSubQueryBiz accountSubQueryBiz;
    @Autowired
    private AccountSubProcessResultBiz accountSubProcessResultBiz;

    @Override
    public List<Long> listPendingAccountProcessId(Date createTimeBegin, Date createTimeEnd, Long minId, int number) {
        return accountSubProcessPendingBiz.listPendingAccountProcessId(createTimeBegin, createTimeEnd, minId, number);
    }

    @Override
    public PageResult<List<AccountSubProcessPending>> listProcessPendingPage(Map<String, Object> paramMap, PageParam pageParam, boolean inHistory) {
        return accountSubProcessPendingBiz.listByPage(paramMap, pageParam, inHistory);
    }

    @Override
    public AccountSubProcessPending getProcessPendingById(Long id, boolean inHistory) {
        return accountSubProcessPendingBiz.getAccountProcessPendingById(id, inHistory);
    }

    @Override
    public boolean auditProcessPendingRevertToPending(Long id) {
        return false;
    }

    @Override
    public List<Long> listNeedCallBackResultId(Date createTimeBegin, Long minId, int number) {
        return accountSubProcessResultBiz.listNeedCallBackResultId(createTimeBegin, minId, number);
    }

    @Override
    public PageResult<List<AccountSubProcessResult>> listProcessResultPage(Map<String, Object> paramMap, PageParam pageParam, boolean inHistory) {
        return accountSubProcessResultBiz.listByPage(paramMap, pageParam, inHistory);
    }

    @Override
    public AccountSubProcessResult getProcessResultById(Long id, boolean inHistory) {
        return accountSubProcessResultBiz.getAccountProcessResultById(id, inHistory);
    }

    @Override
    public boolean auditProcessResult(Long id, Integer auditResult) {
        return accountSubProcessResultBiz.auditProcessResult(id, auditResult);
    }

    @Override
    public boolean sendProcessResultCallbackMsg(Long id, boolean isOnlySendMsg, boolean inHistory) {
        return accountSubProcessResultBiz.sendProcessResultCallbackMsg(id, isOnlySendMsg, inHistory);
    }

    @Override
    public PageResult<List<AccountSubProcessDetail>> listProcessDetailPage(Map<String, Object> paramMap, PageParam pageParam, boolean inHistory) {
        return accountSubQueryBiz.listProcessDetailPage(paramMap, pageParam, inHistory);
    }

    @Override
    public AccountSubProcessDetail getProcessDetailById(Long id, boolean inHistory) {
        //todo
        return accountSubQueryBiz.getProcessDetailById(id, inHistory);
    }

}
