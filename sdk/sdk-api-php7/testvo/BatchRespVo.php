<?php
namespace xpay;


class BatchRespVo
{
    private $order_status;
    private $total_count;
    private $single_List;

    /**
     * @return mixed
     */
    public function getOrderStatus()
    {
        return $this->order_status;
    }

    /**
     * @param mixed $order_status
     */
    public function setOrderStatus($order_status): void
    {
        $this->order_status = $order_status;
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
    public function getSingleList()
    {
        return $this->single_List;
    }

    /**
     * @param mixed $single_List
     */
    public function setSingleList($single_List): void
    {
        $this->single_List = $single_List;
    }


}