package com.cuzz.webac.dao;


import com.cuzz.webac.mapper.OrderDOMapper;
import com.cuzz.webac.model.doo.OrderDO;
import com.cuzz.webac.model.doo.OrderDOExample;
import com.cuzz.webac.model.dto.RequestOrderInfoDTO;
import com.cuzz.webac.utils.OrderStatus;
import com.cuzz.webac.utils.PaymentStatus;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
@Slf4j
@Component
public class OrderDao {

    @Autowired
    OrderDOMapper orderDOMapper;


//    order_status -- 订单状态 (0:待支付, 1:处理中, 2:已发货, 3:已签收, 4:已完成, 5:已取消, 6:要退款)
//    payment_status  -- 付款状态 (0:待支付, 1:已支付, 2:已退款)

    //查询 对应Order的付款状态是否为已支付 1,订单状态为处理中 1
    public Boolean checkOrderIsPayAndProcessing(String orderNumber){
        OrderDOExample orderDOExample = new OrderDOExample();
        orderDOExample.createCriteria().andOrderNumberEqualTo(orderNumber)
                .andOrderStatusEqualTo(OrderStatus.PROCESSING.getCode())
                .andPaymentStatusEqualTo(PaymentStatus.PAID.getCode());

        List<OrderDO> orderDOS = this.orderDOMapper.selectByExample(orderDOExample);

        return !orderDOS.isEmpty();
    }

    public Boolean updateOrderInfo(OrderDO orderDO){

        int i = this.orderDOMapper.updateByPrimaryKeySelective(orderDO);
        return i>0;


    }


    public List<OrderDO> getPageOrder(){
        OrderDOExample orderDOExample = new OrderDOExample();
        List<OrderDO> orderDOList = this.orderDOMapper.selectByExampleWithBLOBs(orderDOExample);
        return orderDOList;
    }

    public Boolean updateOrderToProcessing(String orderNumber){
        log.info("更新传来的订单号是"+orderNumber);
        OrderDO order = OrderDO.builder().orderStatus(OrderStatus.PROCESSING.getCode()).paymentStatus(PaymentStatus.PAID.getCode()).build();
        OrderDOExample orderDOExample = new OrderDOExample();
        orderDOExample.createCriteria().andOrderNumberEqualTo(orderNumber);
        int i = this.orderDOMapper.updateByExampleSelective(order,orderDOExample);

        System.out.println("i 为 :"+i);
        return i>0;
    }

    public OrderDO spawnNewOrder(OrderDO orderDO){
        this.orderDOMapper.insertSelective(orderDO);
        return orderDO;
    }
    public OrderDO getOrderDoByOrderNumber(String orderNumber){
        OrderDOExample orderDOExample = new OrderDOExample();
        orderDOExample.createCriteria().andOrderNumberEqualTo(orderNumber);

        List<OrderDO> orderDo = this.orderDOMapper.selectByExampleWithBLOBs(orderDOExample);
        if (orderDo.isEmpty()){
            return null;
        }

        return orderDo.get(0);

    }
    public OrderDO getOrderDoByOrderId(int orderID){
        OrderDOExample orderDOExample = new OrderDOExample();
        orderDOExample.createCriteria().andOrderIdEqualTo(orderID);
        List<OrderDO> orderDOS = this.orderDOMapper.selectByExampleWithBLOBs(orderDOExample);
        if (orderDOS.isEmpty()){
            return null;
        }

        return orderDOS.get(0);

    }

}
