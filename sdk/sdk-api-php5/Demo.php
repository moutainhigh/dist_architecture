<?php
/**
 * 测试样例类
 *
 * PHP VERSION = PHP 5.6
 */
require "Autoload.php";

use xpay\Request;
use xpay\SecretKey;
use xpay\RequestUtil;
use xpay\RandomUtil;
use xpay\AESUtil;

//平台公钥
$platPublicKey = "-----BEGIN PUBLIC KEY-----
MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCM3URE1UGDxtQw3iuUnraEwVytyWDwjSt/dd0Pbev8ax6MEzozwbyKXIIoFTowpTG1PMjmXlYFVb/zDxbj5mwQAg/2CTj7Z6sbjOWNAV2JTROuomrxag4dg7rXZu1WWkGrMuZbpyNLzcFPIgHJ/jkHnEKEC8+WygSklhjF9BUOqwIDAQAB
-----END PUBLIC KEY-----";

//商户私钥
$mchPrivateKey = "-----BEGIN RSA PRIVATE KEY-----
MIICWwIBAAKBgQCAzW049Do1JERlM1by8ZZD/iDTR4TxHUr0rfa7S2oDP6fjPE9V4DmjZpOj7PSZjUyuy+Pd6wmaH/6p4VMOpXnFn8i1VMTYp56gbo3JySG5Bc1vAQpXix/sX3M4CoUy6gLsx7iNwxWGwj0g4FiIcZmqBTyMIlnyxEKifFii72jHDQIDAQABAoGAHie6NBTd0dZBWw4ng65y9002gzCh2893XoyiENyJn+QV2toiWuEprWtVEpJl+9M8ikn6+dhOwbJO7Zfu2aV9OiKiUUyU8N7zHkDla0QwaCznJdbgCZ+/Bs0gkf1B2RTKcf9jMWo0VQjeeLZFjJBC2NzV3+sWj5RlNYxZ23Bc1vECQQCJasSh6irX3utgQGGtAPZ3szRi5BNEegrnAWVaGHr6NZRDNMY3kXasMQ/9sCNK2pI8grwBjPv4PPmnhYEm2dhJAkEA7/OTu9n+5GEpHznbo/iT0P5x8Yhjh6HGift16LZ9+wFhVHOdixHRyFJmA7KAGaJEqZ8uHxL6ZszblbmmVr1gpQJAK8fofe2tWLmdJD1e4U+9NsXRmvAvc1Gux4qaT+ocxDrt0buuq2Wc+Z9oCNtlYyPgE4dTfur8HErQ/555V3dwgQJAJd50N1ZtMs/etI3mCxfFBFvT0cGrq5Eti0NDVYnPkhiiqrhgab1TNWFDtluqeeExIu/7UG4nVKgKE7Qr+06B/QJAM24SyalwWmicM/DDBQQTI2XxMWMhUAQksa4lE3Tj46i+u+E79djAdr+zhyxeL3C2bBC/8J9FHLn8azDODaQzsA==
-----END RSA PRIVATE KEY-----";

$secKey = RandomUtil::randomStr(16);

$data = [];
$data["mch_no_trade"] = "888100000003024";
$data["mch_order_no"] = "2019120200001";
$data["order_amount"] = "10.02";
$data["mch_req_time"] = date('Y-m-d H:i:s', time());
$data["order_desc"] = "测试sdk-php5";
$data["callback_url"] = "http://10.10.10.37:8080";
$data["callback_param"] = null;
$data["bank_card_no"] = AESUtil::encryptECB("6222600260001072444", $secKey);//加密

$request = new Request();
$request->setMethod("fastPay.agreement.pay");
$request->setVersion("1.0");
$request->setMchNo("888100000003024");
$request->setSignType("2");
$request->setRandStr(RandomUtil::randomStr(32));
$request->setData($data);
$request->setSecKey($secKey);//rsa有效

$secretKey = new SecretKey();
$secretKey->setReqSignKey($mchPrivateKey);//签名：使用商户私钥
$secretKey->setRespVerifyKey($platPublicKey);//验签：使用平台公钥
$secretKey->setSecKeyEncryptKey($platPublicKey);//sec_key加密：使用平台公钥
$secretKey->setSecKeyDecryptKey($mchPrivateKey);//sec_key解密：使用商户私钥

$url = "https://api.joinpay.com/fastpay";
try {
    $response = RequestUtil::doRequest($url, $request, $secretKey);
    if ($response->isSuccess()) {//受理成功
        $dataArr = json_decode($response->getData(), true);
        if($dataArr["order_status"] == "P1000"){//订单交易成功
            echo "SUCCESS, Response = ";
            print_r($response);
        }else{
            echo "FAIL OR PROCESSING OR UNKNOWN, Response = ";
            print_r($dataArr);
        }
    }else{
        echo "受理失败, Response = ";
        print_r($response);
    }
} catch (Exception $e) {
    print_r($e);
}

