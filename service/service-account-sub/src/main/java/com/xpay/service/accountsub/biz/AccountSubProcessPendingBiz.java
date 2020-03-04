package com.xpay.service.accountsub.biz;

import com.xpay.common.statics.enums.account.AccountProcessPendingStageEnum;
import com.xpay.common.statics.exception.CommonExceptions;
import com.xpay.common.statics.result.PageParam;
import com.xpay.common.statics.result.PageResult;
import com.xpay.facade.accountsub.entity.AccountSubProcessPending;
import com.xpay.facade.accountsub.entity.AccountSubProcessPendingHistory;
import com.xpay.service.accountsub.dao.AccountSubProcessPendingDao;
import com.xpay.service.accountsub.dao.AccountSubProcessPendingHistoryDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Author: chenyf
 * Date: 2018/3/8
 * Time: 18:24
 * Description:待账务处理逻辑处理层
 */
@Component
public class AccountSubProcessPendingBiz {
    @Autowired
    private AccountSubProcessPendingDao accountProcessPendingDao;
    @Autowired
    private AccountSubProcessPendingHistoryDao accountSubProcessPendingHistoryDao;

    public void add(AccountSubProcessPending accountSubProcessPending) {
        accountProcessPendingDao.insert(accountSubProcessPending);
    }

    /**
     * 更新待账务处理的记录状态
     *
     * @param accountProcessNo 账务处理流水号
     */
    @Transactional(rollbackFor = Exception.class)
    public void updatePendingStatus(String accountProcessNo, AccountProcessPendingStageEnum stageNew, AccountProcessPendingStageEnum stageOld) {
        boolean isSuccess = accountProcessPendingDao.updatePendingStatus(accountProcessNo, stageNew, stageOld);
        if (!isSuccess) {
            //如果更新失败，则需要抛出异常，避免同一笔单多次处理
            throw CommonExceptions.BIZ_INVALID.newWithErrMsg("待账务处理记录更新状态失败");
        }
    }

    /**
     * 把待账务处理记录从处理中审核为待处理
     *
     * @param accountProcessPendingId 待账务处理记录id
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public boolean auditProcessingToPending(Long accountProcessPendingId) {
        AccountSubProcessPending pending = getAccountProcessPendingById(accountProcessPendingId, false);
        if (pending == null) {
            throw CommonExceptions.BIZ_INVALID.newWithErrMsg("待账务处理记录不存在");
        } else if (pending.getProcessStage() == AccountProcessPendingStageEnum.PENDING.getValue()) {
            return true;
        }
        updatePendingStatus(pending.getAccountProcessNo(), AccountProcessPendingStageEnum.PENDING, AccountProcessPendingStageEnum.PROCESSING);
        return true;
    }


    public List<Long> listPendingAccountProcessId(Date createTimeBegin, Date createTimeEnd, Long minId, int number) {
        if (createTimeBegin == null || number == 0) {
            return Collections.emptyList();
        }
        return accountProcessPendingDao.listPendingAccountProcessId(createTimeBegin, createTimeEnd, minId, number);
    }

    public PageResult<List<AccountSubProcessPending>> listByPage(Map<String, Object> paramMap, PageParam pageParam, boolean inHistory) {
        if (!inHistory) {
            return accountProcessPendingDao.listPage(paramMap, pageParam);
        } else {
            if (paramMap == null || paramMap.get("createTimeBegin") == null) {
                throw CommonExceptions.BIZ_INVALID.newWithErrMsg("查询历史数据需要传入开始时间参数createTimeBegin");
            }
            PageResult pageResult = accountSubProcessPendingHistoryDao.listPage(paramMap, pageParam);
            List<AccountSubProcessPending> collect = ((List<AccountSubProcessPendingHistory>) pageResult.getData()).stream()
                    .map(AccountSubProcessPending::newFromHistory).collect(Collectors.toList());
            pageResult.setData(collect);
            return pageResult;
        }


    }

    public AccountSubProcessPending getAccountProcessPendingById(Long id, boolean inHistory) {
        if (id == null) {
            return null;
        }
        if (!inHistory) {
            return accountProcessPendingDao.getById(id);
        } else {
            return AccountSubProcessPending.newFromHistory(accountSubProcessPendingHistoryDao.getById(id));
        }
    }
}
