package com.xpay.facade.accounttransit.service;

import com.xpay.common.statics.exception.BizException;
import com.xpay.facade.accounttransit.dto.AccountTransitProcessDto;
import com.xpay.facade.accounttransit.dto.AccountTransitRequestDto;

import java.util.List;

public interface AccountTransitProcessFacade {

    /**
     * 同步账务处理
     *
     * @param requestDto 账务处理请求DTO
     *                   accountProcessNo:账务处理流水号，如果使用自定义的流水号，需要确保长度在15~30之间且只能由字母或数字或下划线组成
     *                   callbackDestination:RocketMQ的topic、tags 是可以用英文的冒号分割，如：my-topic:tags_one,不需要回调时可传入null
     * @param processDto 账务处理业务对象
     * @throws BizException 如果账务处理过程中出现业务校验错误或者系统异常，会抛出相应的异常，
     *                      若判定为重复账务处理,会抛出com.xpay.common.statics.exception.AccountTransitExceptions#ACCOUNT_PROCESS_REPEAT
     */
    boolean executeSync(AccountTransitRequestDto requestDto, AccountTransitProcessDto processDto) throws BizException;

    boolean executeSync(AccountTransitRequestDto requestDto, List<AccountTransitProcessDto> processDtoList) throws BizException;

    /**
     * 异步账务处理
     * 说明:
     * 1、账务处理流水号由调用方生成，
     * 对于同一个账务处理，最好使用相同的账务处理流水（比如业务流水号)，这样有利于对重复账务处理的判定
     * 【如果同一个业务流水号，若需要进行多次账务处理，请使用相应的方法进行区分，比如每一个账务处理加上不同的后缀】
     * <p>
     * 2.内部会把 merchantNo + trxNo + processType 作为唯一约束的key，如果重复提交，会抛出异常：com.xpay.common.statics.exception.AccountTransitExceptions#ACCOUNT_PROCESS_PENDING_UNIQUE_KEY_REPEAT
     * 3.如果账务处理流水号重复，则会抛出com.xpay.common.statics.exception.AccountTransitExceptions#ACCOUNT_PROCESS_PENDING_PROCESS_NO_REPEAT
     *
     * @param requestDto 账务处理请求DTO
     *                   accountProcessNo:账务处理流水号，如果使用自定义的流水号，需要确保长度在15~30之间且只能由字母或数字或下划线组成
     *                   callbackDestination:RocketMQ的topic、tags 是可以用英文的冒号分割，如：my-topic:tags_one,不需要回调时可传入null
     * @param processDto 账务处理业务对象
     * @throws BizException 如果账务处理过程中出现业务校验错误或者系统异常，会抛出相应的异常，注意上述说明里的两个特殊的异常
     */
    boolean executeAsync(AccountTransitRequestDto requestDto, AccountTransitProcessDto processDto) throws BizException;

    boolean executeAsync(AccountTransitRequestDto requestDto, List<AccountTransitProcessDto> processDtoList) throws BizException;

    /**
     * 确认扣款账务明细
     *
     * @param merchantNoAndTrxNoList 商户号和扣款的平台流水号集合，约定每个数组【0】为商户号，【1】为平台流水号
     */
    void commitDebitSync(List<String[]> merchantNoAndTrxNoList) throws BizException;

    /**
     * 异步之后的同步账务处理
     *
     * @param accountProcessPendingId 待账务处理id
     * @throws BizException 如果账务处理过程中出现业务校验错误或者系统异常，会抛出相应的异常
     *                      若判定为重复账务处理,会抛出com.xpay.common.statics.exception.AccountTransitExceptions#ACCOUNT_PROCESS_REPEAT
     */
    boolean executeSyncForAsync(long accountProcessPendingId) throws BizException;

}
