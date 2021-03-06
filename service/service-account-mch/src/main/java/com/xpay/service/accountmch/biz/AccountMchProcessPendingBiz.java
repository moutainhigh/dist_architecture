package com.xpay.service.accountmch.biz;

import com.xpay.common.statics.enums.account.AccountProcessPendingStageEnum;
import com.xpay.common.statics.exception.CommonExceptions;
import com.xpay.common.statics.result.PageParam;
import com.xpay.common.statics.result.PageResult;
import com.xpay.facade.accountmch.entity.AccountMchProcessPending;
import com.xpay.facade.accountmch.entity.AccountMchProcessPendingHistory;
import com.xpay.service.accountmch.dao.AccountMchProcessPendingDao;
import com.xpay.service.accountmch.dao.AccountMchProcessPendingHistoryDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author luobinzhao
 * @date 2020/1/14 14:31
 */
@Component
public class AccountMchProcessPendingBiz {
    @Autowired
    private AccountMchProcessPendingDao accountProcessPendingDao;
    @Autowired
    private AccountMchProcessPendingHistoryDao accountMchProcessPendingHistoryDao;

    public void add(AccountMchProcessPending accountMchProcessPending) {
        accountProcessPendingDao.insert(accountMchProcessPending);
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
        AccountMchProcessPending pending = getAccountProcessPendingById(accountProcessPendingId, false);
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

    public PageResult<List<AccountMchProcessPending>> listByPage(Map<String, Object> paramMap, PageParam pageParam, boolean inHistory) {
        if (!inHistory) {
            return accountProcessPendingDao.listPage(paramMap, pageParam);
        } else {
            if (paramMap == null || paramMap.get("createTimeBegin") == null) {
                throw CommonExceptions.BIZ_INVALID.newWithErrMsg("查询历史数据需要传入开始时间参数createTimeBegin");
            }
            PageResult pageResult = accountMchProcessPendingHistoryDao.listPage(paramMap, pageParam);
            List<AccountMchProcessPending> collect = ((List<AccountMchProcessPendingHistory>) pageResult.getData()).stream()
                    .map(AccountMchProcessPending::newFromHistory).collect(Collectors.toList());
            pageResult.setData(collect);
            return pageResult;
        }


    }

    public AccountMchProcessPending getAccountProcessPendingById(Long id, boolean inHistory) {
        if (id == null) {
            return null;
        }
        if (!inHistory) {
            return accountProcessPendingDao.getById(id);
        } else {
            return AccountMchProcessPending.newFromHistory(accountMchProcessPendingHistoryDao.getById(id));
        }
    }
}
