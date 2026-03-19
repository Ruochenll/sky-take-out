package com.sky.controller.user;

import com.sky.dto.DishDTO;
import com.sky.result.Result;
import com.sky.service.DishService;
import com.sky.vo.DishVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController("userDishController")
@RequestMapping("/user/dish")
public class DishController {

    @Autowired
    private DishService dishService;

    @Autowired
    private RedisTemplate redisTemplate;

    @GetMapping("/list")
    public Result<List<DishVO>> list(Integer categoryId) {
        log.info("查询菜品信息，分类id为：{}", categoryId);
        //构造redis中的key, 规则：dish_分类id
        String key = "dish_" + categoryId;

        //查询redis中是否有菜品缓存数据
        List<DishVO> dishVO = (List<DishVO>) redisTemplate.opsForValue().get(key);
        if(dishVO != null && dishVO.size() > 0){
            //存在，直接返回缓存数据
            return Result.success(dishVO);
        }

        //不存在，需要查询数据库，将查询到的菜品数据缓存到redis中
        dishVO = dishService.listWithFlavor(categoryId);
        redisTemplate.opsForValue().set(key, dishVO);

        return Result.success(dishVO);

    }


}
