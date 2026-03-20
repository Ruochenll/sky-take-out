package com.sky.service.impl;

import com.sky.context.BaseContext;
import com.sky.dto.DishDTO;
import com.sky.dto.SetmealDTO;
import com.sky.dto.ShoppingCartDTO;
import com.sky.entity.Dish;
import com.sky.entity.Setmeal;
import com.sky.entity.ShoppingCart;
import com.sky.mapper.DishMapper;
import com.sky.mapper.SetmealMapper;
import com.sky.mapper.ShoppingCartMapper;
import com.sky.service.ShoppingCartService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
@Slf4j
public class ShoppingCartServiceImpl implements ShoppingCartService {

    @Autowired
    private ShoppingCartMapper shoppingCartMapper;
    @Autowired
    private DishMapper dishMapper;
    @Autowired
    private SetmealMapper setmealMapper;

    /**
     * 添加购物车
     * @param shoppingCartDTO
     */
    @Transactional(rollbackFor = {Exception.class})
    @Override
    public void add(ShoppingCartDTO shoppingCartDTO) {
        if(shoppingCartDTO.getDishId() != null){
            log.info("购物车添加菜品...");
            addDish(shoppingCartDTO);
        }else if(shoppingCartDTO.getSetmealId() != null){
            log.info("购物车添加套餐...");
            addSetmeal(shoppingCartDTO);
        }
    }

    /**
     * 查看购物车
     * @return
     */
    @Override
    public List<ShoppingCart> list() {
        Long userId = BaseContext.getCurrentId();
        return shoppingCartMapper.list(userId);
    }

    /**
     * 清空购物车
     */
    @Override
    public void cleanAll() {
        Long userId = BaseContext.getCurrentId();
        shoppingCartMapper.deleteAllByUserId(userId);
    }

    /**
     * 删除购物车一个菜品
     * @param shoppingCartDTO
     */
    @Override
    public void remove(ShoppingCartDTO shoppingCartDTO){
        if(shoppingCartDTO.getDishId() != null){
            log.info("购物车删除菜品...");
            removeDish(shoppingCartDTO);
        }else if(shoppingCartDTO.getSetmealId() != null){
            log.info("购物车删除套餐...");
            removeSetmeal(shoppingCartDTO);
        }
    }



    private void addDish(ShoppingCartDTO shoppingCartDTO){
        Long dishId = shoppingCartDTO.getDishId();
        Long userId = BaseContext.getCurrentId();
        String dishFlavor = shoppingCartDTO.getDishFlavor();

        ShoppingCart shoppingCart = shoppingCartMapper.selectByDishIdAndUserIdAndDishFlavor(dishId, userId, dishFlavor);
        if (shoppingCart != null) {
            Integer number = shoppingCart.getNumber();
            shoppingCart.setNumber(number + 1);
            shoppingCartMapper.update(shoppingCart);
        } else {
            DishDTO dish = dishMapper.getDishById(dishId);
            shoppingCart = ShoppingCart.builder()
                    .name(dish.getName())
                    .userId(userId)
                    .dishId(dishId)
                    .dishFlavor(dishFlavor)
                    .number(1)
                    .amount(dish.getPrice())
                    .image(dish.getImage())
                    .createTime(LocalDateTime.now())
                    .build();
            shoppingCartMapper.insert(shoppingCart);
        }
    }

    private void addSetmeal(ShoppingCartDTO shoppingCartDTO){
        Long setmealId = shoppingCartDTO.getSetmealId();
        Long userId = BaseContext.getCurrentId();
        ShoppingCart shoppingCart = shoppingCartMapper.selectBySetmealIdAndUserId(setmealId, userId);
        if (shoppingCart != null) {
            Integer number = shoppingCart.getNumber();
            shoppingCart.setNumber(number + 1);
            shoppingCartMapper.update(shoppingCart);
        } else {
            SetmealDTO setmeal = setmealMapper.getById(setmealId);
            shoppingCart = ShoppingCart.builder()
                    .name(setmeal.getName())
                    .userId(userId)
                    .setmealId(setmealId)
                    .number(1)
                    .amount(setmeal.getPrice())
                    .image(setmeal.getImage())
                    .createTime(LocalDateTime.now())
                    .build();
            shoppingCartMapper.insert(shoppingCart);
        }

    }

    private void removeDish(ShoppingCartDTO shoppingCartDTO){
        Long dishId = shoppingCartDTO.getDishId();
        Long userId = BaseContext.getCurrentId();
        String dishFlavor = shoppingCartDTO.getDishFlavor();
        ShoppingCart shoppingCart = shoppingCartMapper.selectByDishIdAndUserIdAndDishFlavor(dishId, userId, dishFlavor);
        if (shoppingCart != null) {
            Integer number = shoppingCart.getNumber();
            if (number > 1) {
                shoppingCart.setNumber(number - 1);
                shoppingCartMapper.update(shoppingCart);
            }else{
                shoppingCartMapper.deleteByDishIdAndUserIdAndDishFlavor(dishId, userId, dishFlavor);
            }
        }
    }

    private void removeSetmeal(ShoppingCartDTO shoppingCartDTO){
        Long setmealId = shoppingCartDTO.getSetmealId();
        Long userId = BaseContext.getCurrentId();
        ShoppingCart shoppingCart = shoppingCartMapper.selectBySetmealIdAndUserId(setmealId, userId);
        if (shoppingCart != null) {
            Integer number = shoppingCart.getNumber();
            if (number > 1) {
                shoppingCart.setNumber(number - 1);
                shoppingCartMapper.update(shoppingCart);
            }else{
                shoppingCartMapper.deleteBySetmealIdAndUserId(setmealId, userId);
            }
        }

    }


}
