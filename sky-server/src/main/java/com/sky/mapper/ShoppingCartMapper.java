package com.sky.mapper;

import com.sky.entity.ShoppingCart;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface ShoppingCartMapper {

    @Insert("insert into shopping_cart (name, image, user_id, dish_id, setmeal_id, dish_flavor, number, amount, create_time) VALUES" +
            " (#{name}, #{image}, #{userId}, #{dishId}, #{setmealId}, #{dishFlavor}, #{number}, #{amount}, #{createTime})")
    void insert(ShoppingCart shoppingCart);


    ShoppingCart selectByDishIdAndUserIdAndDishFlavor(Long dishId, Long userId, String dishFlavor);

    void update(ShoppingCart shoppingCart);

    @Select("select * from shopping_cart where setmeal_id = #{setmealId} and user_id = #{userId}")
    ShoppingCart selectBySetmealIdAndUserId(Long setmealId, Long userId);

    @Select("select * from shopping_cart where user_id = #{userId}")
    List<ShoppingCart> list(Long userId);

    @Delete("delete from shopping_cart where user_id = #{userId}")
    void deleteAllByUserId(Long userId);


    void deleteByDishIdAndUserIdAndDishFlavor(Long dishId, Long userId, String dishFlavor);

    @Delete("delete from shopping_cart where setmeal_id = #{setmealId} and user_id = #{userId}")
    void deleteBySetmealIdAndUserId(Long setmealId, Long userId);
}
