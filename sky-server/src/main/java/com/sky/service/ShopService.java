package com.sky.service;

/**
 * 店铺业务接口
 */
public interface ShopService {

    /**
     * 设置营业状态
     * @return
     */

    void setStatus(Integer status);

    Integer getStatus();
}
