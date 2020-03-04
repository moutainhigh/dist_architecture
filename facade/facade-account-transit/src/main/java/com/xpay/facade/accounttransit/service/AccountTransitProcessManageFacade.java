package com.xpay.facade.accounttransit.service;

import com.xpay.common.statics.exception.BizException;
import com.xpay.common.statics.result.PageParam;
import com.xpay.common.statics.result.PageResult;
import com.xpay.facade.accounttransit.entity.AccountTransitProcessDetail;
import com.xpay.facade.accounttransit.entity.AccountTransitProcessPending;
import com.xpay.facade.accounttransit.entity.AccountTransitProcessResult;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Author: Cmf
 * Date: 2019.12.31
 * Time: 18:07
 * Description: 在途账户账务处理管理接口
 */
public interface AccountTransitProcessManageFacade {

    /**
     * 查询未处理的待账务处理记录id
     *
     * @param createTimeBegin .
     * @param createTimeEnd   .
     * @param minId           循环查询时使用上一次的最大id作为本次查询的minId，第一次循环时，传null
     * @param number          查询的记录数
     * @return .
     */
    List<Long> listPendingAccountProcessId(Date createTimeBegin, Date createTimeEnd, Long minId, int number) throws BizException;


    /**
     * 分页查询待账务处理表记录
     *
     * @param paramMap  .
     * @param pageParam .
     * @param inHistory 是否查询历史表
     * @return .
     */
    PageResult<List<AccountTransitProcessPending>> listProcessPendingPage(Map<String, Object> paramMap, PageParam pageParam, boolean inHistory) throws BizException;

    /**
     * 根据id查询待账务处理表记录
     *
     * @param id        .
     * @param inHistory 是否查询历史表
     * @return .
     */
    AccountTransitProcessPending getProcessPendingById(Long id, boolean inHistory) throws BizException;


    /**
     * 把待账务处理记录从处理中审核为待处理
     *
     * @param id 待账务处理记录id
     * @return .
     */
    boolean auditProcessPendingRevertToPending(Long id) throws BizException;

    /**
     * 查询需要进行异步回调的账务处理结果id
     *
     * @param createTimeBegin .
     * @param minId           循环查询时使用上一次的最大id作为本次查询的minId，第一次循环时，传null
     * @param number          每次查询的记录数
     * @return .
     */
    List<Long> listNeedCallBackResultId(Date createTimeBegin, Long minId, int number) throws BizException;


    /**
     * 分页查询账务处理结果表记录
     *
     * @param paramMap  .
     * @param pageParam .
     * @param inHistory 是否查询历史表
     * @return
     */
    PageResult<List<AccountTransitProcessResult>> listProcessResultPage(Map<String, Object> paramMap, PageParam pageParam, boolean inHistory) throws BizException;


    /**
     * 根据id查询账务处理结果表记录
     *
     * @param id        .
     * @param inHistory 是否查询历史表
     * @return .
     */
    AccountTransitProcessResult getProcessResultById(Long id, boolean inHistory) throws BizException;

    /**
     * 审核账务处理结果
     *
     * @param id          .
     * @param auditResult -1=审核为处理失败 0=审核为重新处理 1=审核为处理成功
     * @return .
     */
    boolean auditProcessResult(Long id, Integer auditResult) throws BizException;

    /**
     * 发送处理结果的回调通知并更新记录状态为“已发送”
     *
     * @param id            账务处理结果id
     * @param isOnlySendMsg true:只有当发送状态扭转成功时，才发送消息；false:仅仅发送消息，不扭转回调发送状态
     * @param inHistory     是否是从历史表查询
     * @return 发送成功/失败
     */
    boolean sendProcessResultCallbackMsg(Long id, boolean isOnlySendMsg, boolean inHistory) throws BizException;


    /**
     * 分页查询账务处理明细
     *
     * @param paramMap  .
     * @param pageParam .
     * @param inHistory 是否查询历史表
     * @return .
     */
    PageResult<List<AccountTransitProcessDetail>> listProcessDetailPage(Map<String, Object> paramMap, PageParam pageParam, boolean inHistory) throws BizException;


    /**
     * 根据id查询账务处理明细
     *
     * @param id        .
     * @param inHistory 是否查询历史表
     * @return .
     */
    AccountTransitProcessDetail getProcessDetailById(Long id, boolean inHistory) throws BizException;


}
