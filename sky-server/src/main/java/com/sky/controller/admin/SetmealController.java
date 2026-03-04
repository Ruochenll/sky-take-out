package com.sky.controller.admin;

import com.sky.dto.SetmealDTO;
import com.sky.dto.SetmealPageQueryDTO;
import com.sky.entity.Setmeal;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.SetmealService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin/setmeal")
@Slf4j
public class SetmealController {

    @Autowired
    private SetmealService setmealService;

    /**
     * 新增套餐
     */
    @PostMapping
    public Result<String> save(@RequestBody SetmealDTO setmealDTO){
        log.info("新增套餐: {}", setmealDTO.getName());

        setmealService.save(setmealDTO);

        return Result.success();
    }

    /**
     * 分页查询
     */
    @GetMapping("/page")
    public Result<PageResult<Setmeal>> pageList(SetmealPageQueryDTO setmealPageQueryDTO){
        log.info("套餐分页查询...");

        PageResult<Setmeal> list = setmealService.pageList(setmealPageQueryDTO);

        return Result.success(list);
    }

    /**
     * 根据id查询套餐
     */
    @GetMapping("/{id}")
    public Result<SetmealDTO> getById(@PathVariable Long id){
        log.info("查询套餐id为: {}", id);

        SetmealDTO setmealDTO = setmealService.getById(id);
        return Result.success(setmealDTO);
    }

    /**
     * 修改套餐
     */
    @PutMapping
    public Result<String> update(@RequestBody SetmealDTO setmealDTO){
        log.info("修改套餐: {}", setmealDTO.getName());

        setmealService.update(setmealDTO);
        return Result.success();
    }
}
