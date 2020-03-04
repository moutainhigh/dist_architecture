<?php
/**
 * 测试入口类 -> 单笔交易
 *
 * PHP VERSION = PHP 7.2.20
 */
require "Autoload.php";

use xpay\Request;
use xpay\SecretKey;
use xpay\RequestUtil;
use xpay\RandomUtil;
use xpay\ObjectUtil;
use xpay\AESUtil;
use xpay\SingleVo;
use xpay\SingleRespVo;

//平台公钥
$platPublicKey = "-----BEGIN PUBLIC KEY-----
MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCmxhJQdH2mgJ0qI6o3298O70+MMDnAqaR759e1+qIYYKId77yS0DH+32WsAyT0S7QqB2aeermQTRYep83taxfpY60/FKEXJMtzuwgTK6HMnKj/y6Y2tsvqYHLRwpcppatJSI0xtrQQgTobObY0OXUgNhfa7FJlT5QI0WWPGrdH5wIDAQAB
-----END PUBLIC KEY-----";

//商户私钥
$mchPrivateKey = "-----BEGIN RSA PRIVATE KEY-----
MIICdwIBADANBgkqhkiG9w0BAQEFAASCAmEwggJdAgEAAoGBAK3x/rhol/h8cZdwdlmL/owbsGnGp81xWjzNXqWPj79MePS6xg5Eq9/OMFhz19f8/7UNaYnSM40GDXU5XAZwKahIc1ZHiUOF/8kjdCdibeuAohOy/n3x9IE+JkMve5Jt6gK7ImUB+tlxGK/hvs0ZS3oWv/C5FK1m4QfAL2w3ABqLAgMBAAECgYEAjrdtocoXXkZkQLeCMwh8ymHxhWf7x8EII7jwvfAjJfbNiyYvWAeZ6V/YJMqvPsSS3O7rN6wipcNgiVyNl66xTcacVSzdo5O8qE7LRq/SqKghgeCKiFCkBf0jz1yhfeTFz6FIA6bPqW2a2mph7H3BjAtNz9Kp0BUIIO8wT9gqTokCQQDzFrm3w3g5TvYJ8GEoL2yRjC5yKDplPR7WF1yPG2jE1TNxs/GQw+r6ytp26EnMrEvOZD6GonadsvZbFhuLkKa/AkEAty8gbJXh/j5mcsUXjaKLZO/Tu5myLIBHRLWRtCJalSEvq+OhzhWljU2BS5rIaRZUK7x05db5vfA6d0jTBFSrNQJBAKnW/ekbpDr+JT8qeOdXwxYqCCVwX+RuAMZ6SgwejWPAxhGyNJv9loalBmA/yZg75TcNqPXuOsvRxvg7CQHJDgcCQCrV1VIu9kq6ksBRwEE5ejkfL7bVqOlpEcFKaAjkY6Qtv3UFZMfJHg5dyldAmY6hTwtvJTK9w2ab70AHgninx+UCQGfbygkQxP9jtowmJtfRdhSZI7YZNzuoElqX/vdPkn/CWDtMRkbuFVMfR92C9YvARTFjMZvUCZrdcz1bUZ8xFGc=
-----END RSA PRIVATE KEY-----";
$maxCount = 5;
$totalCount = 0;
$totalAmount = 0;
$secKey = RandomUtil::randomStr(16);

$single = new SingleVo();
$single->setProductAmount(20.01);
$single->setProductName("都是交流交流发就发给对方感到我认为日u我认465dff34DWS34PO发的发生的34343，。？@！#%￥%~,;'=》》‘；【】@发生的开发商的方式飞机克里斯多夫快回家的思考方式对方老师的讲课费");
$single->setCount(1);
//加密
$single->setProductName(AESUtil::encryptECB($single->getProductName(), $secKey));

$dataStr = json_encode($single, JSON_UNESCAPED_UNICODE); //JSON_UNESCAPED_UNICODE避免中文转码

$request = new Request();
$request->setMethod("demo.single");
$request->setVersion("1.0");
$request->setMchNo("888800000001");
$request->setSignType("2");
$request->setRandStr(RandomUtil::randomStr(32));
$request->setData($dataStr);
$request->setSecKey($secKey);//rsa有效

$secretKey = new SecretKey();
$secretKey->setReqSignKey($mchPrivateKey);//签名：使用商户私钥
$secretKey->setRespVerifyKey($platPublicKey);//验签：使用平台公钥
$secretKey->setSecKeyEncryptKey($platPublicKey);//sec_key加密：使用平台公钥
$secretKey->setSecKeyDecryptKey($mchPrivateKey);//sec_key解密：使用商户私钥

$url = "127.0.0.1:8099/backend";
try {
    $response = RequestUtil::doRequest($url, $request, $secretKey);

    echo "响应数据为 Response = ";
    print_r($response);

    if ($response->isSuccess()) {
        $singleResp = new SingleRespVo();
        ObjectUtil::fillProperties($singleResp, $response->getData());

        $singleResp->setProductName(AESUtil::decryptECB($singleResp->getProductName(), $response->getSecKey()));
        echo "响应的data数据为 data = ";
        print_r($singleResp);
    }else{
        echo "错误信息为 resp_msg = " . $response->getRespMsg();
    }
} catch (Exception $e) {
    print_r($e);
}

