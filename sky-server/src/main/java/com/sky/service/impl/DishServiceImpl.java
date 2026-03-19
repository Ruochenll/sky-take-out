package com.sky.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.entity.DishFlavor;
import com.sky.mapper.DishFlavorMapper;
import com.sky.mapper.DishMapper;
import com.sky.mapper.SetmealDishMapper;
import com.sky.result.PageResult;
import com.sky.service.DishService;
import com.sky.vo.DishItemVO;
import com.sky.vo.DishVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
public class DishServiceImpl implements DishService {

    @Autowired
    DishMapper dishMapper;
    @Autowired
    DishFlavorMapper dishFlavorMapper;


    /**
     * 菜品分页查询
     * @param dishPageQueryDTO
     * @return
     */
    @Override
    public PageResult<DishVO> dishList(DishPageQueryDTO dishPageQueryDTO){
        //设置分页参数
        PageHelper.startPage(dishPageQueryDTO.getPage(), dishPageQueryDTO.getPageSize());
        //执行查询
        List<DishVO> page = dishMapper.dishList(dishPageQueryDTO);
        //封装结果并返回
        Page<DishVO> p = (Page<DishVO>) page;

        return new PageResult<>(p.getTotal(), p.getResult());
    }

    /**
     * 起售停售
     * @param status
     * @param id
     */
    @Transactional(rollbackFor = {Exception.class})
    @Override
    public Long startOrStop(Integer status, Long id) {
        Dish dish = Dish.builder()
                .id(id)
                .status(status)
                .build();
        dishMapper.update(dish);

        return dishMapper.getDishById(id).getCategoryId();
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

    /**
     * 批量删除菜品
     * @param ids
     */
    @Transactional(rollbackFor = {Exception.class})
    @Override
    public void delete(Long[] ids) {
        //批量删除菜品数据
        dishMapper.deleteBatchByDishIds(ids);
        //批量删除菜品口味数据
        dishFlavorMapper.deleteBatchByDishIds(ids);
    }

    /**
     * 根据分类id查询菜品数据
     * @param categoryId
     * @return
     */
    @Override
    public List<DishVO> list(Integer categoryId) {

        return dishMapper.list(categoryId);
    }

    /**
     * 根据分类id查询菜品数据以及口味数据
     * @param categoryId
     * @return
     */
    @Transactional(rollbackFor = {Exception.class})
    @Override
    public List<DishVO> listWithFlavor(Integer categoryId) {
        /**
         * ---------------时间复杂度更高的原始方法--------------
         *
         * List<Dish> dishList = dishMapper.list(dish);
         *
         * List<DishVO> dishVOList = new ArrayList<>();
         *
         * for (Dish d : dishList) {
         *      DishVO dishVO = new DishVO();
         *      BeanUtils.copyProperties(d,dishVO);
         *
         *       //根据菜品id查询对应的口味
         *       List<DishFlavor> flavors = dishFlavorMapper.getByDishId(d.getId());
         *
         *       dishVO.setFlavors(flavors);
         *       dishVOList.add(dishVO);
         *       }
         *
         *    return dishVOList;
         * }
         */

        //------------------时间复杂度低的方法------------------//
        List<Dish> dishList = dishMapper.listWithFlavor(categoryId);

        if(dishList == null || dishList.isEmpty()){
            return new ArrayList<>();
        }

        // 2. 一次性查询所有菜品的口味（避免 N+1 问题）
        List<Long> dishIds = dishList.stream()
                .map(Dish::getId)
                .collect(Collectors.toList());

        List<DishFlavor> allFlavors = dishFlavorMapper.getByDishIds(dishIds);

        // 3. 将口味按菜品 ID 分组
        Map<Long, List<DishFlavor>> flavorMap = allFlavors.stream()
                .collect(Collectors.groupingBy(DishFlavor::getDishId));

        // 4. 构建结果
        List<DishVO> dishVOList = new ArrayList<>();
        for (Dish dish : dishList) {
            DishVO dishVO = new DishVO();
            BeanUtils.copyProperties(dish, dishVO);

            // 从 Map 中获取该菜品的口味
            List<DishFlavor> flavors = flavorMap.getOrDefault(dish.getId(), new ArrayList<>()); //获取该菜品的口味，如果没有则返回一个空列表
            dishVO.setFlavors(flavors);

            dishVOList.add(dishVO);
        }

        return dishVOList;
    }

}
