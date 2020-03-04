package com.xpay.service.accounttransit.facade;

import com.xpay.common.statics.result.PageParam;
import com.xpay.common.statics.result.PageResult;
import com.xpay.facade.accounttransit.entity.AccountTransitProcessDetail;
import com.xpay.facade.accounttransit.entity.AccountTransitProcessPending;
import com.xpay.facade.accounttransit.entity.AccountTransitProcessResult;
import com.xpay.facade.accounttransit.service.AccountTransitProcessManageFacade;
import com.xpay.service.accounttransit.biz.AccountTransitProcessPendingBiz;
import com.xpay.service.accounttransit.biz.AccountTransitProcessResultBiz;
import com.xpay.service.accounttransit.biz.AccountTransitQueryBiz;
import org.apache.dubbo.config.annotation.Service;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Author: Cmf
 * Date: 2019.12.31
 * Time: 18:08
 * Description:在途账户账务处理管理接口
 */
@Service
public class AccountTransitProcessManageFacadeImpl implements AccountTransitProcessManageFacade {
    @Autowired
    private AccountTransitProcessPendingBiz accountTransitProcessPendingBiz;
    @Autowired
    private AccountTransitQueryBiz accountTransitQueryBiz;
    @Autowired
    private AccountTransitProcessResultBiz accountTransitProcessResultBiz;

    @Override
    public List<Long> listPendingAccountProcessId(Date createTimeBegin, Date createTimeEnd, Long minId, int number) {
        return accountTransitProcessPendingBiz.listPendingAccountProcessId(createTimeBegin, createTimeEnd, minId, number);
    }

    @Override
    public PageResult<List<AccountTransitProcessPending>> listProcessPendingPage(Map<String, Object> paramMap, PageParam pageParam, boolean inHistory) {
        return accountTransitProcessPendingBiz.listByPage(paramMap, pageParam, inHistory);
    }

    @Override
    public AccountTransitProcessPending getProcessPendingById(Long id, boolean inHistory) {
        return accountTransitProcessPendingBiz.getAccountProcessPendingById(id, inHistory);
    }

    @Override
    public boolean auditProcessPendingRevertToPending(Long id) {
        return false;
    }

    @Override
    public List<Long> listNeedCallBackResultId(Date createTimeBegin, Long minId, int number) {
        return accountTransitProcessResultBiz.listNeedCallBackResultId(createTimeBegin, minId, number);
    }

    @Override
    public PageResult<List<AccountTransitProcessResult>> listProcessResultPage(Map<String, Object> paramMap, PageParam pageParam, boolean inHistory) {
        return accountTransitProcessResultBiz.listByPage(paramMap, pageParam, inHistory);
    }

    @Override
    public AccountTransitProcessResult getProcessResultById(Long id, boolean inHistory) {
        return accountTransitProcessResultBiz.getAccountProcessResultById(id, inHistory);
    }

    @Override
    public boolean auditProcessResult(Long id, Integer auditResult) {
        return accountTransitProcessResultBiz.auditProcessResult(id, auditResult);
    }

    @Override
    public boolean sendProcessResultCallbackMsg(Long id, boolean isOnlySendMsg, boolean inHistory) {
        return accountTransitProcessResultBiz.sendProcessResultCallbackMsg(id, isOnlySendMsg, inHistory);
    }

    @Override
    public PageResult<List<AccountTransitProcessDetail>> listProcessDetailPage(Map<String, Object> paramMap, PageParam pageParam, boolean inHistory) {
        return accountTransitQueryBiz.listProcessDetailPage(paramMap, pageParam, inHistory);
    }

    @Override
    public AccountTransitProcessDetail getProcessDetailById(Long id, boolean inHistory) {
        //todo
        return accountTransitQueryBiz.getProcessDetailById(id, inHistory);
    }

}
