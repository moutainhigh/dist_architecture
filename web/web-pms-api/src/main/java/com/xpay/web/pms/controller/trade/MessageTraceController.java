package com.xpay.web.pms.controller.trade;

import com.xpay.common.statics.annotations.Permission;
import com.xpay.common.statics.dto.rmq.MsgDto;
import com.xpay.common.statics.result.RestResult;
import com.xpay.common.util.utils.StringUtil;
import com.xpay.facade.rocketmq.manage.entity.TraceEntity;
import com.xpay.facade.rocketmq.manage.service.TraceManageFacade;
import com.xpay.web.pms.controller.BaseController;
import com.xpay.web.pms.vo.trade.MessageTraceQueryVO;
import org.apache.dubbo.config.annotation.Reference;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Author: Cmf
 * Date: 2019.12.2
 * Time: 16:24
 * Description:
 */
@RestController("messageTraceController")
@RequestMapping("messageTrace")
public class MessageTraceController extends BaseController {
    @Reference
    private TraceManageFacade traceManageFacade;


    /**
     * 查询通知列表
     *
     * @param messageTraceQueryVO .
     * @return .
     */
    @RequestMapping("listMessageTrace")
    @Permission("message:trace:view")
    public RestResult<List<TraceEntity>> listMessageTrace(@RequestBody MessageTraceQueryVO messageTraceQueryVO) {
        if (StringUtil.isEmpty(messageTraceQueryVO.getTrxNo())) {
            return RestResult.success(traceManageFacade.listTraceEntityPage(messageTraceQueryVO.getPubTimeBegin(), messageTraceQueryVO.getPubTimeEnd()));
        } else {
            return RestResult.success(traceManageFacade.listTraceEntityByTrxNo(messageTraceQueryVO.getTrxNo()));
        }
    }

    @RequestMapping("getTraceDetail")
    @Permission("message:trace:view")
    public RestResult<TraceEntity> getTraceDetail(@RequestParam String msgId) {
        return RestResult.success(traceManageFacade.getTraceDetail(msgId));
    }

    @RequestMapping("getMessageContent")
    @Permission("message:trace:view")
    public RestResult<MsgDto> getMessageContent(@RequestParam String topic, @RequestParam String msgId) {
        return RestResult.success(traceManageFacade.getMessageContent(topic, msgId));
    }
}
