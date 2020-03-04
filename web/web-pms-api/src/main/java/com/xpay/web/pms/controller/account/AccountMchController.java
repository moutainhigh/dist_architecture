package com.xpay.web.pms.controller.account;

import com.xpay.common.statics.annotations.Permission;
import com.xpay.common.statics.enums.account.AccountResultAuditTypeEnum;
import com.xpay.common.statics.result.PageParam;
import com.xpay.common.statics.result.PageResult;
import com.xpay.common.statics.result.RestResult;
import com.xpay.common.util.utils.BeanUtil;
import com.xpay.common.util.utils.StringUtil;
import com.xpay.facade.accountmch.entity.AccountMch;
import com.xpay.facade.accountmch.entity.AccountMchProcessDetail;
import com.xpay.facade.accountmch.entity.AccountMchProcessPending;
import com.xpay.facade.accountmch.entity.AccountMchProcessResult;
import com.xpay.facade.accountmch.service.AccountMchManageFacade;
import com.xpay.facade.accountmch.service.AccountMchProcessManageFacade;
import com.xpay.web.pms.vo.account.AccountMchProcessDetailQueryVO;
import com.xpay.web.pms.vo.account.AccountMchProcessPendingQueryVO;
import com.xpay.web.pms.vo.account.AccountMchProcessResultQueryVO;
import com.xpay.web.pms.vo.account.AccountMchQueryVO;
import org.apache.dubbo.config.annotation.Reference;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author luobinzhao
 * @date 2020/1/16 9:24
 */
@RequestMapping("accountMch")
@RestController
public class AccountMchController {
    @Reference
    private AccountMchManageFacade accountMchManageFacade;

    @Reference
    private AccountMchProcessManageFacade accountMchProcessManageFacade;


    @Permission("account:sub:view")
    @RequestMapping("listAccountMch")
    public RestResult<PageResult<List<AccountMch>>> listAccountMch(@RequestBody AccountMchQueryVO accountMchQueryVO,
                                                                   @RequestParam int pageCurrent,
                                                                   @RequestParam int pageSize) {
        PageResult<List<AccountMch>> pageResult = accountMchManageFacade.listAccountPage(BeanUtil.toMap(accountMchQueryVO), PageParam.newInstance(pageCurrent, pageSize));
        return RestResult.success(pageResult);
    }


    @RequestMapping("listProcessPending")
    public RestResult<PageResult<List<AccountMchProcessPending>>> listProcessPending(@RequestBody AccountMchProcessPendingQueryVO accountMchProcessPendingQueryVO,
                                                                                     @RequestParam int pageCurrent,
                                                                                     @RequestParam int pageSize,
                                                                                     @RequestParam boolean inHistory) {
        if (!StringUtil.isEmpty(accountMchProcessPendingQueryVO.getTrxNo()) || !StringUtil.isEmpty(accountMchProcessPendingQueryVO.getMerchantNo())) {
            if (accountMchProcessPendingQueryVO.getCreateTimeBegin() == null || accountMchProcessPendingQueryVO.getCreateTimeEnd() == null
                    || (accountMchProcessPendingQueryVO.getCreateTimeEnd().getTime() - accountMchProcessPendingQueryVO.getCreateTimeBegin().getTime()) > 24 * 60 * 60 * 1000) {
                return RestResult.error("当使用平台流水号或者商户编号搜索账务处理结果时，必须设置创建时间范围，且范围区间不能大于1天");
            }
        }
        PageResult<List<AccountMchProcessPending>> pageResult = accountMchProcessManageFacade.listProcessPendingPage(BeanUtil.toMap(accountMchProcessPendingQueryVO), PageParam.newInstance(pageCurrent, pageSize), inHistory);
        return RestResult.success(pageResult);
    }


    /**
     * 查看待账务处理详情
     *
     * @return
     */
    @RequestMapping("viewProcessPending")
    public RestResult<AccountMchProcessPending> viewProcessPending(@RequestParam long id, @RequestParam boolean inHistory) {
        AccountMchProcessPending processPending = accountMchProcessManageFacade.getProcessPendingById(id, inHistory);
        if (processPending == null) {
            return RestResult.error("记录不存在");
        } else {
            return RestResult.success(processPending);
        }
    }


    /**
     * 分页查询账务处理明细
     *
     * @return .
     */
    @RequestMapping("listProcessDetail")
    public RestResult<PageResult<List<AccountMchProcessDetail>>> listProcessDetail(@RequestBody AccountMchProcessDetailQueryVO accountMchProcessDetailQueryVO,
                                                                                   @RequestParam int pageCurrent,
                                                                                   @RequestParam int pageSize,
                                                                                   @RequestParam boolean inHistory) {
        if (inHistory && accountMchProcessDetailQueryVO.getCreateTimeBegin() == null) {
            return RestResult.error("查询历史表必须输入日期范围");
        }
        PageResult<List<AccountMchProcessDetail>> pageResult = accountMchProcessManageFacade.listProcessDetailPage(BeanUtil.toMap(accountMchProcessDetailQueryVO), PageParam.newInstance(pageCurrent, pageSize), inHistory);
        return RestResult.success(pageResult);
    }


    /**
     * 分页查询账务处理结果表记录
     *
     * @return .
     */
    @RequestMapping("listProcessResult")
    public RestResult<PageResult<List<AccountMchProcessResult>>> listProcessResult(@RequestBody AccountMchProcessResultQueryVO accountMchProcessResultQueryVO,
                                                                                   @RequestParam int pageCurrent,
                                                                                   @RequestParam int pageSize,
                                                                                   @RequestParam boolean inHistory) {
        if (!StringUtil.isEmpty(accountMchProcessResultQueryVO.getTrxNo()) || !StringUtil.isEmpty(accountMchProcessResultQueryVO.getMerchantNo())) {
            if (accountMchProcessResultQueryVO.getCreateTimeBegin() == null || accountMchProcessResultQueryVO.getCreateTimeEnd() == null
                    || (accountMchProcessResultQueryVO.getCreateTimeEnd().getTime() - accountMchProcessResultQueryVO.getCreateTimeBegin().getTime()) > 24 * 60 * 60 * 1000) {
                return RestResult.error("当使用平台流水号或者商户编号搜索账务处理结果时，必须设置创建时间范围，且范围区间不能大于1天");
            }
        }
        PageResult<List<AccountMchProcessResult>> pageResult = accountMchProcessManageFacade.listProcessResultPage(BeanUtil.toMap(accountMchProcessResultQueryVO), PageParam.newInstance(pageCurrent, pageSize), inHistory);
        return RestResult.success(pageResult);
    }


    /**
     * 查看账务处理结果处理详情
     *
     * @return .
     */
    @RequestMapping("viewProcessResult")
    public RestResult<AccountMchProcessResult> viewProcessResult(@RequestParam long id, @RequestParam boolean inHistory) {
        AccountMchProcessResult processResult = accountMchProcessManageFacade.getProcessResultById(id, inHistory);
        if (processResult == null) {
            return RestResult.error("记录不存在");
        } else {
            return RestResult.success(processResult);
        }
    }


    @RequestMapping("resendProcessResultCallback")
    public RestResult<String> resendProcessResultCallback(@RequestParam long id, @RequestParam boolean inHistory) {
        if (accountMchProcessManageFacade.sendProcessResultCallbackMsg(id, true, inHistory)) {
            return RestResult.success("发送成功");
        } else {
            return RestResult.error("发送失败");
        }
    }


    /**
     * 账务处理结果审核
     *
     * @return .
     */
    @RequestMapping("auditProcessResult")
    public RestResult<String> auditProcessResult(@RequestBody List<Long> ids, @RequestParam Integer auditType) {
        if (ids == null || ids.size() == 0) {
            return RestResult.error("账务处理结果ID不能为空");
        }
        int successCount = 0, failCount = 0;
        Map<Boolean, List<Boolean>> successFailGroup = ids.stream().map(id -> accountMchProcessManageFacade.auditProcessResult(id, auditType))
                .collect(Collectors.groupingBy(Boolean::new));
        if (successFailGroup.get(Boolean.TRUE) != null) {
            successCount = successFailGroup.get(Boolean.TRUE).size();
        }
        if (successFailGroup.get(Boolean.FALSE) != null) {
            failCount = successFailGroup.get(Boolean.FALSE).size();
        }
        return RestResult.success("审核为" + AccountResultAuditTypeEnum.getEnum(auditType) + "完成，成功:" + successCount + "笔，失败:" + failCount + "笔");
    }
}
