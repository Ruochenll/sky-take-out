package com.sky.controller.admin;

import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.DishService;
import com.sky.vo.DishVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.Cursor;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ScanOptions;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * 菜品管理
 */
@RestController("adminDishController")
@RequestMapping("/admin/dish")
@Slf4j
public class DishController {

    @Autowired
    DishService dishService;
    @Autowired
    RedisTemplate redisTemplate;

    /**
     * 菜品分页查询
     * @param dishPageQueryDTO
     * @return
     */
    @GetMapping("/page")
    public Result<PageResult<DishVO>> dishList(DishPageQueryDTO dishPageQueryDTO){
        log.info("菜品分页查询");
        PageResult<DishVO> pageResult = dishService.dishList(dishPageQueryDTO);

        return Result.success(pageResult);
    }

    /**
     * 菜品起售停售
     * @param status
     * @param id
     * @return
     */
    @PostMapping("/status/{status}")
    public Result startOrStop(@PathVariable Integer status, Long id){
        log.info("菜品起售停售");

        Long categroyId = dishService.startOrStop(status,id);
        //清理缓存
        cleanCache("dish_"+categroyId);

        return Result.success();
    }

    /**
     * 根据id查询菜品和对应的口味数据
     * @return
     */
    @GetMapping("/{id}")
    public Result<DishDTO> getByIdWithFlavor(@PathVariable Long id){
        log.info("根据id查询菜品和对应的口味数据...");
        DishDTO dishDTO = dishService.getByIdWithFlavor(id);
        return Result.success(dishDTO);
    }

    /**
     * 新增菜品
     * @param dishDTO
     * @return
     */
    @PostMapping
    public Result<String> save(@RequestBody DishDTO dishDTO){
        log.info("新增菜品: {}", dishDTO.getName());
        dishService.save(dishDTO);
        return Result.success();
    }

    /**
     * 修改菜品
     * @param dishDTO
     * @return
     */
    @PutMapping
    public Result<String> update(@RequestBody DishDTO dishDTO){
        log.info("修改菜品: {}", dishDTO.getName());
        dishService.update(dishDTO);
        //清理缓存
        cleanCache("dish_*");

        return Result.success();
    }

    /**
     * 批量删除菜品
     * @param ids
     * @return
     */
    @DeleteMapping
    public Result<String> delete(Long[] ids){
        log.info("批量删除菜品: {}", (Object) ids);
        dishService.delete(ids);
        //清理缓存
        cleanCache("dish_*");

        return Result.success();
    }

    /**
     * 根据分类id查询菜品数据
     */
    @GetMapping("/list")
    public Result<List<DishVO>> list(Integer categoryId) {
        log.info("根据分类id查询菜品数据: {}", categoryId);
        List<DishVO> list = dishService.list(categoryId);
        return Result.success(list);
    }


    /**
     * 清理缓存
     * @param pattern
     */
    @SuppressWarnings("unchecked")
    private void cleanCache(String pattern){
        Set<String> keys = (Set<String>) redisTemplate.execute((RedisCallback<Object>) connection -> {
            Set<String> result = new HashSet<>();
            try (Cursor<byte[]> cursor = connection.scan(ScanOptions.scanOptions()
                    .match(pattern)
                    .count(1000)
                    .build())) {

                while (cursor.hasNext()) {
                    result.add(new String(cursor.next()));
                }
            }
            return result;
        });

        if (keys != null && !keys.isEmpty()) {
            redisTemplate.delete(keys);
            log.info("清理缓存: {}", keys);
        }
    }

}
