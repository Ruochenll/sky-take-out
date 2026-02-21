package com.sky.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.mapper.DishMapper;
import com.sky.result.PageResult;
import com.sky.service.DishService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DishServiceImpl implements DishService {

    @Autowired
    DishMapper dishMapper;

    @Override
    public PageResult<DishDTO> dishList(DishPageQueryDTO dishPageQueryDTO){
        //设置分页参数
        PageHelper.startPage(dishPageQueryDTO.getPage(), dishPageQueryDTO.getPageSize());
        //执行查询
        List<DishDTO> page = dishMapper.dishList(dishPageQueryDTO);
        //封装结果并返回
        Page<DishDTO> p = (Page<DishDTO>) page;

        return new PageResult<>(p.getTotal(), p.getResult());
    }


}
