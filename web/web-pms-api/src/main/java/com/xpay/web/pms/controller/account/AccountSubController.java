package com.xpay.web.pms.controller.account;

import com.xpay.common.statics.annotations.Permission;
import com.xpay.common.statics.enums.account.AccountResultAuditTypeEnum;
import com.xpay.common.statics.result.PageParam;
import com.xpay.common.statics.result.PageResult;
import com.xpay.common.statics.result.RestResult;
import com.xpay.common.util.utils.BeanUtil;
import com.xpay.common.util.utils.StringUtil;
import com.xpay.facade.accountsub.entity.AccountSub;
import com.xpay.facade.accountsub.entity.AccountSubProcessDetail;
import com.xpay.facade.accountsub.entity.AccountSubProcessPending;
import com.xpay.facade.accountsub.entity.AccountSubProcessResult;
import com.xpay.facade.accountsub.service.AccountSubManageFacade;
import com.xpay.facade.accountsub.service.AccountSubProcessManageFacade;
import com.xpay.web.pms.controller.BaseController;
import com.xpay.web.pms.vo.account.AccountSubProcessDetailQueryVO;
import com.xpay.web.pms.vo.account.AccountSubProcessPendingQueryVO;
import com.xpay.web.pms.vo.account.AccountSubProcessResultQueryVO;
import com.xpay.web.pms.vo.account.AccountSubQueryVO;
import org.apache.dubbo.config.annotation.Reference;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Author: Cmf
 * Date: 2019.12.31
 * Time: 16:35
 * Description:
 */
@RestController
@RequestMapping("accountSub")
public class AccountSubController extends BaseController {
    @Reference
    private AccountSubManageFacade accountSubManageFacade;

    @Reference
    private AccountSubProcessManageFacade accountSubProcessManageFacade;


    @Permission("account:sub:view")
    @RequestMapping("listAccountSub")
    public RestResult<PageResult<List<AccountSub>>> listAccountSub(@RequestBody AccountSubQueryVO accountSubQueryVO,
                                                                   @RequestParam int pageCurrent,
                                                                   @RequestParam int pageSize) {
        PageResult<List<AccountSub>> pageResult = accountSubManageFacade.listAccountPage(BeanUtil.toMap(accountSubQueryVO), PageParam.newInstance(pageCurrent, pageSize));
        return RestResult.success(pageResult);
    }


    @RequestMapping("listProcessPending")
    public RestResult<PageResult<List<AccountSubProcessPending>>> listProcessPending(@RequestBody AccountSubProcessPendingQueryVO accountSubProcessPendingQueryVO,
                                                                                     @RequestParam int pageCurrent,
                                                                                     @RequestParam int pageSize,
                                                                                     @RequestParam boolean inHistory) {
        if (!StringUtil.isEmpty(accountSubProcessPendingQueryVO.getTrxNo()) || !StringUtil.isEmpty(accountSubProcessPendingQueryVO.getMerchantNo())) {
            if (accountSubProcessPendingQueryVO.getCreateTimeBegin() == null || accountSubProcessPendingQueryVO.getCreateTimeEnd() == null
                    || (accountSubProcessPendingQueryVO.getCreateTimeEnd().getTime() - accountSubProcessPendingQueryVO.getCreateTimeBegin().getTime()) > 24 * 60 * 60 * 1000) {
                return RestResult.error("当使用平台流水号或者商户编号搜索账务处理结果时，必须设置创建时间范围，且范围区间不能大于1天");
            }
        }
        PageResult<List<AccountSubProcessPending>> pageResult = accountSubProcessManageFacade.listProcessPendingPage(BeanUtil.toMap(accountSubProcessPendingQueryVO), PageParam.newInstance(pageCurrent, pageSize), inHistory);
        return RestResult.success(pageResult);
    }


    /**
     * 查看待账务处理详情
     *
     * @return
     */
    @RequestMapping("viewProcessPending")
    public RestResult<AccountSubProcessPending> viewProcessPending(@RequestParam long id, @RequestParam boolean inHistory) {
        AccountSubProcessPending processPending = accountSubProcessManageFacade.getProcessPendingById(id, inHistory);
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
    public RestResult<PageResult<List<AccountSubProcessDetail>>> listProcessDetail(@RequestBody AccountSubProcessDetailQueryVO accountSubProcessDetailQueryVO,
                                                                                   @RequestParam int pageCurrent,
                                                                                   @RequestParam int pageSize,
                                                                                   @RequestParam boolean inHistory) {
        if (inHistory && accountSubProcessDetailQueryVO.getCreateTimeBegin() == null) {
            return RestResult.error("查询历史表必须输入日期范围");
        }
        PageResult<List<AccountSubProcessDetail>> pageResult = accountSubProcessManageFacade.listProcessDetailPage(BeanUtil.toMap(accountSubProcessDetailQueryVO), PageParam.newInstance(pageCurrent, pageSize), inHistory);
        return RestResult.success(pageResult);
    }


    /**
     * 分页查询账务处理结果表记录
     *
     * @return .
     */
    @RequestMapping("listProcessResult")
    public RestResult<PageResult<List<AccountSubProcessResult>>> listProcessResult(@RequestBody AccountSubProcessResultQueryVO accountSubProcessResultQueryVO,
                                                                                   @RequestParam int pageCurrent,
                                                                                   @RequestParam int pageSize,
                                                                                   @RequestParam boolean inHistory) {
        if (!StringUtil.isEmpty(accountSubProcessResultQueryVO.getTrxNo()) || !StringUtil.isEmpty(accountSubProcessResultQueryVO.getMerchantNo())) {
            if (accountSubProcessResultQueryVO.getCreateTimeBegin() == null || accountSubProcessResultQueryVO.getCreateTimeEnd() == null
                    || (accountSubProcessResultQueryVO.getCreateTimeEnd().getTime() - accountSubProcessResultQueryVO.getCreateTimeBegin().getTime()) > 24 * 60 * 60 * 1000) {
                return RestResult.error("当使用平台流水号或者商户编号搜索账务处理结果时，必须设置创建时间范围，且范围区间不能大于1天");
            }
        }
        PageResult<List<AccountSubProcessResult>> pageResult = accountSubProcessManageFacade.listProcessResultPage(BeanUtil.toMap(accountSubProcessResultQueryVO), PageParam.newInstance(pageCurrent, pageSize), inHistory);
        return RestResult.success(pageResult);
    }


    /**
     * 查看账务处理结果处理详情
     *
     * @return .
     */
    @RequestMapping("viewProcessResult")
    public RestResult<AccountSubProcessResult> viewProcessResult(@RequestParam long id, @RequestParam boolean inHistory) {
        AccountSubProcessResult processResult = accountSubProcessManageFacade.getProcessResultById(id, inHistory);
        if (processResult == null) {
            return RestResult.error("记录不存在");
        } else {
            return RestResult.success(processResult);
        }
    }


    @RequestMapping("resendProcessResultCallback")
    public RestResult<String> resendProcessResultCallback(@RequestParam long id, @RequestParam boolean inHistory) {
        if (accountSubProcessManageFacade.sendProcessResultCallbackMsg(id, true, inHistory)) {
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
        Map<Boolean, List<Boolean>> successFailGroup = ids.stream().map(id -> accountSubProcessManageFacade.auditProcessResult(id, auditType))
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
