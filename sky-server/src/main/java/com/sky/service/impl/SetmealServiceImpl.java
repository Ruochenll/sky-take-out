package com.sky.service.impl;


import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sky.dto.SetmealDTO;
import com.sky.dto.SetmealPageQueryDTO;
import com.sky.entity.Setmeal;
import com.sky.entity.SetmealDish;
import com.sky.mapper.CategoryMapper;
import com.sky.mapper.SetmealDishMapper;
import com.sky.mapper.SetmealMapper;
import com.sky.result.PageResult;
import com.sky.service.SetmealService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.beans.Transient;
import java.util.List;

@Service
@Slf4j
public class SetmealServiceImpl implements SetmealService {

    @Autowired
    private SetmealMapper setmealMapper;
    @Autowired
    private SetmealDishMapper setmealDishMapper;
    @Autowired
    private CategoryMapper categoryMapper;

    /**
     * 新增套餐
     * @param setmealDTO
     */
    @Override
    @Transactional(rollbackFor = {Exception.class})
    public void save(SetmealDTO setmealDTO) {
        //创建套餐对象
        Setmeal setmeal = Setmeal.builder()
                .name(setmealDTO.getName())
                .categoryId(setmealDTO.getCategoryId())
                .price(setmealDTO.getPrice())
                .image(setmealDTO.getImage())
                .description(setmealDTO.getDescription())
                .status(setmealDTO.getStatus())
                .build();

        setmealMapper.insert(setmeal);
        Long id = setmeal.getId();
        List<SetmealDish> setmealDishes = setmealDTO.getSetmealDishes();
        if(setmealDishes!=null && !setmealDishes.isEmpty()){
            setmealDishes.forEach(setmealDish -> setmealDish.setSetmealId(id));
            setmealDishMapper.insertBatch(setmealDishes);
        }
    }

    /**
     * 套餐分页查询
     */
    @Transactional(rollbackFor = {Exception.class})
    @Override
    public PageResult<Setmeal> pageList(SetmealPageQueryDTO setmealPageQueryDTO) {
        //设置分页参数
        PageHelper.startPage(setmealPageQueryDTO.getPage(), setmealPageQueryDTO.getPageSize());

        List<Setmeal> list = setmealMapper.pageList(setmealPageQueryDTO);
        //传入套餐名称
        if (list != null){
            list.forEach(setmeal -> setmeal.setCategoryName(categoryMapper.getById(setmeal.getCategoryId())));
        }

        Page<Setmeal> pageList = (Page<Setmeal>) list;

        return new PageResult<>(pageList.getTotal(), pageList.getResult());
    }

    /**
     * 根据id套餐详情查询
     */
    @Override
    public SetmealDTO getById(Long id) {
        SetmealDTO setmealDTO = setmealMapper.getById(id);
        List<SetmealDish> setmealDishes = setmealDishMapper.getBySetmealId(id);

        setmealDTO.setSetmealDishes(setmealDishes);

        return setmealDTO;
    }

    /**
     * 修改套餐
     */
    @Transactional(rollbackFor = {Exception.class})
    @Override
    public void update(SetmealDTO setmealDTO) {
        //创建套餐对象
        Setmeal setmeal = Setmeal.builder()
                .id(setmealDTO.getId())
                .name(setmealDTO.getName())
                .categoryId(setmealDTO.getCategoryId())
                .price(setmealDTO.getPrice())
                .image(setmealDTO.getImage())
                .description(setmealDTO.getDescription())
                .status(setmealDTO.getStatus())
                .build();
        //修改套餐数据
        setmealMapper.update(setmeal);
        //删除原套餐对应的菜品数据
        setmealDishMapper.deleteBySetmealId(setmealDTO.getId());
        //添加新的菜品数据
        List<SetmealDish> setmealDishes = setmealDTO.getSetmealDishes();
        if(setmealDishes!=null && !setmealDishes.isEmpty()){
            setmealDishes.forEach(setmealDish -> setmealDish.setSetmealId(setmealDTO.getId()));
            setmealDishMapper.insertBatch(setmealDishes);
        }
    }

    /**
     * 套餐启售/停售
     */
    @Override
    public void startOrStop(Integer status, Long id) {
        Setmeal setmeal = Setmeal.builder()
                .id(id)
                .status(status)
                .build();
        setmealMapper.update(setmeal);
    }

    /**
     * 删除套餐
     */
    @Transactional(rollbackFor = {Exception.class})
    @Override
    public void delete(Long[] ids) {
        //批量删除菜品数据
        setmealMapper.deleteByIds(ids);
        //批量删除菜品对应的口味数据
        setmealDishMapper.deleteByIds(ids);
    }


}
