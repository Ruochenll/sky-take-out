package com.sky.controller.user;

import com.sky.dto.DishPageQueryDTO;
import com.sky.dto.OrdersPaymentDTO;
import com.sky.dto.OrdersSubmitDTO;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.OrderService;
import com.sky.vo.OrderPaymentVO;
import com.sky.vo.OrderSubmitVO;
import com.sky.vo.OrderVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController("userOrderController")
@RequestMapping("/user/order")
@Slf4j
public class OrderController {

    @Autowired
    private OrderService orderService;

    /**
     * 用户下单
     */
    @PostMapping("/submit")
    public Result<OrderSubmitVO> submit(@RequestBody OrdersSubmitDTO ordersSubmitDTO){
        log.info("用户下单: {}", ordersSubmitDTO);

        OrderSubmitVO orderSubmitVO = orderService.submit(ordersSubmitDTO);

        return Result.success(orderSubmitVO);
    }

    /**
     *  用户订单支付 （无法申请凭证，默认点击支付即可成功）
     */
    @PutMapping("/payment")
    public Result<OrderPaymentVO> payment(@RequestBody OrdersPaymentDTO ordersPaymentDTO){
        log.info("用户订单支付: {}", ordersPaymentDTO);

        OrderPaymentVO orderPaymentVO = orderService.payment(ordersPaymentDTO);

        return Result.success(orderPaymentVO);
    }

    /**
     * 查询订单详情
     */
    @GetMapping("/orderDetail/{id}")
    public Result<OrderVO> orderDetail(@PathVariable Long id){
        log.info("查询订单详情，订单id为:{}", id);

        OrderVO orderVO = orderService.orderDetail(id);

        return Result.success(orderVO);
    }

    /**
     * 历史订单查询
     */
    @GetMapping("/historyOrders")
    public Result<PageResult<OrderVO>> historyOrders(DishPageQueryDTO dishPageQueryDTO){
        log.info("历史订单查询：{} ", dishPageQueryDTO);

        return Result.success(orderService.historyOrders(dishPageQueryDTO));
    }


}
