package com.sky.service;


import com.sky.dto.SetmealDTO;
import com.sky.dto.SetmealPageQueryDTO;
import com.sky.entity.Setmeal;
import com.sky.result.PageResult;

import java.util.List;

public interface SetmealService {


    void save(SetmealDTO setmealDTO);

    PageResult<Setmeal> pageList(SetmealPageQueryDTO setmealPageQueryDTO);

    SetmealDTO getById(Long id);

    void update(SetmealDTO setmealDTO);

    void startOrStop(Integer status, Long id);

    void delete(Long[] ids);
}
