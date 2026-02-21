package com.sky.controller.admin;

import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.DishService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 菜品管理
 */
@RestController
@RequestMapping("/admin/dish")
@Slf4j
public class DishController {

    @Autowired
    DishService dishService;

    @GetMapping("/page")
    public Result<PageResult<DishDTO>> dishList(DishPageQueryDTO dishPageQueryDTO){
        log.info("菜品分页查询");
        PageResult<DishDTO> pageResult = dishService.dishList(dishPageQueryDTO);

        return Result.success(pageResult);
    }
}
