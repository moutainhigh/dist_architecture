package com.xpay.demo.gateway;

import com.xpay.api.base.dto.RequestDto;
import com.xpay.api.base.dto.ResponseDto;
import com.xpay.common.statics.enums.common.OrderStatusEnum;
import com.xpay.common.util.utils.AESUtil;
import com.xpay.common.util.utils.JsonUtil;
import com.xpay.common.util.utils.RandomUtil;
import com.xpay.demo.gateway.vo.BatchRespVo;
import com.xpay.demo.gateway.vo.BatchVo;
import com.xpay.demo.gateway.vo.SingleRespVo;
import com.xpay.demo.gateway.vo.SingleVo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/demo")
public class DemoController {
    Logger logger = LoggerFactory.getLogger(this.getClass());

    @RequestMapping("single")
    public ResponseDto<SingleRespVo> single(@RequestBody RequestDto<SingleVo> request){
        logger.info("RequestDto = {}", JsonUtil.toString(request));

//        if(true){
//            throw BizException.bizFailResp("", "测试controller中抛出的异常");
//        }

        SingleRespVo respVo = new SingleRespVo();
        respVo.setOrderStatus(OrderStatusEnum.SUCCESS.getCode());
        respVo.setProductName("响应名称：" + AESUtil.decryptECB(request.getData().getProductName(), request.getSecKey()));//解密

        String secKey = RandomUtil.get16LenStr();
        respVo.setProductName(AESUtil.encryptECB(respVo.getProductName(), secKey));//加密

        ResponseDto<SingleRespVo> resp = ResponseDto.success(request.getMchNo(), request.getSignType(), respVo);
        resp.setSecKey(secKey);
        return resp;
    }

    @RequestMapping("batch")
    public ResponseDto<BatchRespVo> batch(@RequestBody RequestDto<BatchVo> request){
        logger.info("RequestDto = {}", JsonUtil.toString(request));

        int index = 0;
        String secKey = RandomUtil.get16LenStr();
        List<SingleRespVo> singleRespVoList = new ArrayList<>();

        for(SingleVo singleVo : request.getData().getDetailList()){

            SingleRespVo singleRespVo = new SingleRespVo();
            String nameDec = AESUtil.decryptECB(singleVo.getProductName(), request.getSecKey());
            if(index%3 == 0){
                singleRespVo.setOrderStatus(OrderStatusEnum.SUCCESS.getCode());
                nameDec = "受理成功名称：" + nameDec;
                singleRespVo.setProductName(AESUtil.encryptECB(nameDec, secKey));
            }else if(index%3 == 1){
                singleRespVo.setOrderStatus(OrderStatusEnum.PENDING.getCode());
                nameDec = "受理中名称：" + nameDec;
                singleRespVo.setProductName(AESUtil.encryptECB(nameDec, secKey));
            }else{
                singleRespVo.setOrderStatus(OrderStatusEnum.FAIL.getCode());
                nameDec = "受理失败名称：" + nameDec;
                singleRespVo.setProductName(AESUtil.encryptECB(nameDec, secKey));
            }
            singleRespVoList.add(singleRespVo);
            index++ ;
        }

        BatchRespVo batchRespVo = new BatchRespVo();
        batchRespVo.setOrderStatus(OrderStatusEnum.SUCCESS.getCode());
        batchRespVo.setTotalCount(String.valueOf(singleRespVoList.size()));
        batchRespVo.setSingleList(singleRespVoList);

        ResponseDto responseDto = ResponseDto.success(request.getMchNo(), request.getSignType(), batchRespVo);
        responseDto.setSecKey(secKey);
        return responseDto;
    }

    /** -----------------------------------下面两个方法是测试webflux时使用 ---------------------------- */
//    @RequestMapping("single1")
//    public reactor.core.publisher.Mono<ResponseDto<SingleRespVo>> single1(@RequestBody RequestDto<SingleVo> request){
//        ResponseDto<SingleRespVo> resp = single(request);
//        if(true){
//            throw BizException.bizFailResp("", "测试controller中抛出的异常1");
//        }
//        return reactor.core.publisher.Mono.just(resp);
//    }
//    @RequestMapping("batch1")
//    public reactor.core.publisher.Mono<ResponseDto<BatchRespVo>> batch1(@RequestBody RequestDto<BatchVo> request){
//        ResponseDto<BatchRespVo> resp = batch(request);
//        return reactor.core.publisher.Mono.just(resp);
//    }
}
