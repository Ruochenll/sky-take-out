package com.sky.service;

import com.sky.dto.UserLoginDTO;
import com.sky.entity.User;
import com.sky.vo.UserLoginVO;

/**
 * 用户服务接口
 */
public interface UserService {

    UserLoginVO wxlogin(UserLoginDTO userLoginDTO);
}
