<?php
namespace xpay;

/**
 * 响应参数类
 * Class Response
 * @package entity
 */
class Response {
    private $resp_code;
    private $resp_msg;
    private $mch_no;
    private $data;
    private $rand_str;
    private $sign_type;
    private $sign;
    private $sec_key;

    /**
     * @return mixed
     */
    public function getRespCode()
    {
        return $this->resp_code;
    }

    /**
     * @param mixed $resp_code
     */
    public function setRespCode($resp_code): void
    {
        $this->resp_code = $resp_code;
    }

    /**
     * @return mixed
     */
    public function getRespMsg()
    {
        return $this->resp_msg;
    }

    /**
     * @param mixed $resp_msg
     */
    public function setRespMsg($resp_msg): void
    {
        $this->resp_msg = $resp_msg;
    }

    /**
     * @return mixed
     */
    public function getMchNo()
    {
        return $this->mch_no;
    }

    /**
     * @param mixed $mch_no
     */
    public function setMchNo($mch_no): void
    {
        $this->mch_no = $mch_no;
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
     * @return mixed
     */
    public function getRandStr()
    {
        return $this->rand_str;
    }

    /**
     * @param mixed $rand_str
     */
    public function setRandStr($rand_str): void
    {
        $this->rand_str = $rand_str;
    }

    /**
     * @return mixed
     */
    public function getSignType()
    {
        return $this->sign_type;
    }

    /**
     * @param mixed $sign_type
     */
    public function setSignType($sign_type): void
    {
        $this->sign_type = $sign_type;
    }

    /**
     * @return mixed
     */
    public function getSign()
    {
        return $this->sign;
    }

    /**
     * @param mixed $sign
     */
    public function setSign($sign): void
    {
        $this->sign = $sign;
    }

    /**
     * @return mixed
     */
    public function getSecKey()
    {
        return $this->sec_key;
    }

    /**
     * @param mixed $sec_key
     */
    public function setSecKey($sec_key): void
    {
        $this->sec_key = $sec_key;
    }

    /**
     * 判断本次请求是否成功
     * @return bool
     */
    public function isSuccess(): bool
    {
        return "01" === $this->resp_code;
    }

    /**
     * 从resp_msg中获取错误码
     * @return string
     */
    public function getRespMsgCode(): string
    {
        if($this->resp_msg && ($index = strpos($this->resp_msg, "[")) >= 0
            && strpos($this->resp_msg, "]") == (strlen($this->resp_msg)-1)){
            $code = substr($this->resp_msg, $index+1);
            return substr($code, 0, strlen($code)-1);
        }else{
            return "";
        }
    }
}