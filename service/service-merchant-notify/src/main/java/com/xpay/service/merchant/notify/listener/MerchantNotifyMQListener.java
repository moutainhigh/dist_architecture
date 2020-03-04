package com.xpay.service.merchant.notify.listener;

import com.xpay.api.base.dto.CallbackDto;
import com.xpay.api.base.dto.CallbackResp;
import com.xpay.api.base.utils.CallbackUtil;
import com.xpay.common.statics.constants.rmqdest.MerchantNotifyDest;
import com.xpay.common.statics.dto.merchant.notify.MerchantNotifyDto;
import com.xpay.common.statics.dto.rmq.MsgDto;
import com.xpay.common.statics.exception.CommonExceptions;
import com.xpay.common.util.utils.JsonUtil;
import com.xpay.facade.merchant.entity.MerchantSecret;
import com.xpay.facade.merchant.service.MerchantSecretFacade;
import org.apache.dubbo.config.annotation.Reference;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * Author: Cmf
 * Date: 2019.12.17
 * Time: 15:48
 * Description: 商户异步能知消息接收
 */
@Component
public class MerchantNotifyMQListener {
    private Logger logger = LoggerFactory.getLogger(MerchantNotifyMQListener.class);

    @Reference
    private MerchantSecretFacade merchantSecretFacade;

    @Component
    @RocketMQMessageListener(topic = MerchantNotifyDest.TOPIC_MERCHANT_NOTIFY, selectorExpression = MerchantNotifyDest.TAG_MERCHANT_NOTIFY, consumeThreadMax = 8, consumerGroup = "merchantNotifyConsumer")
    public class MerchantNotifyConsumer implements RocketMQListener<MsgDto> {
        public void onMessage(MsgDto msgDto) {
            logger.info("发送商户异步通知,trxNo={},jsonParam={}", msgDto.getTrxNo(), msgDto.getJsonParam());

            String jsonParam = msgDto.getJsonParam();
            try {
                MerchantNotifyDto merchantNotifyDto = JsonUtil.toBean(jsonParam, MerchantNotifyDto.class);
                MerchantSecret merchantSecret = merchantSecretFacade.getByMerchantNo(merchantNotifyDto.getMchNo());
                if (merchantSecret == null) {
                    logger.error("商户密钥不存在,jsonParam={}", jsonParam);
                    return;
                }

                CallbackDto callbackDto = new CallbackDto();
                callbackDto.setData(merchantNotifyDto.getData());
                callbackDto.setMch_no(merchantNotifyDto.getMchNo());
                callbackDto.setRand_str(merchantNotifyDto.getRandStr());
                callbackDto.setSign_type(String.valueOf(merchantSecret.getSignType()));
                callbackDto.setSec_key(merchantNotifyDto.getSecKey());
                CallbackResp callbackResp = CallbackUtil.callbackSync(merchantNotifyDto.getCallbackUrl(), callbackDto, merchantSecret.getPlatformPrivateKey(), merchantSecret.getMerchantPublicKey());
                if (callbackResp.getStatus().equals(CallbackResp.SUCCESS)) {
                    logger.info("商户异步通知成功,jsonParam={}", jsonParam);
                } else if (callbackResp.getStatus().equals(CallbackResp.FAIL)) {
                    logger.info("商户异步通知失败,不再重复请求,jsonParam={},resp={}", jsonParam, JsonUtil.toString(callbackResp));
                } else {
                    logger.error("商户异步通知失败,抛出异常,jsonParam={},callbackResp={}", jsonParam, JsonUtil.toString(callbackResp));
                    throw CommonExceptions.CONNECT_ERROR.newWithErrMsg("商户通知失败，抛出异常，稍后重新请求");
                }
            } catch (com.xpay.common.statics.exception.BizException bizEx) {
                logger.error("发送商户异步通知出现异常,jsonParam={}", jsonParam, bizEx);
                if (bizEx.isMqRetry()) {
                    throw bizEx;
                }
            } catch (Exception ex) {
                logger.error("发送商户异步通知出现异常,jsonParam={}", jsonParam, ex);
                throw ex;
            }
        }
    }
}
