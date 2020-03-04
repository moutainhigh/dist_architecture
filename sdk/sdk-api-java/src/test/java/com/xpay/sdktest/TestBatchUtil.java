package com.xpay.sdktest;

import com.xpay.sdk.api.entity.Request;
import com.xpay.sdk.api.entity.Response;
import com.xpay.sdk.api.entity.SecretKey;
import com.xpay.sdk.api.enums.OrderStatus;
import com.xpay.sdk.api.enums.RespCode;
import com.xpay.sdk.api.enums.SignType;
import com.xpay.sdk.api.utils.AESUtil;
import com.xpay.sdk.api.utils.JsonUtil;
import com.xpay.sdk.api.utils.RandomUtil;
import com.xpay.sdk.api.utils.RequestUtil;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class TestBatchUtil {
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

        String secKey = RandomUtil.get16LenStr();//生成随机的密钥

        int maxCount = 5;
        List<SingleVo> detailList = new ArrayList<>();
        BigDecimal totalAmount = BigDecimal.ZERO;
        for(int i=1; i<=maxCount; i++){
            SingleVo singleVo = new SingleVo();
            singleVo.setCount("1");
            singleVo.setProduct_amount(BigDecimal.valueOf(20.01));
            singleVo.setProduct_name(AESUtil.encryptECB("都是交流交流发就发给对方感到我认为日u我认465dff34DWS34PO发的发生的34343，。？@！#%￥%~,;'=》》‘；【】@发生的开发商的方式飞机克里斯多夫快回家的思考方式对方老师的讲课费" + i, secKey));

            totalAmount = totalAmount.add(singleVo.getProduct_amount());
            detailList.add(singleVo);
        }
        BatchVo batchVo = new BatchVo();
        batchVo.setTotal_count(String.valueOf(detailList.size()));
        batchVo.setTotal_amount(totalAmount);
        batchVo.setDetail_list(detailList);

        final Request request = new Request();
        request.setMethod("demo.batch");
        request.setVersion("1.0");
        request.setMch_no("444000000000001");
        request.setSign_type(SignType.RSA.getValue());
        request.setData(batchVo);
        request.setSec_key(secKey);

        final String url = "127.0.0.1:8099/backend";
        try{
            Response response = RequestUtil.doRequest(url, request, key);
            BatchRespVo batchVoResp = JsonUtil.toBean(response.getData(), BatchRespVo.class);

            if(RespCode.SUCCESS.getCode().equals(response.getResp_code())
                    && OrderStatus.SUCCESS.getStatus().equals(batchVoResp.getOrder_status())){
                //交易成功
                System.out.println("交易成功 Response = " + JsonUtil.toString(response));

                for(SingleRespVo singleVo : batchVoResp.getSingle_List()){
                    singleVo.setProduct_name(AESUtil.decryptECB(singleVo.getProduct_name(), response.getSec_key()));
                }

                System.out.println("解密后的data数据为 = " + JsonUtil.toString(batchVoResp));
            }else{
                System.out.println("交易未完成 Response = " + JsonUtil.toString(response));
            }
        }catch(Throwable e){
            e.printStackTrace();
        }
    }
}
