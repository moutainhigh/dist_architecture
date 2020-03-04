<?php
namespace xpay;

/**
 * 样例VO，模拟批量交易的明细业务对象
 * Class SingleVo
 * @package testvo
 */
class SingleVo implements \JsonSerializable {
    private $product_name;
    private $product_amount;
    private $count;

    /**
     * 需要实现此方法，以便json_encode()方法能返回私有属性
     * @return array|mixed
     */
    public function jsonSerialize(){
        $vars = get_object_vars($this);
        return $vars;
    }

    /**
     * @return mixed
     */
    public function getProductName()
    {
        return $this->product_name;
    }

    /**
     * @param mixed $product_name
     */
    public function setProductName($product_name): void
    {
        $this->product_name = $product_name;
    }

    /**
     * @return mixed
     */
    public function getProductAmount()
    {
        return $this->product_amount;
    }

    /**
     * @param mixed $product_amount
     */
    public function setProductAmount($product_amount): void
    {
        $this->product_amount = $product_amount;
    }

    /**
     * @return mixed
     */
    public function getCount()
    {
        return $this->count;
    }

    /**
     * @param mixed $count
     */
    public function setCount($count): void
    {
        $this->count = $count;
    }

}