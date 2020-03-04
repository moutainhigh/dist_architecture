<?php
namespace xpay;

/**
 * 样例VO，模拟批量交易的批次业务对象
 * Class BatchVo
 * @package testvo
 */
class BatchVo implements \JsonSerializable {
    private $total_count;
    private $total_amount;
    private $detail_list;

    /**
     * 需要实现此方法，以便json_encode()方法能返回私有属性
     * @return array|mixed
     */
    public function jsonSerialize(){
        $data = [];
        foreach ($this as $key => $val){
            $data[$key] = $val;
        }
        return $data;
    }

    /**
     * @return mixed
     */
    public function getTotalCount()
    {
        return $this->total_count;
    }

    /**
     * @param mixed $total_count
     */
    public function setTotalCount($total_count): void
    {
        $this->total_count = $total_count;
    }

    /**
     * @return mixed
     */
    public function getTotalAmount()
    {
        return $this->total_amount;
    }

    /**
     * @param mixed $total_amount
     */
    public function setTotalAmount($total_amount): void
    {
        $this->total_amount = $total_amount;
    }

    /**
     * @return mixed
     */
    public function getDetailList()
    {
        return $this->detail_list;
    }

    /**
     * @param mixed $detail_list
     */
    public function setDetailList($detail_list): void
    {
        $this->detail_list = $detail_list;
    }
}