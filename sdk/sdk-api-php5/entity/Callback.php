<?php

namespace xpay;

class Callback
{
    private $biz_code;
    private $biz_msg;
    private $data;
    private $rand_str;
    private $sign_type;
    private $mch_no;
    private $sign;
    private $sec_key;

    /**
     * @return mixed
     */
    public function getBizCode()
    {
        return $this->biz_code;
    }

    /**
     * @param mixed $biz_code
     */
    public function setBizCode($biz_code)
    {
        $this->biz_code = $biz_code;
    }

    /**
     * @return mixed
     */
    public function getBizMsg()
    {
        return $this->biz_msg;
    }

    /**
     * @param mixed $biz_msg
     */
    public function setBizMsg($biz_msg)
    {
        $this->biz_msg = $biz_msg;
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
    public function setData($data)
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
    public function setRandStr($rand_str)
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
    public function setSignType($sign_type)
    {
        $this->sign_type = $sign_type;
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
    public function setMchNo($mch_no)
    {
        $this->mch_no = $mch_no;
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
    public function setSign($sign)
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
    public function setSecKey($sec_key)
    {
        $this->sec_key = $sec_key;
    }

    public function successResp()
    {
        return "success";
    }

    public function retryResp()
    {
        return "retry";
    }
}