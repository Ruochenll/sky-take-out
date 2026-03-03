package com.sky.controller.admin;

import com.sky.result.Result;
import com.sky.service.ShopService;
import com.sky.service.impl.ShopServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 店铺管理
 */
@RestController("adminShopController")
@RequestMapping("/admin/shop")
@Slf4j
public class ShopController {

    @Autowired
    private ShopService shopService;

    /**
     * 修改店铺营业状态
     */
    @PutMapping("/{status}")
    public Result<String> setStatus(@PathVariable Integer status){
        log.info("设置店铺营业状态:{}",status == 1 ? "营业中" : "打烊中");
        shopService.setStatus(status);
        return Result.success();
    }

    /**
     * 获取店铺营业状态
     */
    @GetMapping("/status")
    public Result<Integer> getStatus(){
        Integer status = shopService.getStatus();
        return Result.success(status);
    }

}
