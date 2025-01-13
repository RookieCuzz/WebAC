package com.cuzz.webac.dao;


import com.cuzz.webac.mapper.OrderDOMapper;
import com.cuzz.webac.model.doo.OrderDO;
import com.cuzz.webac.model.doo.OrderDOExample;
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

    //修改Order的付款状态为已支付 1,订单状态为处理中 1
    public Boolean checkOrderIsPayAndProcessing(String orderNumber){
        OrderDOExample orderDOExample = new OrderDOExample();
        orderDOExample.createCriteria().andOrderNumberEqualTo(orderNumber)
                .andOrderStatusEqualTo((byte)1)
                .andPaymentStatusEqualTo((byte)1);

        List<OrderDO> orderDOS = this.orderDOMapper.selectByExample(orderDOExample);

        return !orderDOS.isEmpty();
    }

    public List<OrderDO> getPageOrder(){
        OrderDOExample orderDOExample = new OrderDOExample();
        List<OrderDO> orderDOList = this.orderDOMapper.selectByExampleWithBLOBs(orderDOExample);
        return orderDOList;
    }

    public Boolean updateOrderToProcessing(String orderNumber){
        log.info("更新传来的订单号是"+orderNumber);
        OrderDO order = OrderDO.builder().orderStatus((byte) 1).paymentStatus((byte) 1).build();
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

        List<OrderDO> orderDo = this.orderDOMapper.selectByExample(orderDOExample);
        if (orderDo.isEmpty()){
            return null;
        }

        return orderDo.get(0);

    }
    public OrderDO getOrderDoByOrderId(int orderID){
        OrderDO orderDo = this.orderDOMapper.selectByPrimaryKey(orderID);

        return orderDo;

    }

}
