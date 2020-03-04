package com.xpay.web.pms.controller.merchant;

import com.xpay.common.statics.annotations.Permission;
import com.xpay.common.statics.enums.merchant.MerchantTypeEnum;
import com.xpay.common.statics.result.PageParam;
import com.xpay.common.statics.result.PageResult;
import com.xpay.common.statics.result.RestResult;
import com.xpay.common.util.utils.BeanUtil;
import com.xpay.facade.merchant.entity.Merchant;
import com.xpay.facade.merchant.service.MerchantFacade;
import com.xpay.web.pms.vo.merchant.MerchantQueryVO;
import org.apache.dubbo.config.annotation.Reference;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

/**
 * Author: Cmf
 * Date: 2020.2.14
 * Time: 11:56
 * Description:
 */
@RequestMapping("merchant")
@RestController
public class MerchantController {
    @Reference
    private MerchantFacade merchantFacade;


    /**
     * 获取平台商户列表
     *
     * @param pageCurrent .
     * @param pageSize    .
     * @param queryVO     .
     * @return .
     */
    @RequestMapping("listMerchant")
    @Permission("merchant:plat:view")
    public RestResult<PageResult<List<Merchant>>> listMerchant(@RequestParam int pageCurrent, @RequestParam int pageSize, @RequestBody MerchantQueryVO queryVO) {
        Map<String, Object> paramMap = BeanUtil.toMap(queryVO);
        paramMap.put("merchantType", MerchantTypeEnum.PLAT_MERCHANT.getValue());
        PageResult<List<Merchant>> pageResult = merchantFacade.listMerchantPage(paramMap, PageParam.newInstance(pageCurrent, pageSize));
        return RestResult.success(pageResult);
    }

}
