package com.xpay.sdktest;

import com.xpay.sdk.api.entity.Request;
import com.xpay.sdk.api.entity.Response;
import com.xpay.sdk.api.entity.SecretKey;
import com.xpay.sdk.api.enums.OrderStatus;
import com.xpay.sdk.api.enums.RespCode;
import com.xpay.sdk.api.enums.SignType;
import com.xpay.sdk.api.utils.JsonUtil;
import com.xpay.sdk.api.utils.RequestUtil;

import java.math.BigDecimal;
import java.util.*;

public class TestSingleUtil {
    //平台公钥
    public static String platPublicKey = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCmxhJQdH2mgJ0qI6o3298O70+MMDnAqaR759e1+qIYYKId77yS0DH+32WsAyT0S7QqB2aeermQTRYep83taxfpY60/FKEXJMtzuwgTK6HMnKj/y6Y2tsvqYHLRwpcppatJSI0xtrQQgTobObY0OXUgNhfa7FJlT5QI0WWPGrdH5wIDAQAB";
    //商户私钥
    public static String mchPrivateKey = "MIICdwIBADANBgkqhkiG9w0BAQEFAASCAmEwggJdAgEAAoGBAK3x/rhol/h8cZdwdlmL/owbsGnGp81xWjzNXqWPj79MePS6xg5Eq9/OMFhz19f8/7UNaYnSM40GDXU5XAZwKahIc1ZHiUOF/8kjdCdibeuAohOy/n3x9IE+JkMve5Jt6gK7ImUB+tlxGK/hvs0ZS3oWv/C5FK1m4QfAL2w3ABqLAgMBAAECgYEAjrdtocoXXkZkQLeCMwh8ymHxhWf7x8EII7jwvfAjJfbNiyYvWAeZ6V/YJMqvPsSS3O7rN6wipcNgiVyNl66xTcacVSzdo5O8qE7LRq/SqKghgeCKiFCkBf0jz1yhfeTFz6FIA6bPqW2a2mph7H3BjAtNz9Kp0BUIIO8wT9gqTokCQQDzFrm3w3g5TvYJ8GEoL2yRjC5yKDplPR7WF1yPG2jE1TNxs/GQw+r6ytp26EnMrEvOZD6GonadsvZbFhuLkKa/AkEAty8gbJXh/j5mcsUXjaKLZO/Tu5myLIBHRLWRtCJalSEvq+OhzhWljU2BS5rIaRZUK7x05db5vfA6d0jTBFSrNQJBAKnW/ekbpDr+JT8qeOdXwxYqCCVwX+RuAMZ6SgwejWPAxhGyNJv9loalBmA/yZg75TcNqPXuOsvRxvg7CQHJDgcCQCrV1VIu9kq6ksBRwEE5ejkfL7bVqOlpEcFKaAjkY6Qtv3UFZMfJHg5dyldAmY6hTwtvJTK9w2ab70AHgninx+UCQGfbygkQxP9jtowmJtfRdhSZI7YZNzuoElqX/vdPkn/CWDtMRkbuFVMfR92C9YvARTFjMZvUCZrdcz1bUZ8xFGc=";
    public static String md5Key = "B87F24EC25310CD4402A360CD150BF6B";

    public static void main(String[] args){
        final SecretKey key = new SecretKey();
        key.setReqSignKey(mchPrivateKey);//签名：使用商户私钥
        key.setRespVerifyKey(platPublicKey);//验签：使用平台公钥
        key.setSecKeyEncryptKey(platPublicKey);//sec_key加密：使用平台公钥
        key.setSecKeyDecryptKey(mchPrivateKey);//sec_key解密：使用商户私钥

//        key.setReqSignKey(md5Key);
//        key.setRespVerifyKey(md5Key);
        SingleVo vo = new SingleVo();
        vo.setProduct_name("xx商品123");
        vo.setProduct_amount(BigDecimal.valueOf(20.01));
        vo.setCount("13");

        Request request = new Request();
        request.setMch_no("888800000001");
        request.setMethod("demo.single");
        request.setVersion("1.0");
        request.setSign_type(SignType.RSA.getValue());
        request.setData(vo);

        final String url = "127.0.0.1:8099/backend";
        try{
            Response response = RequestUtil.doRequest(url, request, key);
            Map<String, Object> respData = JsonUtil.toBean(response.getData(), HashMap.class);

            if(RespCode.SUCCESS.getCode().equals(response.getResp_code())
                    && OrderStatus.SUCCESS.getStatus().equals(respData.get("order_status"))){
                //交易成功
                SingleVo respVo = JsonUtil.toBean(response.getData(), SingleVo.class);
                System.out.println("交易成功 SingleVo = " + JsonUtil.toString(respVo) + " Response = " + JsonUtil.toString(response));
            }else{
                System.out.println("交易未完成 Response = " + JsonUtil.toString(response));
            }
        }catch(Throwable e){
            e.printStackTrace();
        }
    }
}
