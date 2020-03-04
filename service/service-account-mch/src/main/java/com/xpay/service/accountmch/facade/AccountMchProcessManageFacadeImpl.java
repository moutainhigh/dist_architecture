package com.xpay.service.accountmch.facade;

import com.xpay.common.statics.result.PageParam;
import com.xpay.common.statics.result.PageResult;
import com.xpay.facade.accountmch.entity.AccountMchProcessDetail;
import com.xpay.facade.accountmch.entity.AccountMchProcessPending;
import com.xpay.facade.accountmch.entity.AccountMchProcessResult;
import com.xpay.facade.accountmch.service.AccountMchProcessManageFacade;
import com.xpay.service.accountmch.biz.AccountMchProcessBiz;
import com.xpay.service.accountmch.biz.AccountMchProcessPendingBiz;
import com.xpay.service.accountmch.biz.AccountMchProcessResultBiz;
import com.xpay.service.accountmch.biz.AccountMchQueryBiz;
import org.apache.dubbo.config.annotation.Service;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @author luobinzhao
 * @date 2020/1/14 16:30
 */
@Service
public class AccountMchProcessManageFacadeImpl implements AccountMchProcessManageFacade {
    @Autowired
    private AccountMchProcessBiz accountMchProcessBiz;
    @Autowired
    private AccountMchProcessPendingBiz accountMchProcessPendingBiz;
    @Autowired
    private AccountMchQueryBiz accountMchQueryBiz;
    @Autowired
    private AccountMchProcessResultBiz accountMchProcessResultBiz;

    @Override
    public List<Long> listPendingAccountProcessId(Date createTimeBegin, Date createTimeEnd, Long minId, int number) {
        return accountMchProcessPendingBiz.listPendingAccountProcessId(createTimeBegin, createTimeEnd, minId, number);
    }

    @Override
    public PageResult<List<AccountMchProcessPending>> listProcessPendingPage(Map<String, Object> paramMap, PageParam pageParam, boolean inHistory) {
        return accountMchProcessPendingBiz.listByPage(paramMap, pageParam, inHistory);
    }

    @Override
    public AccountMchProcessPending getProcessPendingById(Long id, boolean inHistory) {
        return accountMchProcessPendingBiz.getAccountProcessPendingById(id, inHistory);
    }

    @Override
    public boolean auditProcessPendingRevertToPending(Long id) {
        return accountMchProcessPendingBiz.auditProcessingToPending(id);
    }

    @Override
    public List<Long> listNeedCallBackResultId(Date createTimeBegin, Long minId, int number) {
        return accountMchProcessResultBiz.listNeedCallBackResultId(createTimeBegin, minId, number);
    }

    @Override
    public PageResult<List<AccountMchProcessResult>> listProcessResultPage(Map<String, Object> paramMap, PageParam pageParam, boolean inHistory) {
        return accountMchProcessResultBiz.listByPage(paramMap, pageParam, inHistory);
    }

    @Override
    public AccountMchProcessResult getProcessResultById(Long id, boolean inHistory) {
        return accountMchProcessResultBiz.getAccountProcessResultById(id, inHistory);
    }

    @Override
    public boolean auditProcessResult(Long id, Integer auditResult) {
        return accountMchProcessResultBiz.auditProcessResult(id, auditResult);
    }

    @Override
    public boolean sendProcessResultCallbackMsg(Long id, boolean isOnlySendMsg, boolean inHistory) {
        return accountMchProcessResultBiz.sendProcessResultCallbackMsg(id, isOnlySendMsg, inHistory);
    }

    @Override
    public PageResult<List<AccountMchProcessDetail>> listProcessDetailPage(Map<String, Object> paramMap, PageParam pageParam, boolean inHistory) {
        return accountMchQueryBiz.listProcessDetailPage(paramMap, pageParam, inHistory);
    }

    @Override
    public AccountMchProcessDetail getProcessDetailById(Long id, boolean inHistory) {
        //todo
        return accountMchQueryBiz.getProcessDetailById(id, inHistory);
    }
}
