<?php
namespace xpay;

/**
 * 密钥类
 * Class SecretKey
 * @package entity
 */
class SecretKey {
    /**
     * 对请求参数进行签名的私钥
     */
    private $reqSignKey = "";
    /**
     * 对响应参数进行验签的公钥
     */
    private $respVerifyKey = "";
    /**
     * 对sec_key进行加密的公钥
     */
    private $secKeyEncryptKey = "";
    /**
     * 对sec_key进行解密的私钥
     */
    private $secKeyDecryptKey = "";

    /**
     * @return string
     */
    public function getReqSignKey(): string
    {
        return $this->reqSignKey;
    }

    /**
     * @param string $reqSignKey
     */
    public function setReqSignKey(string $reqSignKey): void
    {
        $this->reqSignKey = $reqSignKey;
    }

    /**
     * @return string
     */
    public function getRespVerifyKey(): string
    {
        return $this->respVerifyKey;
    }

    /**
     * @param string $respVerifyKey
     */
    public function setRespVerifyKey(string $respVerifyKey): void
    {
        $this->respVerifyKey = $respVerifyKey;
    }

    /**
     * @return string
     */
    public function getSecKeyEncryptKey(): string
    {
        return $this->secKeyEncryptKey;
    }

    /**
     * @param string $secKeyEncryptKey
     */
    public function setSecKeyEncryptKey(string $secKeyEncryptKey): void
    {
        $this->secKeyEncryptKey = $secKeyEncryptKey;
    }

    /**
     * @return string
     */
    public function getSecKeyDecryptKey(): string
    {
        return $this->secKeyDecryptKey;
    }

    /**
     * @param string $secKeyDecryptKey
     */
    public function setSecKeyDecryptKey(string $secKeyDecryptKey): void
    {
        $this->secKeyDecryptKey = $secKeyDecryptKey;
    }

}