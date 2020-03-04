package com.xpay.service.accounttransit.biz;

import com.xpay.common.statics.enums.account.AccountProcessPendingStageEnum;
import com.xpay.common.statics.exception.CommonExceptions;
import com.xpay.common.statics.result.PageParam;
import com.xpay.common.statics.result.PageResult;
import com.xpay.facade.accounttransit.entity.AccountTransitProcessPending;
import com.xpay.facade.accounttransit.entity.AccountTransitProcessPendingHistory;
import com.xpay.service.accounttransit.dao.AccountTransitProcessPendingDao;
import com.xpay.service.accounttransit.dao.AccountTransitProcessPendingHistoryDao;
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
public class AccountTransitProcessPendingBiz {
    @Autowired
    private AccountTransitProcessPendingDao accountTransitProcessPendingDao;
    @Autowired
    private AccountTransitProcessPendingHistoryDao accountTransitProcessPendingHistoryDao;

    public void add(AccountTransitProcessPending accountTransitProcessPending) {
        accountTransitProcessPendingDao.insert(accountTransitProcessPending);
    }

    /**
     * 更新待账务处理的记录状态
     *
     * @param accountProcessNo 账务处理流水号
     */
    @Transactional(rollbackFor = Exception.class)
    public void updatePendingStatus(String accountProcessNo, AccountProcessPendingStageEnum stageNew, AccountProcessPendingStageEnum stageOld) {
        boolean isSuccess = accountTransitProcessPendingDao.updatePendingStatus(accountProcessNo, stageNew, stageOld);
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
        AccountTransitProcessPending pending = getAccountProcessPendingById(accountProcessPendingId, false);
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
        return accountTransitProcessPendingDao.listPendingAccountProcessId(createTimeBegin, createTimeEnd, minId, number);
    }

    public PageResult<List<AccountTransitProcessPending>> listByPage(Map<String, Object> paramMap, PageParam pageParam, boolean inHistory) {
        if (!inHistory) {
            return accountTransitProcessPendingDao.listPage(paramMap, pageParam);
        } else {
            if (paramMap == null || paramMap.get("createTimeBegin") == null) {
                throw CommonExceptions.BIZ_INVALID.newWithErrMsg("查询历史数据需要传入开始时间参数createTimeBegin");
            }
            PageResult pageResult = accountTransitProcessPendingHistoryDao.listPage(paramMap, pageParam);
            List<AccountTransitProcessPending> collect = ((List<AccountTransitProcessPendingHistory>) pageResult.getData()).stream()
                    .map(AccountTransitProcessPending::newFromHistory).collect(Collectors.toList());
            pageResult.setData(collect);
            return pageResult;
        }


    }

    public AccountTransitProcessPending getAccountProcessPendingById(Long id, boolean inHistory) {
        if (id == null) {
            return null;
        }
        if (!inHistory) {
            return accountTransitProcessPendingDao.getById(id);
        } else {
            return AccountTransitProcessPending.newFromHistory(accountTransitProcessPendingHistoryDao.getById(id));
        }
    }
}
