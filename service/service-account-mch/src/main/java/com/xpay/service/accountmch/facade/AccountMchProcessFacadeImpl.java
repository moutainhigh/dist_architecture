package com.xpay.service.accountmch.facade;

import com.xpay.facade.accountmch.dto.AccountMchProcessDto;
import com.xpay.facade.accountmch.dto.AccountMchRequestDto;
import com.xpay.facade.accountmch.service.AccountMchProcessFacade;
import com.xpay.service.accountmch.biz.AccountMchProcessHandler;
import org.apache.dubbo.config.annotation.Service;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

/**
 * @author luobinzhao
 * @date 2020/1/14 16:30
 */
@Service
public class AccountMchProcessFacadeImpl implements AccountMchProcessFacade {
    @Autowired
    AccountMchProcessHandler accountProcessHandler;

    public boolean executeSync(AccountMchRequestDto requestDto, AccountMchProcessDto processDto) {
        return accountProcessHandler.executeSync(requestDto, processDto);
    }

    public boolean executeSync(AccountMchRequestDto requestDto, List<AccountMchProcessDto> processDtoList) {
        return accountProcessHandler.executeSync(requestDto, processDtoList);
    }

    public boolean executeAsync(AccountMchRequestDto requestDto, AccountMchProcessDto processDto) {
        return accountProcessHandler.executeAsync(requestDto, processDto);
    }

    public boolean executeAsync(AccountMchRequestDto requestDto, List<AccountMchProcessDto> processDtoList) {
        return accountProcessHandler.executeAsync(requestDto, processDtoList);
    }

    /**
     * 确认扣款账务明细的状态
     *
     * @param merchantNoAndTrxNoList 商户号和扣款的平台流水号集合，约定每个数组【0】为商户号，【1】为平台流水号
     * @return .
     */
    public void commitDebitSync(List<String[]> merchantNoAndTrxNoList) {
        accountProcessHandler.commitDebitSync(merchantNoAndTrxNoList);
    }

    public boolean executeSyncForAsync(long accountProcessPendingId) {
        return accountProcessHandler.executeSyncForAsync(accountProcessPendingId);
    }
}
