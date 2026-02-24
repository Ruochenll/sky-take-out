package com.sky.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.entity.DishFlavor;
import com.sky.mapper.DishFlavorMapper;
import com.sky.mapper.DishMapper;
import com.sky.result.PageResult;
import com.sky.service.DishService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
public class DishServiceImpl implements DishService {

    @Autowired
    DishMapper dishMapper;
    @Autowired
    DishFlavorMapper dishFlavorMapper;


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

    /**
     * 起售停售
     * @param status
     * @param ids
     */
    @Override
    public void startOrStop(Integer status, Long id) {
        Dish dish = Dish.builder()
                .id(id)
                .status(status)
                .build();
        dishMapper.update(dish);
    }

    /**
     * 根据id查询菜品和对应的口味
     * @param id
     * @return
     */
    @Transactional(rollbackFor = {Exception.class})  // 设置回滚
    @Override
    public DishDTO getByIdWithFlavor(Long id) {
        //1.查询菜品数据
        DishDTO dishDTO = dishMapper.getDishById(id);
        //2.查询口味数据
        List<DishFlavor> flavors = dishMapper.getFlavorsByDishId(id);
        //3.封装数据并返回
        dishDTO.setFlavors(flavors);

        return dishDTO;
    }

    /**
     * 新增菜品
     * @param dishDTO
     */
    @Transactional(rollbackFor = {Exception.class})
    @Override
    public void save(DishDTO dishDTO) {
        Dish dish = Dish.builder()
                .name(dishDTO.getName())
                .categoryId(dishDTO.getCategoryId())
                .price(dishDTO.getPrice())
                .image(dishDTO.getImage())
                .description(dishDTO.getDescription())
                .status(dishDTO.getStatus())
                .build();
        dishMapper.insert(dish);
        //获取新插入的菜品的id
        Long dishId = dish.getId();
        log.info("新插入的菜pid为：{}", dishId);

        List<DishFlavor> flavors = dishDTO.getFlavors();
        if(flavors != null && !flavors.isEmpty()){
            flavors.forEach(dishFlavor -> dishFlavor.setDishId(dish.getId()));
            dishFlavorMapper.insertBatch(flavors);
        }
    }

    /**
     * 修改菜品
     * @param dishDTO
     */
    @Transactional(rollbackFor = {Exception.class})
    @Override
    public void update(DishDTO dishDTO) {
        //创建菜品对象
        Dish dish = Dish.builder()
                .id(dishDTO.getId())
                .name(dishDTO.getName())
                .categoryId(dishDTO.getCategoryId())
                .price(dishDTO.getPrice())
                .image(dishDTO.getImage())
                .description(dishDTO.getDescription())
                .status(dishDTO.getStatus())
                .build();
        //修改菜品数据
        dishMapper.update(dish);
        //删除原有的口味数据
        dishFlavorMapper.deleteByDishId(dishDTO.getId());
        //重新插入新的口味数据
        List<DishFlavor> flavors = dishDTO.getFlavors();
        if(flavors != null && !flavors.isEmpty()){
            flavors.forEach(dishFlavor -> dishFlavor.setDishId(dishDTO.getId()));
            dishFlavorMapper.insertBatch(flavors);
        }
    }

}
