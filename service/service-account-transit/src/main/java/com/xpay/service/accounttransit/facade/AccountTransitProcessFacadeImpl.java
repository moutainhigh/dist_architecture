package com.xpay.service.accounttransit.facade;

import com.xpay.facade.accounttransit.dto.AccountTransitProcessDto;
import com.xpay.facade.accounttransit.dto.AccountTransitRequestDto;
import com.xpay.facade.accounttransit.service.AccountTransitProcessFacade;
import com.xpay.service.accounttransit.biz.AccountTransitProcessHandler;
import org.apache.dubbo.config.annotation.Service;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

@Service
public class AccountTransitProcessFacadeImpl implements AccountTransitProcessFacade {
    @Autowired
    AccountTransitProcessHandler accountProcessHandler;

    public boolean executeSync(AccountTransitRequestDto requestDto, AccountTransitProcessDto processDto) {
        return accountProcessHandler.executeSync(requestDto, processDto);
    }

    public boolean executeSync(AccountTransitRequestDto requestDto, List<AccountTransitProcessDto> processDtoList) {
        return accountProcessHandler.executeSync(requestDto, processDtoList);
    }

    public boolean executeAsync(AccountTransitRequestDto requestDto, AccountTransitProcessDto processDto) {
        return accountProcessHandler.executeAsync(requestDto, processDto);
    }

    public boolean executeAsync(AccountTransitRequestDto requestDto, List<AccountTransitProcessDto> processDtoList) {
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
