package com.xuecheng.orders.service;

import com.xuecheng.orders.model.dto.AddOrderDto;
import com.xuecheng.orders.model.dto.PayRecordDto;
import com.xuecheng.orders.model.po.XcPayRecord;

public interface OrderService {


    /**
     * @param addOrderDto 订单信息
     * @return PayRecordDto 支付记录(包括二维码)
     * @description 创建商品订单
     * @author Mr.M
     * @date 2022/10/4 11:02
     */
    PayRecordDto createOrder(String userId, AddOrderDto addOrderDto);


    /**
     * @param payNo 交易记录号
     * @return com.xuecheng.orders.model.po.XcPayRecord
     * @description 查询支付记录
     * @author Mr.M
     * @date 2022/10/20 23:38
     */
    XcPayRecord getPayRecordByPayno(String payNo);
}