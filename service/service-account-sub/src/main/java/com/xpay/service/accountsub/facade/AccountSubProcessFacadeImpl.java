package com.xpay.service.accountsub.facade;

import com.xpay.facade.accountsub.dto.AccountSubProcessDto;
import com.xpay.facade.accountsub.dto.AccountSubRequestDto;
import com.xpay.facade.accountsub.service.AccountSubProcessFacade;
import com.xpay.service.accountsub.biz.AccountSubProcessHandler;
import org.apache.dubbo.config.annotation.Service;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

@Service
public class AccountSubProcessFacadeImpl implements AccountSubProcessFacade {
    @Autowired
    AccountSubProcessHandler accountProcessHandler;

    public boolean executeSync(AccountSubRequestDto requestDto, AccountSubProcessDto processDto) {
        return accountProcessHandler.executeSync(requestDto, processDto);
    }

    public boolean executeSync(AccountSubRequestDto requestDto, List<AccountSubProcessDto> processDtoList) {
        return accountProcessHandler.executeSync(requestDto, processDtoList);
    }

    public boolean executeAsync(AccountSubRequestDto requestDto, AccountSubProcessDto processDto) {
        return accountProcessHandler.executeAsync(requestDto, processDto);
    }

    public boolean executeAsync(AccountSubRequestDto requestDto, List<AccountSubProcessDto> processDtoList) {
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
