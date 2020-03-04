<?php
namespace xpay;

/**
 * 请求参数类
 * Class Request
 * @package entity
 */
class Request implements \JsonSerializable {
    private $method = "";
    private $version = "1.0";
    private $data;
    private $rand_str = "";
    private $sign_type = "2";
    private $mch_no = "";
    private $sign = "";
    private $sec_key = "";

    /**
     * 进行json序列化时返回的内容
     * @return array|mixed
     */
    public function jsonSerialize()
    {
        $vars = get_object_vars($this);
        return $vars;
    }

    /**
     * @return string
     */
    public function getMethod(): string
    {
        return $this->method;
    }

    /**
     * @param string $method
     */
    public function setMethod(string $method): void
    {
        $this->method = $method;
    }

    /**
     * @return string
     */
    public function getVersion(): string
    {
        return $this->version;
    }

    /**
     * @param string $version
     */
    public function setVersion(string $version): void
    {
        $this->version = $version;
    }

    /**
     * @return mixed
     */
    public function getData()
    {
        return $this->data;
    }

    /**
     * @param mixed $data
     */
    public function setData($data): void
    {
        $this->data = $data;
    }

    /**
     * @return string
     */
    public function getRandStr(): string
    {
        return $this->rand_str;
    }

    /**
     * @param string $rand_str
     */
    public function setRandStr(string $rand_str): void
    {
        $this->rand_str = $rand_str;
    }

    /**
     * @return string
     */
    public function getSignType(): string
    {
        return $this->sign_type;
    }

    /**
     * @param string $sign_type
     */
    public function setSignType(string $sign_type): void
    {
        $this->sign_type = $sign_type;
    }

    /**
     * @return string
     */
    public function getMchNo(): string
    {
        return $this->mch_no;
    }

    /**
     * @param string $mch_no
     */
    public function setMchNo(string $mch_no): void
    {
        $this->mch_no = $mch_no;
    }

    /**
     * @return string
     */
    public function getSign(): string
    {
        return $this->sign;
    }

    /**
     * @param string $sign
     */
    public function setSign(string $sign): void
    {
        $this->sign = $sign;
    }

    /**
     * @return string
     */
    public function getSecKey(): string
    {
        return $this->sec_key;
    }

    /**
     * @param string $sec_key
     */
    public function setSecKey(string $sec_key): void
    {
        $this->sec_key = $sec_key;
    }

}