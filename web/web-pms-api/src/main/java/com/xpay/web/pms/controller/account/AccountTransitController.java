package com.xpay.web.pms.controller.account;

import com.xpay.common.statics.annotations.Permission;
import com.xpay.common.statics.enums.account.AccountResultAuditTypeEnum;
import com.xpay.common.statics.result.PageParam;
import com.xpay.common.statics.result.PageResult;
import com.xpay.common.statics.result.RestResult;
import com.xpay.common.util.utils.BeanUtil;
import com.xpay.common.util.utils.StringUtil;
import com.xpay.facade.accounttransit.entity.AccountTransit;
import com.xpay.facade.accounttransit.entity.AccountTransitProcessDetail;
import com.xpay.facade.accounttransit.entity.AccountTransitProcessPending;
import com.xpay.facade.accounttransit.entity.AccountTransitProcessResult;
import com.xpay.facade.accounttransit.service.AccountTransitManageFacade;
import com.xpay.facade.accounttransit.service.AccountTransitProcessManageFacade;
import com.xpay.web.pms.controller.BaseController;
import com.xpay.web.pms.vo.account.AccountTransitProcessDetailQueryVO;
import com.xpay.web.pms.vo.account.AccountTransitProcessPendingQueryVO;
import com.xpay.web.pms.vo.account.AccountTransitProcessResultQueryVO;
import com.xpay.web.pms.vo.account.AccountTransitQueryVO;
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
 * Date: 2020.2.11
 * Time: 15:49
 * Description: 在途账户运营后台接口
 */
@RestController
@RequestMapping("accountTransit")
public class AccountTransitController extends BaseController {
    @Reference
    private AccountTransitManageFacade accountTransitManageFacade;

    @Reference
    private AccountTransitProcessManageFacade accountTransitProcessManageFacade;


    @Permission("account:transit:view")
    @RequestMapping("listAccountTransit")
    public RestResult<PageResult<List<AccountTransit>>> listAccountTransit(@RequestBody AccountTransitQueryVO accountTransitQueryVO,
                                                                           @RequestParam int pageCurrent,
                                                                           @RequestParam int pageSize) {
        PageResult<List<AccountTransit>> pageResult = accountTransitManageFacade.listAccountPage(BeanUtil.toMap(accountTransitQueryVO), PageParam.newInstance(pageCurrent, pageSize));
        return RestResult.success(pageResult);
    }


    @RequestMapping("listProcessPending")
    public RestResult<PageResult<List<AccountTransitProcessPending>>> listProcessPending(@RequestBody AccountTransitProcessPendingQueryVO accountTransitProcessPendingQueryVO,
                                                                                         @RequestParam int pageCurrent,
                                                                                         @RequestParam int pageSize,
                                                                                         @RequestParam boolean inHistory) {
        if (!StringUtil.isEmpty(accountTransitProcessPendingQueryVO.getTrxNo()) || !StringUtil.isEmpty(accountTransitProcessPendingQueryVO.getMerchantNo())) {
            if (accountTransitProcessPendingQueryVO.getCreateTimeBegin() == null || accountTransitProcessPendingQueryVO.getCreateTimeEnd() == null
                    || (accountTransitProcessPendingQueryVO.getCreateTimeEnd().getTime() - accountTransitProcessPendingQueryVO.getCreateTimeBegin().getTime()) > 24 * 60 * 60 * 1000) {
                return RestResult.error("当使用平台流水号或者商户编号搜索账务处理结果时，必须设置创建时间范围，且范围区间不能大于1天");
            }
        }
        PageResult<List<AccountTransitProcessPending>> pageResult = accountTransitProcessManageFacade.listProcessPendingPage(BeanUtil.toMap(accountTransitProcessPendingQueryVO), PageParam.newInstance(pageCurrent, pageSize), inHistory);
        return RestResult.success(pageResult);
    }


    /**
     * 查看待账务处理详情
     *
     * @return
     */
    @RequestMapping("viewProcessPending")
    public RestResult<AccountTransitProcessPending> viewProcessPending(@RequestParam long id, @RequestParam boolean inHistory) {
        AccountTransitProcessPending processPending = accountTransitProcessManageFacade.getProcessPendingById(id, inHistory);
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
    public RestResult<PageResult<List<AccountTransitProcessDetail>>> listProcessDetail(@RequestBody AccountTransitProcessDetailQueryVO accountTransitProcessDetailQueryVO,
                                                                                       @RequestParam int pageCurrent,
                                                                                       @RequestParam int pageSize,
                                                                                       @RequestParam boolean inHistory) {
        if (inHistory && accountTransitProcessDetailQueryVO.getCreateTimeBegin() == null) {
            return RestResult.error("查询历史表必须输入日期范围");
        }
        PageResult<List<AccountTransitProcessDetail>> pageResult = accountTransitProcessManageFacade.listProcessDetailPage(BeanUtil.toMap(accountTransitProcessDetailQueryVO), PageParam.newInstance(pageCurrent, pageSize), inHistory);
        return RestResult.success(pageResult);
    }


    /**
     * 分页查询账务处理结果表记录
     *
     * @return .
     */
    @RequestMapping("listProcessResult")
    public RestResult<PageResult<List<AccountTransitProcessResult>>> listProcessResult(@RequestBody AccountTransitProcessResultQueryVO accountTransitProcessResultQueryVO,
                                                                                       @RequestParam int pageCurrent,
                                                                                       @RequestParam int pageSize,
                                                                                       @RequestParam boolean inHistory) {
        if (!StringUtil.isEmpty(accountTransitProcessResultQueryVO.getTrxNo()) || !StringUtil.isEmpty(accountTransitProcessResultQueryVO.getMerchantNo())) {
            if (accountTransitProcessResultQueryVO.getCreateTimeBegin() == null || accountTransitProcessResultQueryVO.getCreateTimeEnd() == null
                    || (accountTransitProcessResultQueryVO.getCreateTimeEnd().getTime() - accountTransitProcessResultQueryVO.getCreateTimeBegin().getTime()) > 24 * 60 * 60 * 1000) {
                return RestResult.error("当使用平台流水号或者商户编号搜索账务处理结果时，必须设置创建时间范围，且范围区间不能大于1天");
            }
        }
        PageResult<List<AccountTransitProcessResult>> pageResult = accountTransitProcessManageFacade.listProcessResultPage(BeanUtil.toMap(accountTransitProcessResultQueryVO), PageParam.newInstance(pageCurrent, pageSize), inHistory);
        return RestResult.success(pageResult);
    }


    /**
     * 查看账务处理结果处理详情
     *
     * @return .
     */
    @RequestMapping("viewProcessResult")
    public RestResult<AccountTransitProcessResult> viewProcessResult(@RequestParam long id, @RequestParam boolean inHistory) {
        AccountTransitProcessResult processResult = accountTransitProcessManageFacade.getProcessResultById(id, inHistory);
        if (processResult == null) {
            return RestResult.error("记录不存在");
        } else {
            return RestResult.success(processResult);
        }
    }


    @RequestMapping("resendProcessResultCallback")
    public RestResult<String> resendProcessResultCallback(@RequestParam long id, @RequestParam boolean inHistory) {
        if (accountTransitProcessManageFacade.sendProcessResultCallbackMsg(id, true, inHistory)) {
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
        Map<Boolean, List<Boolean>> successFailGroup = ids.stream().map(id -> accountTransitProcessManageFacade.auditProcessResult(id, auditType))
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
