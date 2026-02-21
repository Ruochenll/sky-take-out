package com.sky.service;

import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.result.PageResult;

/**
 * 菜品管理
 */
public interface DishService {


    PageResult<DishDTO> dishList(DishPageQueryDTO dishPageQueryDTO);
}
