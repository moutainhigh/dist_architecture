package com.xpay.web.pms.controller.system;

import com.xpay.common.statics.result.PageParam;
import com.xpay.common.statics.result.PageResult;
import com.xpay.common.statics.result.RestResult;
import com.xpay.common.util.utils.BeanUtil;
import com.xpay.common.util.utils.StringUtil;
import com.xpay.facade.lock.entity.GlobalLock;
import com.xpay.facade.lock.service.GlobalLockFacade;
import com.xpay.web.pms.controller.BaseController;
import com.xpay.web.pms.vo.system.GlobalLockQueryVO;
import org.apache.dubbo.config.annotation.Reference;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

/**
 * Author: Cmf
 * Date: 2019.11.22
 * Time: 14:23
 * Description: 全局锁管理
 */
@RequestMapping("globalLock")
@RestController
public class GlobalLockController extends BaseController {

    @Reference
    private GlobalLockFacade globalLockFacade;

    
    @RequestMapping("listGlobalLock")
    public RestResult<PageResult<List<GlobalLock>>> listGlobalLock(@RequestParam int pageCurrent, @RequestParam int pageSize, @RequestBody GlobalLockQueryVO queryVO) {
        Map<String, Object> paramMap = BeanUtil.toMap(queryVO);
        PageResult<List<GlobalLock>> pageResult = globalLockFacade.listPage(paramMap, PageParam.newInstance(pageCurrent, pageSize));
        return RestResult.success(pageResult);
    }


    @RequestMapping("unlockForce")
    public RestResult<String> unlockForce(@RequestParam String resourceId) {
        if (StringUtil.isEmpty(resourceId)) {
            return RestResult.error("resourceId不能为空");
        }

        boolean isSuccess = globalLockFacade.unlockForce(resourceId, false);
        if (isSuccess) {
            return RestResult.success("操作成功");
        } else {
            return RestResult.error("操作失败");
        }
    }


    @RequestMapping("unlockAndDelete")
    public RestResult<String> unlockAndDelete(@RequestParam String resourceId) {
        if (StringUtil.isEmpty(resourceId)) {
            return RestResult.error("resourceId不能为空");
        }

        boolean isSuccess = globalLockFacade.unlockForce(resourceId, true);
        if (isSuccess) {
            return RestResult.success("操作成功");
        } else {
            return RestResult.error("操作失败");
        }
    }
}
