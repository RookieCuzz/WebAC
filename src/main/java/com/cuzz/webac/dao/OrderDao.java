package com.cuzz.webac.dao;


import com.cuzz.webac.mapper.OrderBelongProductDoMapper;
import com.cuzz.webac.mapper.OrderDOMapper;
import com.cuzz.webac.model.doo.OrderBelongProductDo;
import com.cuzz.webac.model.doo.OrderDO;
import com.cuzz.webac.model.doo.OrderDOExample;
import com.cuzz.webac.model.game.postbox.AdminItem;
import com.cuzz.webac.utils.OrderStatus;
import com.cuzz.webac.utils.PaymentStatus;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
public class OrderDao {

    @Autowired
    OrderDOMapper orderDOMapper;
    @Resource
    OrderBelongProductDoMapper orderBelongProductDoMapper;

    //String 格式 商品名@数量


    public List<AdminItem> getAllProductByOrder(String orderNumber){

        List<AdminItem> orderBelongProductDo = orderBelongProductDoMapper.queryAllProductByOrderNumber(orderNumber);
        return  orderBelongProductDo;
    };


    public boolean addProductToOrder(List<String> productsAndAmountList,String orderNumber) throws Exception {

        List<OrderBelongProductDo> products =new ArrayList<>();
        productsAndAmountList.forEach(
                item->{
                    String[] split = item.split("@");
                    if (split.length != 2) {
                        throw new IllegalArgumentException("Invalid product format: " + item);
                    }
                    OrderBelongProductDo orderBelongProductDo = new OrderBelongProductDo();
                    orderBelongProductDo.setProductName(split[0]);
                    orderBelongProductDo.setBelongOrderNumber(orderNumber);
                    orderBelongProductDo.setQuantity(Integer.valueOf(split[1]));
                    products.add(orderBelongProductDo);

                }

        );
        int i = orderBelongProductDoMapper.batchInsertOrderBelongProduct(products);
        if (i!=products.size()){
            return false;
        }else {
            return true;
        }

    };
    //String 格式 商品名@数量

    public boolean addProductToOrder(String productsAndAmount,String orderNumber) throws Exception {

        if (productsAndAmount==null||productsAndAmount.isEmpty()){
            return false;
        }
        String[] split = productsAndAmount.split("@");
        if (split.length != 2) {
            throw new IllegalArgumentException("Invalid product format: " +productsAndAmount);
        }
        OrderBelongProductDo orderBelongProductDo = new OrderBelongProductDo();
        orderBelongProductDo.setProductName(split[0]);
        orderBelongProductDo.setBelongOrderNumber(orderNumber);
        orderBelongProductDo.setQuantity(Integer.valueOf(split[1]));

        int i = orderBelongProductDoMapper.insertSelective(orderBelongProductDo);

        return i>0;

    };

    public boolean addProductToOrder(OrderBelongProductDo orderBelongProductDo){

        if (orderBelongProductDo==null){
            return false;
        }
        int i = orderBelongProductDoMapper.insertSelective(orderBelongProductDo);
        return i>0;


    };

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


    public byte queryOrderStatus(String orderNumber){

        byte orderStatus = this.orderDOMapper.queryOrderStatus(orderNumber);
        return orderStatus;
    }

    public byte queryOrderPaymentStatus(String orderNumber){
        byte paymentStatus = this.orderDOMapper.queryOrderPaymentStatus(orderNumber);
        return paymentStatus;
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

    public OrderDO createNewOrder(OrderDO orderDO) {
        int i = this.orderDOMapper.insertSelective(orderDO);
        if (i>0){
            return orderDO;
        }
        return null;
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
