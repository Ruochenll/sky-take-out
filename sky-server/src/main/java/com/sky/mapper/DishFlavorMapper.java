package com.sky.mapper;

import com.sky.entity.DishFlavor;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 菜品口味关系Mapper
 */
@Mapper
public interface DishFlavorMapper {

    /**
     * 新增菜品口味数据
     */
    void insertBatch(List<DishFlavor> flavors);

    /**
     * 删除单个菜品的所有口味（推荐方式）
     */
    @Delete("delete from dish_flavor where dish_id = #{dishId}")
    void deleteByDishId(Long dishId);

    /**
     批量删除多个菜品的口味（备用）
    */
    void deleteBatchByDishIds(Long[] dishIds);


}
