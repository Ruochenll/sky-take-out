package com.sky.service.impl;

import com.sky.constant.MessageConstant;
import com.sky.context.BaseContext;
import com.sky.dto.OrdersConfirmDTO;
import com.sky.dto.OrdersPaymentDTO;
import com.sky.dto.OrdersSubmitDTO;
import com.sky.entity.AddressBook;
import com.sky.entity.OrderDetail;
import com.sky.entity.Orders;
import com.sky.entity.ShoppingCart;
import com.sky.exception.AddressBookBusinessException;
import com.sky.mapper.AddressBookMapper;
import com.sky.mapper.OrderDetailMapper;
import com.sky.mapper.OrderMapper;
import com.sky.mapper.ShoppingCartMapper;
import com.sky.service.OrderService;
import com.sky.vo.OrderPaymentVO;
import com.sky.vo.OrderSubmitVO;
import com.sky.vo.OrderVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class OrderServiceImpl implements OrderService {

    @Autowired
    private AddressBookMapper addressBookMapper;
    @Autowired
    private OrderMapper orderMapper;
    @Autowired
    private OrderDetailMapper orderDetailMapper;
    @Autowired
    private ShoppingCartMapper shoppingCartMapper;

    /**
     * 用户下单
     *
     * @param ordersSubmitDTO
     * @return
     */
    @Transactional(rollbackFor = {Exception.class})
    @Override
    public OrderSubmitVO submit(OrdersSubmitDTO ordersSubmitDTO) {

        //处理各种业务异常（地址为空/购物车为空）
        AddressBook addressBook = addressBookMapper.getById(ordersSubmitDTO.getAddressBookId());
        if (addressBook == null){
            //抛出业务异常
            throw new AddressBookBusinessException(MessageConstant.ADDRESS_BOOK_IS_NULL);
        }
        List<ShoppingCart> shoppingCart = shoppingCartMapper.list(BaseContext.getCurrentId());
        if (shoppingCart == null || shoppingCart.isEmpty()){
            //抛出业务异常
            throw new AddressBookBusinessException(MessageConstant.SHOPPING_CART_IS_NULL);
        }

        //向订单表插入1条数据
        Orders orders = Orders.builder()
                .number(String.valueOf(UUID.randomUUID()))
                .status(Orders.PENDING_PAYMENT)
                .userId(BaseContext.getCurrentId())
                .addressBookId(ordersSubmitDTO.getAddressBookId())
                .orderTime(LocalDateTime.now())
                .payMethod(ordersSubmitDTO.getPayMethod())
                .payStatus(Orders.UN_PAID)
                .amount(ordersSubmitDTO.getAmount())
                .remark(ordersSubmitDTO.getRemark())
                .packAmount(ordersSubmitDTO.getPackAmount())
                .phone(addressBook.getPhone())
                .address(addressBook.getDetail())
                .consignee(addressBook.getConsignee())
                .estimatedDeliveryTime(ordersSubmitDTO.getEstimatedDeliveryTime())
                .deliveryStatus(ordersSubmitDTO.getDeliveryStatus())
                .tablewareNumber(ordersSubmitDTO.getTablewareNumber())
                .tablewareStatus(ordersSubmitDTO.getTablewareStatus())
                .build();

        orderMapper.insert(orders);

        //向订单明细表插入 n 条数据
        List<OrderDetail> orderDetailList = new ArrayList<>();
        for (ShoppingCart cart : shoppingCart) {
            OrderDetail orderDetail = new OrderDetail();
            BeanUtils.copyProperties(cart, orderDetail);
            orderDetail.setOrderId(orders.getId());
            orderDetailList.add(orderDetail);
        }
        orderDetailMapper.insertBatch(orderDetailList);

        //清空当前用户的购物车数据
        shoppingCartMapper.deleteAllByUserId(BaseContext.getCurrentId());

        //返回 orderSubmitVO
        return OrderSubmitVO.builder()
                .id(orders.getId())
                .orderNumber(orders.getNumber())
                .orderAmount(orders.getAmount())
                .orderTime(orders.getOrderTime())
                .build();
    }

    /**
     * 订单详情
     */
    @Transactional(rollbackFor = {Exception.class})
    @Override
    public OrderVO orderDetail(Long orderId) {

        //查询订单基本信息
        OrderVO orderVO = orderMapper.getById(orderId);
        //查询订单明细信息
        List<OrderDetail> orderDetailList = orderDetailMapper.listByOrderId(orderId);
        //设置订单明细
        orderVO.setOrderDetailList(orderDetailList);

        return orderVO;
    }

    /**
     * 用户订单支付
     */
    @Override
    public OrderPaymentVO payment(OrdersPaymentDTO ordersPaymentDTO) {

        //根据订单号查询订单
        Orders ordersDB = orderMapper.getByNumber(ordersPaymentDTO.getOrderNumber());

        // 根据订单id更新订单的状态、支付方式、支付状态、结账时间
        Orders orders = Orders.builder()
                .id(ordersDB.getId())
                .status(Orders.TO_BE_CONFIRMED)
                .payStatus(Orders.PAID)
                .checkoutTime(LocalDateTime.now())
                .build();

        //修改订单状态为待接单
        orderMapper.update(orders);

        //模拟数据
        OrderPaymentVO orderPaymentVO = OrderPaymentVO.builder()
                .nonceStr("123")
                .paySign("123")
                .timeStamp(LocalDateTime.now().toString())
                .signType("MD5")
                .packageStr("prepay_id=" + ordersPaymentDTO.getOrderNumber())
                .build();

        return orderPaymentVO;
    }
}
