package com.sky.service;

import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.result.PageResult;
import com.sky.vo.DishVO;

import java.util.List;

/**
 * 菜品管理
 */
public interface DishService {


    PageResult<DishDTO> dishList(DishPageQueryDTO dishPageQueryDTO);

    void startOrStop(Integer status, Long id);

    DishDTO getByIdWithFlavor(Long id);

    void save(DishDTO dishDTO);

    void update(DishDTO dishDTO);

    void delete(Long[] ids);

    List<DishVO> listWithFlavor(Integer categoryId);

    List<DishVO> list(Integer categoryId);
}
