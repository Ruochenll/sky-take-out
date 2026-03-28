package com.sky.service;

import com.sky.dto.DishPageQueryDTO;
import com.sky.dto.OrdersPaymentDTO;
import com.sky.dto.OrdersSubmitDTO;
import com.sky.result.PageResult;
import com.sky.vo.DishVO;
import com.sky.vo.OrderPaymentVO;
import com.sky.vo.OrderSubmitVO;
import com.sky.vo.OrderVO;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface OrderService {

    OrderSubmitVO submit(OrdersSubmitDTO ordersSubmitDTO);

    OrderVO orderDetail(Long id);

    OrderPaymentVO payment(OrdersPaymentDTO ordersPaymentDTO);

    PageResult<OrderVO> historyOrders(DishPageQueryDTO dishPageQueryDTO);
}
